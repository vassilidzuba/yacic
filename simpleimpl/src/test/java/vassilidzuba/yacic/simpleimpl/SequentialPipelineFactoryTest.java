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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

class SequentialPipelineFactoryTest {

	@Test
	@SneakyThrows
	void test1() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline1.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var pconfig = new SequentialPipelineConfiguration();
			var ps = pipeline.run(pconfig, Files.createTempFile(Path.of("target"), "test", ".log"), null, null);
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test2() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline2.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var pconfig = new SequentialPipelineConfiguration();
			var ps = pipeline.run(pconfig, Files.createTempFile(Path.of("target"), "test", ".log"), null, null);
			
			Assertions.assertEquals("badaction1:failure", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test3() {
		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().put("PROJECT", "example1");
		pconf.getProperties().put("REPO", "http://odin.manul.lan:3000/vassili/example1.git");
		pconf.getProperties().put("ROOT", "/mnt/yacic");
		pconf.getProperties().put("BRANCH", "feature/initial");
		pconf.getProperties().put("VERSION", "0.0.1-SNAPSHOT");
		
		pconf.getPad().putAll(readPodmanActionDefinitions());
		
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline3-podman.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var nodes = List.of(new Node("odin", "any"));
			
			var ps = pipeline.run(pconf, Files.createTempFile(Path.of("target"), "test", ".log"), nodes, new HashSet<>());
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test4() {
		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().put("PROJECT", "example1");
		pconf.getProperties().put("REPO", "http://odin.manul.lan:3000/vassili/example1.git");
		pconf.getProperties().put("ROOT", "/mnt/yacic");
		pconf.getProperties().put("BRANCH", "feature/initial");
		pconf.getProperties().put("VERSION", "0.0.1-SNAPSHOT");
		
		pconf.getPad().putAll(readPodmanActionDefinitions());
		
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline3-podmannodocker.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var nodes = List.of(new Node("odin", "any"));
			
			var ps = pipeline.run(pconf, Files.createTempFile(Path.of("target"), "test", ".log"), nodes, Set.of("NODOCKERR"));
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}

	@SneakyThrows
	private Map<String, PodmanActionDefinition> readPodmanActionDefinitions() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/podmanactiondefinitions.xml")) {
			return PodmanActionDefinitionFactory.parse(is);
		}
	}
}
