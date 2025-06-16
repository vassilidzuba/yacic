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

package vassilidzuba.yacic.server.project;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.GlobalConfiguration;
import vassilidzuba.yacic.model.Node;

@Slf4j
class ProjectFactoryTest {

	@Test
	void test1() {
		var gc = new GlobalConfiguration();
		gc.setActionDefinitionDirectory(Path.of("config/actionDefinitions"));
		gc.setPipelineDirectory(Path.of("config/pipelines"));
		gc.setProjectDirectory(Path.of("/projects"));
		gc.setLogsDirectory(Path.of("target/logs"));
		gc.setNodes(List.of(new Node("odin", "gcc", "git")));
		gc.setMaxNbLogs(5);

		var projectDirectory = Path.of("config/projects");
		
		var projectFactory = new ProjectFactory(projectDirectory);
		
		var pc = projectFactory.loadProjectConfiguration("helloc");
		Assertions.assertEquals("helloc", pc.getProject());
		
		var project = projectFactory.createProject(pc,  gc,  "main");
		Assertions.assertNotNull(project);
	}
}
