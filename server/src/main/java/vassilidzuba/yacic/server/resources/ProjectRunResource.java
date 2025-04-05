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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.podmanutil.FileAccessUtil;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;
import vassilidzuba.yacic.server.api.RunStatus;
import vassilidzuba.yacic.simpleimpl.ProjectConfiguration;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

/**
 * Resource executing a pipeline on a specific project.
 */
@Path("/yacic/project/run")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class ProjectRunResource {
	private Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();
	private Map<String, PodmanActionDefinition> actionDefinitions = new HashMap<>();
	private String projectsDirectory;
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
			Map<String, PodmanActionDefinition> actionDefinitions, String projectsDirectory, List<Node> nodes) {
		this.pipelines.putAll(pipelines);
		this.actionDefinitions.putAll(actionDefinitions);
		this.projectsDirectory = projectsDirectory;
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
	public RunStatus run(@QueryParam("project") Optional<String> project,
			@QueryParam("branch") Optional<String> obranch) {
		var branch = obranch.orElse("main");

		if (project.isEmpty()) {
			return new RunStatus("noname");
		}
		java.nio.file.Path pdir = java.nio.file.Path.of(projectsDirectory).resolve(project.get());
		var path = pdir.resolve(project.get() + ".json");
		if (!Files.isReadable(path)) {
			return new RunStatus("noexist");
		}

		var prconf = readProjectConfiguration(path);
		if (prconf == null) {
			return new RunStatus("bad project config");
		}

		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().putAll(prconf.getProperties());
		pconf.getProperties().put("PROJECT", prconf.getProject());
		pconf.getProperties().put("REPO", prconf.getRepo());
		pconf.getProperties().put("ROOT", prconf.getRoot());
		pconf.getProperties().put("BRANCH", branch);
		pconf.getProperties().put("BRANCHDIR", getBranchDir(prconf, branch));
		pconf.getProperties().put("DATAAREA",
				prconf.getRoot() + "/" + prconf.getProject() + "/" + getBranchDir(prconf, branch));
		pconf.getProperties().putAll(prconf.getProperties());

		pconf.getPad().putAll(actionDefinitions);

		var pipeline = pipelines.get(prconf.getPipeline());

		var logFile = pdir.resolve(project.get() + ".log");
		Files.deleteIfExists(logFile);
		Files.writeString(logFile, "");

		var ps = pipeline.run(pconf, logFile, nodes);

		return new RunStatus(ps.getStatus());
	}

	private ProjectConfiguration readProjectConfiguration(java.nio.file.Path path) {
		try (var is = Files.newInputStream(path)) {
			return ProjectConfiguration.read(is);
		} catch (Exception e) {
			log.error("unable to read project configuration", e);
			return null;
		}
	}

	private String getBranchDir(ProjectConfiguration prconf, String branch) {
		var dir = prconf.getBranches().get(branch);
		return dir == null ? "b0" : dir;
	}
}
