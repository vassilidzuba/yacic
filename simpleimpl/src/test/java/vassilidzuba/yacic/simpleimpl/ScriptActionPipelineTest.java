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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;

@Slf4j
class ScriptActionPipelineTest {

	@Test
	@SneakyThrows
	void test1() {
		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().put("PROJECT", "example1");
		pconf.getProperties().put("REPO", "http://odin.manul.lan:3000/vassili/example1.git");
		pconf.getProperties().put("ROOT", "/mnt/yacic");
		pconf.getProperties().put("BRANCH", "feature/initial");
		pconf.getProperties().put("VERSION", "0.0.1-SNAPSHOT");
		
		pconf.setScriptDirectory(Path.of("src/test/resources/scripts"));
		
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline4-script.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var nodes = List.of(new Node("odin", "any"));
			
			var tempFile = Files.createTempFile(Path.of("target"), "test", ".log");
			var ps = pipeline.run(pconf, tempFile, nodes, new HashSet<>());
			
			var data = Files.readString(tempFile, StandardCharsets.UTF_8);
			
			log.info("log is \n{}", data);
			
			Assertions.assertEquals("ok", ps.getStatus());
			Assertions.assertTrue(data.contains("example1"));
			Assertions.assertTrue(data.contains("feature/initial"));
			
			Files.deleteIfExists(tempFile);
		}
	}
}
