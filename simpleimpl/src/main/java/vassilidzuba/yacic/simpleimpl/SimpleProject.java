/**
   Copyright 2025 Vassili Dzuba

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.	
**/

package vassilidzuba.yacic.simpleimpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.GlobalConfiguration;
import vassilidzuba.yacic.model.Project;
import vassilidzuba.yacic.model.ProjectConfiguration;
import vassilidzuba.yacic.model.RunStatus;
import vassilidzuba.yacic.model.exceptions.NoSuchBranchException;
import vassilidzuba.yacic.persistence.PersistenceManager;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

@Slf4j
public class SimpleProject implements Project {
	private ProjectConfiguration prconfig;
	private GlobalConfiguration glconfig;
	private String branch;
	private static Map<String, PodmanActionDefinition> podmanActionDefinitions = new HashMap<>();
	private static Map<String, SequentialPipeline> pipelines = new HashMap<>();
	private PersistenceManager pm = new PersistenceManager();
	@Setter
	private static AbstractInitializer initializer = new Initializer();

	@Override
	public void initialize(ProjectConfiguration prconfig, GlobalConfiguration glconfig, String branch) {
		this.prconfig = prconfig;
		this.glconfig = glconfig;
		this.branch = branch;

		initialize(glconfig);
	}
	
	public static void initialize(GlobalConfiguration glconfig) {
		initializer.initialize(glconfig);
		podmanActionDefinitions = AbstractInitializer.getPodmanActionDefinitions();
		pipelines = AbstractInitializer.getPipelines();
	}
	

	@Override
	@SneakyThrows
	public RunStatus run() {
		var timestamp = getTimeStamp();
		var start = LocalDateTime.now();

		var branchDir = prconfig.getBranchDir(branch).orElseThrow(() -> new NoSuchBranchException(branch, prconfig.getProject()));
		
		
		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().putAll(prconfig.getProperties());
		pconf.getProperties().put("PROJECT", prconfig.getProject());
		pconf.getProperties().put("REPO", prconfig.getRepo());
		pconf.getProperties().put("ROOT", prconfig.getRoot());
		pconf.getProperties().put("BRANCHNAME", branch);
		
		pconf.getProperties().put("BRANCHDIR", branchDir);
		pconf.getProperties().put("DATAAREA",
				prconfig.getRoot() + "/" + prconfig.getProject() + "/" + branchDir);
		pconf.getProperties().put("BUILDID", Integer.toString(pm.getNextBuildId(prconfig.getProject(), branch)));
		pconf.getProperties().putAll(prconfig.getProperties());
		
		pconf.getPad().putAll(podmanActionDefinitions);

		var pipeline = pipelines.get(prconfig.getPipeline(branch));
		
		var logFile = glconfig.getLogsDirectory().resolve(prconfig.getProject()).resolve(prconfig.getProject() + "_" + branchDir + "_" + timestamp + ".log");
		Files.createDirectories(logFile.getParent());
		
		Files.deleteIfExists(logFile);
		Files.writeString(logFile, "");

		pconf.getStepEventListeners().add(new StepEndListener(prconfig.getProject(), branch, timestamp));
		
		var ps = pipeline.run(pconf, logFile, glconfig.getNodes(), prconfig.getFlags());

		cleanupLogs(prconfig.getProject(), branchDir, glconfig.getMaxNbLogs());
		
		var status = ps.getStatus();
		
		var finish = LocalDateTime.now();
		var duration = Duration.between(start, finish).toMillis();

		
		storeBuild(prconfig.getProject(), branch, timestamp, status, (int) duration);
		
		return new RunStatus(prconfig.getProject(), branch, timestamp, status, (int) duration, pipeline.getId());
	}
	private String getTimeStamp() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
	}
	

	@SneakyThrows
	private void cleanupLogs(String project, String branchDir, int maxNbLogs) {
		var dir = glconfig.getLogsDirectory().resolve(project);
		try (var st = Files.list(dir)) {
			List<Path> logs = st.filter(p -> p.getFileName().toString().startsWith(project + "_" + branchDir)).sorted().toList();
			logs.forEach(p -> log.info("log {}", p));
			if (logs.size() > maxNbLogs) {
				var deleted = logs.subList(0,logs.size() - maxNbLogs);
				deleted.forEach(this::delete);
			}
		}
	}
	
	
	@SneakyThrows
	private void delete(Path path) {
		Files.delete(path);
	}
	
	private void storeBuild(String project, String branch, String timestamp, String status, int duration) {
		pm.storeBuild(project, branch, timestamp, status, duration);		
	}

	
	class StepEndListener implements StepEventListener {
		private String project;
		private String branch;
		private String timestamp; 
		
		public StepEndListener(String project, String branch, String timestamp) {
			this.project = project;
			this.branch = branch;
			this.timestamp = timestamp;
		}

		@Override
		public void complete(String step, int seq, String status, int duration) {
			pm.storeStep(project, branch, timestamp, step, seq, status, duration);
		}
	}
}
