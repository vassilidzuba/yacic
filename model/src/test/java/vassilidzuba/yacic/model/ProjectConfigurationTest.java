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

package vassilidzuba.yacic.model;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProjectConfigurationTest {

	@Test
	void test1() {
		var pc = loadProjectConfiguration(Path.of("../server/config/projects"), "hellogroovy");
		
		Assertions.assertEquals("simple", pc.getImplementation());
		Assertions.assertEquals("java-release", pc.getPipeline("main"));
		Assertions.assertEquals("hellogroovy", pc.getProject());
		Assertions.assertEquals("http://odin.manul.lan:3000/vassili/hellogroovy.git", pc.getRepo());
		Assertions.assertEquals("/mnt/yacic", pc.getRoot());
		Assertions.assertEquals("b0", pc.getBranchDir("main").get());
		Assertions.assertEquals("b0", pc.getBranches().get("main"));
		Assertions.assertEquals(1, pc.getFlags().size());
		Assertions.assertTrue(pc.getFlags().contains("NODOCKER"));
		Assertions.assertEquals("gitea", pc.getProperties().get("SCM"));
	}
	
	
	private ProjectConfiguration loadProjectConfiguration(Path projectsDirectory, String project) {
		Path pdir = projectsDirectory.resolve(project);
		var path = pdir.resolve(project + ".json");
		return ProjectConfiguration.readProjectConfiguration(path);
	}
}
