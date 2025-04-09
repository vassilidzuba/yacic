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

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;
import vassilidzuba.yacic.simpleimpl.BuiltinAction;
import vassilidzuba.yacic.simpleimpl.SequentialPipeline;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

@Slf4j
class ProjectRunResourceTest {

	@Test
	@SneakyThrows
	void test1() {
		Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();
		Map<String, PodmanActionDefinition> actionDefinitions = new HashMap<>();

		var action = new SpecialAction();
		action.setId("start");
		
		var pipeline = new SequentialPipeline("sequential");
		pipeline.setId("testpipeline");

		pipeline.addAction(action);

		pipelines.put(pipeline.getId(), pipeline);
		
		String projectDirectory = "target/projects";
		String logsDirectory = "target/logs";

		var prr = new ProjectRunResource(pipelines, actionDefinitions, projectDirectory, logsDirectory, 3, null);

		Files.createDirectories(Path.of(projectDirectory).resolve("test"));
		Files.writeString(Path.of(projectDirectory).resolve("test").resolve("test.json"), """
				
				{"pipeline": "testpipeline",
	             "branches": [
		               {"name": "main",            "dir": "b0"},
		               {"name": "feature/initial", "dir": "b1"}
	              ]
                }				
				""");

		var status = prr.run(Optional.of("test"), Optional.empty());

		log.info("status: {}", status);
		
		Assertions.assertEquals("ok", status.getStatus());
	}

	class SpecialAction extends BuiltinAction {
		@Override
		public String run(SequentialPipelineConfiguration pctx, OutputStream os, List<Node> nodes) {
			log.info("run specialaction");
			return "ok";
		}
	}
}
