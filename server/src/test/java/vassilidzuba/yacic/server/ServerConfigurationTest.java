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

package vassilidzuba.yacic.server;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

class ServerConfigurationTest {

	@Test
	@DisplayName("set/get directories")
	void test1() {
		var sc = new ServerConfiguration();
		
		sc.setPipelineDirectory("pipelinedir");
		Assertions.assertEquals("pipelinedir", sc.getPipelineDirectory());
		
		sc.setProjectDirectory("projectdir");
		Assertions.assertEquals("projectdir", sc.getProjectDirectory());
		
		sc.setActionDefinitionDirectory("actiondir");
		Assertions.assertEquals("actiondir", sc.getActionDefinitionDirectory());	
	}

	@Test
	@DisplayName("load pipelines")
	void test2() {
		var sc = new ServerConfiguration();
		sc.setPipelineDirectory("config/pipelines");
		sc.loadPipelines();
		var pipelines = sc.getPipelines();
		
		Assertions.assertNotNull(pipelines.get("java-build"));
	}


	@Test
	@DisplayName("load action definitions")
	void test3() {
		var sc = new ServerConfiguration();
		sc.setActionDefinitionDirectory("config/actiondefinitions");
		sc.loadActionDefinitions();
		var ad = sc.getPodmanActionDefinitions();
		
		Assertions.assertNotNull(ad.get("go_compile"));
	}
	
	@Test
	@DisplayName("test deserialization")
	@SneakyThrows
	void test4() {
		var path = Path.of("config/yacic.json");
		
		var config = new ObjectMapper().readValue(Files.readAllBytes(path), ServerConfiguration.class);
		config.loadPipelines();
		config.loadActionDefinitions();
		
		Assertions.assertEquals("config/pipelines",  config.getPipelineDirectory());
		Assertions.assertEquals("config/projects",  config.getProjectDirectory());
		Assertions.assertEquals("config/actiondefinitions",  config.getActionDefinitionDirectory());
		
		Assertions.assertEquals(3, config.getPipelines().keySet().size());
		Assertions.assertEquals(10, config.getPodmanActionDefinitions().keySet().size());
		Assertions.assertEquals(1, config.getNodes().size());
		Assertions.assertEquals("odin", config.getNodes().get(0).getHost());
		Assertions.assertEquals(4, config.getNodes().get(0).getRoles().size());
		Assertions.assertTrue(config.getNodes().get(0).getRoles().contains("git"));
	}
}
