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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import vassilidzuba.yacic.server.ServerConfiguration;

class ConfigReloadResourceTest {
	private static final Path DIR = Path.of("target/crr");

	@Test
	@DisplayName("mainline test")
	@SneakyThrows
	void test1() {
		cleanup(DIR);

		Files.createDirectories(DIR.resolve("actiondefinitions"));
		Files.createDirectories(DIR.resolve("pipelines"));

		var configuration = new ServerConfiguration();
		configuration.setPipelineDirectory(DIR.resolve("pipelines").toString());
		configuration.setActionDefinitionDirectory(DIR.resolve("actiondefinitions").toString());

		Assertions.assertEquals(0, configuration.getPipelines().entrySet().size());
		Assertions.assertEquals(0, configuration.getPodmanActionDefinitions().entrySet().size());

		Files.copy(Path.of("config/pipelines/java-build.xml"), Path.of("target/crr/pipelines/java-build.xml"));
		Files.copy(Path.of("config/pipelines/go-build.xml"), Path.of("target/crr/pipelines/go-build.xml"));
		Files.copy(Path.of("config/actiondefinitions/podmanactiondefinitions.xml"),
				Path.of("target/crr/actiondefinitions/podmanactiondefinitions.xml"));

		var crr = new ConfigReloadResource(configuration);

		var status = crr.reload();

		Assertions.assertTrue(status.isOk());
		Assertions.assertEquals(2, configuration.getPipelines().entrySet().size());
		Assertions.assertEquals(17, configuration.getPodmanActionDefinitions().entrySet().size());
	}
	

	@Test
	@DisplayName("failure test")
	@SneakyThrows
	void test2() {
		cleanup(DIR);

		Files.createDirectories(DIR.resolve("actiondefinitions"));
		Files.createDirectories(DIR.resolve("pipelines"));

		var configuration = new ServerConfiguration();
		configuration.setPipelineDirectory(DIR.resolve("pipelines").toString());
		configuration.setActionDefinitionDirectory(DIR.resolve("actiondefinitions").toString());

		Files.writeString(DIR.resolve("pipelines").resolve("anything.xml"), "<foo>");
		
		var crr = new ConfigReloadResource(configuration);

		var status = crr.reload();

		Assertions.assertFalse(status.isOk());

	}

	private void cleanup(Path dir) throws IOException {
		if (Files.isDirectory(dir)) {
			try (var paths = Files.walk(dir)) {
				paths.sorted(Comparator.reverseOrder()).forEach(this::delete);
			}
		}
	}

	@SneakyThrows
	private void delete(Path p) {
		Files.delete(p);
	}
}