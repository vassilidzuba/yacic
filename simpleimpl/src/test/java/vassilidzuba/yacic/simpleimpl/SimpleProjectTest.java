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
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.GlobalConfiguration;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.ProjectConfiguration;
import vassilidzuba.yacic.persistence.PersistenceManager;

@Slf4j
class SimpleProjectTest {

	@Test
	@DisplayName("mainline")
	@SneakyThrows
	void test1() {
		var gc = new GlobalConfiguration();
		gc.setActionDefinitionDirectory(Path.of("../server/config/actionDefinitions"));
		gc.setPipelineDirectory(Path.of("../server/config/pipelines"));
		gc.setProjectDirectory(Path.of("../server/config/projects"));
		gc.setLogsDirectory(Path.of("target/logs"));
		gc.setNodes(List.of(new Node("odin", "gcc", "git")));
		gc.setMaxNbLogs(5);
		
		Files.createDirectories(Path.of("target/logs"));
		var pd = loadProjectConfiguration(gc.getProjectDirectory(), "helloc");
		
		var sp = new SimpleProject();
		
		var pm = Mockito.mock(PersistenceManager.class);
		
		sp.initialize(pd, gc, "main");
		sp.setPm(pm);
		
		var runStatus = sp.run();
		
		Assertions.assertEquals("ok",  runStatus.getStatus());
		Assertions.assertEquals("helloc",  runStatus.getProjectId());
	}
	
	private ProjectConfiguration loadProjectConfiguration(Path projectsDirectory, String project) {
		Path pdir = projectsDirectory.resolve(project);
		var path = pdir.resolve(project + ".json");
		return ProjectConfiguration.readProjectConfiguration(path);
	}

}
