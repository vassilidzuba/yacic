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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;
import vassilidzuba.yacic.server.api.RunStatus;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

/**
 * Resource executing a pipeline on a specific project.
 */
@Path("/yacic/project/run")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class ProjectRunResource {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();
	private Map<String, PodmanActionDefinition> actionDefinitions = new HashMap<>();
	private String projectsDirectory;
	
	/**
	 * Constructor.
	 * 
	 * @param pipelines the pipeline map.
	 * @param actionDefinitions the action definition map
	 * @param projectsDirectory the projects directory
	 */
	public ProjectRunResource(Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines,
			Map<String, PodmanActionDefinition> actionDefinitions, String projectsDirectory) {
		this.pipelines.putAll(pipelines);
		this.actionDefinitions.putAll(actionDefinitions);
		this.projectsDirectory = projectsDirectory;
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
	public RunStatus run(@QueryParam("project") Optional<String> project) {
		if (project.isEmpty()) {
			return new RunStatus("noname");
		}
		java.nio.file.Path pdir = java.nio.file.Path.of(projectsDirectory).resolve(project.get());
		var path = pdir.resolve(project.get() + ".json");
		if (!Files.isReadable(path)) {
			return new RunStatus("noexist");
		}

		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
		};
		Map<String, String> map = objectMapper.readValue(Files.readAllBytes(path), typeRef);

		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().putAll(map);

		pconf.getPad().putAll(actionDefinitions);

		var pipeline = pipelines.get(map.get("pipeline"));

		var logFile =  pdir.resolve(project.get() + ".log");
		Files.deleteIfExists(logFile);
		Files.writeString(logFile,  "");
		
		var ps = pipeline.run(pconf, logFile);

		return new RunStatus(ps.getStatus());
	}
}
