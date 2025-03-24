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

package vassilidzuba.yacic.server.health;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import vassilidzuba.yacic.server.ServerConfiguration;

class ConfigurationHealthCheckTest {

	@Test
	@SneakyThrows
	void test1() {
		var sc = new ServerConfiguration();
		
		var projectDirectory = "target/projectDirectory";
		var pipelineDirectory = "target/pipelineDirectory";
		var actionDefinitionDirectory = "target/actionDefinitionDirectory";
		
		removeDirectories(projectDirectory, pipelineDirectory, actionDefinitionDirectory);
		
		sc.setActionDefinitionDirectory(actionDefinitionDirectory);
		sc.setPipelineDirectory(pipelineDirectory);
		sc.setProjectDirectory(projectDirectory);
		
		var cc = new ConfigurationHealthCheck(sc);
		
		Assertions.assertFalse(cc.check().isHealthy());
		Assertions.assertEquals("action definition directory doesn't exist", cc.check().getMessage());
		
		Files.createDirectory(Path.of(actionDefinitionDirectory));
				
		Assertions.assertFalse(cc.check().isHealthy());
		Assertions.assertEquals("pipeline directory doesn't exist", cc.check().getMessage());
		
		Files.createDirectory(Path.of(pipelineDirectory));
				
		Assertions.assertFalse(cc.check().isHealthy());
		Assertions.assertEquals("project directory doesn't exist", cc.check().getMessage());
		
		Files.createDirectory(Path.of(projectDirectory));
				
		Assertions.assertTrue(cc.check().isHealthy());

		removeDirectories(projectDirectory, pipelineDirectory, actionDefinitionDirectory);
	}

	private void removeDirectories(String projectDirectory, String pipelineDirectory, String actionDefinitionDirectory)
			throws IOException {
		Files.deleteIfExists(Path.of(projectDirectory));
		Files.deleteIfExists(Path.of(pipelineDirectory));
		Files.deleteIfExists(Path.of(actionDefinitionDirectory));
	}
}
