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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class ProjectConfigurationTest {

	@Test
	@SneakyThrows
	void test1() {
		try (var is = Files.newInputStream(Path.of("../server/config/projects/example1/example1.json"))) {
			var pc = ProjectConfiguration.read(is);
			
			Assertions.assertEquals("example1", pc.getProject());
			Assertions.assertEquals("http://odin.manul.lan:3000/vassili/example1.git", pc.getRepo());
			Assertions.assertEquals("/mnt/yacic", pc.getRoot());
			Assertions.assertEquals("java-build", pc.getPipeline());
			
			Assertions.assertEquals("main", pc.getBranches().get("b1"));
			Assertions.assertEquals("feature/initial", pc.getBranches().get("b2"));
			
			Assertions.assertEquals("bar", pc.getProperties().get("foo"));
		}
	}
}
