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
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class ProjectListResourceTest {

	@Test
	@SneakyThrows
	void test1() {
		Path dir = Path.of("target/prr");

		Files.deleteIfExists(dir.resolve("p1"));
		Files.deleteIfExists(dir.resolve("p2"));
		Files.deleteIfExists(dir);
	
		var plr = new ProjectListResource(dir);
		
		Files.createDirectories(dir);
		
		var projects = plr.listProjects();
		Assertions.assertTrue(projects.isEmpty());
		
		Files.createDirectories(dir.resolve("p1"));
		projects = plr.listProjects();
		Assertions.assertEquals(1, projects.size());
		Assertions.assertEquals("p1", projects.get(0));
		
		Files.createDirectories(dir.resolve("p2"));
		projects = plr.listProjects();
		Assertions.assertEquals(2, projects.size());
		Assertions.assertEquals("p1", projects.get(0));
		Assertions.assertEquals("p2", projects.get(1));
	}
}
