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
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class ProjectLogResourceTest {

	@Test
	@SneakyThrows
	@DisplayName("mainline test")
	void test1() {
		Path dir = Path.of("target/plog");

		cleanup(dir);
	
		Files.createDirectories(dir.resolve("p1"));
		Files.writeString(dir.resolve("p1").resolve("p1.log"), "foo");
		
		var plr = new ProjectLogResource(dir);
		
		var log = plr.getLog(Optional.of("p1"));
		
		Assertions.assertEquals("foo", log);
		
		cleanup(dir);
	}


	@Test
	@SneakyThrows
	@DisplayName("when log is missing")
	void test2() {
		Path dir = Path.of("target/plog");

		cleanup(dir);
	
		Files.createDirectories(dir.resolve("p1"));
		
		var plr = new ProjectLogResource(dir);
		
		var log = plr.getLog(Optional.of("p1"));
		
		Assertions.assertEquals("", log);
		
		cleanup(dir);
	}



	@Test
	@SneakyThrows
	@DisplayName("when no project is specified")
	void test3() {
		Path dir = Path.of("target/plog");

		cleanup(dir);
	
		Files.createDirectories(dir.resolve("p1"));
		
		var plr = new ProjectLogResource(dir);
		
		var log = plr.getLog(Optional.empty());
		
		Assertions.assertEquals("", log);
		
		cleanup(dir);
	}

	
	@SneakyThrows
	private void cleanup(Path dir) {
		Files.deleteIfExists(dir.resolve("p1").resolve("p1.log"));
		Files.deleteIfExists(dir.resolve("p1"));
		Files.deleteIfExists(dir);
	}
}
