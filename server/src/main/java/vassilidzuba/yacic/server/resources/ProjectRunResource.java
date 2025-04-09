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

package vassilidzuba.yacic.server.resources;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;
import vassilidzuba.yacic.server.api.RunStatus;
import vassilidzuba.yacic.simpleimpl.ProjectConfiguration;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

/**
 * Resource executing a pipeline on a specific project.
 */
@jakarta.ws.rs.Path("/yacic/project/run")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class ProjectRunResource {
	private Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();
	private Map<String, PodmanActionDefinition> actionDefinitions = new HashMap<>();
	private String projectsDirectory;
	private String logsDirectory;
	private int maxNbLogs;
	private List<Node> nodes;

	/**
	 * Constructor.
	 * 
	 * @param pipelines         the pipeline map.
	 * @param actionDefinitions the action definition map
	 * @param projectsDirectory the projects directory
	 * @param nodes             list of nodes
	 */
	public ProjectRunResource(Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines,
			Map<String, PodmanActionDefinition> actionDefinitions, String projectsDirectory, 
			String logsDirectory, int maxNbLogs, List<Node> nodes) {
		this.pipelines.putAll(pipelines);
		this.actionDefinitions.putAll(actionDefinitions);
		this.projectsDirectory = projectsDirectory;
		this.logsDirectory = logsDirectory;
		this.maxNbLogs = maxNbLogs;
		this.nodes = nodes;
	}

	/**
	 * Performs the run.
	 * 
	 * @param project the name of the project
	 * @return the execution status.
	 */
	@GET
	@Timed
	@SneakyThrows
	public RunStatus run(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch) {
		var branch = obranch.orElse("main");

		if (oproject.isEmpty()) {
			return new RunStatus("noname");
		}
		
		var project = oproject.get();
		
		Path pdir = Path.of(projectsDirectory).resolve(project);
		var path = pdir.resolve(project + ".json");
		if (!Files.isReadable(path)) {
			return new RunStatus("noexist");
		}

		var prconf = ProjectConfiguration.readProjectConfiguration(path);
		if (prconf == null) {
			return new RunStatus("bad project config");
		}

		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().putAll(prconf.getProperties());
		pconf.getProperties().put("PROJECT", prconf.getProject());
		pconf.getProperties().put("REPO", prconf.getRepo());
		pconf.getProperties().put("ROOT", prconf.getRoot());
		pconf.getProperties().put("BRANCH", branch);
		var branchDir = prconf.getBranchDir(branch).orElseThrow(() -> new WebApplicationException("no branch " + branch + " for project " + project, 404));
		
		pconf.getProperties().put("BRANCHDIR", branchDir);
		pconf.getProperties().put("DATAAREA",
				prconf.getRoot() + "/" + prconf.getProject() + "/" + branchDir);
		pconf.getProperties().putAll(prconf.getProperties());

		pconf.getPad().putAll(actionDefinitions);

		var pipeline = pipelines.get(prconf.getPipeline());

		var logFile = Path.of(logsDirectory).resolve(project).resolve(project + "_" + branchDir + "_" + getTimeStamp() + ".log");
		Files.createDirectories(logFile.getParent());
		
		Files.deleteIfExists(logFile);
		Files.writeString(logFile, "");

		var ps = pipeline.run(pconf, logFile, nodes, prconf.getFlags());

		cleanupLogs(project, branchDir);
		
		return new RunStatus(ps.getStatus());
	}

	@SneakyThrows
	private void cleanupLogs(String project, String branchDir) {
		var dir = Path.of(logsDirectory).resolve(project);
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

	private String getTimeStamp() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
	}
}
