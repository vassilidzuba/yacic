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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import lombok.SneakyThrows;

class ProjectLogResourceTest {

	@Test
	@SneakyThrows
	@DisplayName("mainline test")
	void test1() {
		Path pdir = Path.of("target/projects");
		Path dir = Path.of("target/logs");

		cleanup(pdir);
		cleanup(dir);
		
		createProject(pdir, "p1");
		createLog(dir, "p1", "p1_b0_20250402132452", "foo1");
		createLog(dir, "p1", "p1_b0_20250402232452", "foo2");
		createLog(dir, "p1", "p1_b0_20250402332452", "foo3");
		
		createLog(dir, "p1", "p1_b1_20260402132452", "bar");

		var plr = new ProjectLogResource(pdir, dir);

		Assertions.assertEquals("foo3", plr.getLog(Optional.of("p1"), Optional.empty(), Optional.empty()));
		Assertions.assertEquals("foo3", plr.getLog(Optional.of("p1"), Optional.of("main"), Optional.empty()));
		Assertions.assertEquals("foo3", plr.getLog(Optional.of("p1"), Optional.of("main"), Optional.of("1")));
		Assertions.assertEquals("foo2", plr.getLog(Optional.of("p1"), Optional.of("main"), Optional.of("2")));
		Assertions.assertEquals("foo1", plr.getLog(Optional.of("p1"), Optional.of("main"), Optional.of("3")));

		Assertions.assertEquals("bar", plr.getLog(Optional.of("p1"), Optional.of("feature/initial"), Optional.empty()));

		cleanup(pdir);
		cleanup(dir);
	}

	@SneakyThrows
	private void createLog(Path dir, String project, String logname, String content) {
		Files.createDirectories(dir.resolve(project));
		Files.writeString(dir.resolve(project).resolve(logname + ".log"), content);
	}

	@SneakyThrows
	private void createProject(Path pdir, String project) {
		Path dir = pdir.resolve(project);
		Files.createDirectories(dir);
		var projectConfig = """
{
	"project": "p1",
	"repo": "http://odin.manul.lan:3000/vassili/p1.git",
	"root": "target/projects",
	
	"pipeline": "java-build",
	"branches": [
		{"name": "main",            "dir": "b0"},
		{"name": "feature/initial", "dir": "b1"}
	],
	"flags": [],
	"properties": [
	]
	
}				
				""";
		
		Files.writeString(dir.resolve(project + ".json"), projectConfig);
	}


	@SneakyThrows
	private void createBadProject(Path pdir, String project) {
		Path dir = pdir.resolve(project);
		Files.createDirectories(dir);
		var projectConfig = """
***				
				""";
		
		Files.writeString(dir.resolve(project + ".json"), projectConfig);
	}

	@Test
	@SneakyThrows
	@DisplayName("when no project is specified")
	void test2() {
		var plr = new ProjectLogResource(null, null);
		Optional<String> empty = Optional.empty();

		var e1 = Assertions.assertThrows(WebApplicationException.class, () -> {
			 	plr.getLog(empty, empty,empty);
		    });
		Assertions.assertEquals("project not specified", e1.getMessage());
	}

	@Test
	@SneakyThrows
	@DisplayName("when project does not exist")
	void test3() {
		Path pdir = Path.of("target/projects");
		Path dir = Path.of("target/logs");

		var plr = new ProjectLogResource(pdir, dir);

		cleanup(pdir);
		cleanup(dir);

		Optional<String> empty = Optional.empty();
		Optional<String> op1 = Optional.of("p1");
		
		var e1 = Assertions.assertThrows(WebApplicationException.class, () -> {
			 	plr.getLog(op1, empty, empty);
		    });
		Assertions.assertEquals("project does not exists: p1", e1.getMessage());
	}


	@Test
	@SneakyThrows
	@DisplayName("when bad project configuration file")
	void test5() {
		Path pdir = Path.of("target/projects");
		Path dir = Path.of("target/plog");

		cleanup(pdir);
		cleanup(dir);

		createBadProject(pdir, "p1");

		var plr = new ProjectLogResource(pdir, dir);

		Optional<String> empty = Optional.empty();
		Optional<String> op1 = Optional.of("p1");
		
		var e1 = Assertions.assertThrows(WebApplicationException.class, () -> {
			 	plr.getLog(op1, empty, empty);
		    });
		Assertions.assertEquals("bad project configuration file: p1", e1.getMessage());
		
		cleanup(pdir);
		cleanup(dir);
	}


	@Test
	@SneakyThrows
	@DisplayName("when branch does not exists")
	void test6() {
		Path pdir = Path.of("target/projects");
		Path dir = Path.of("target/plog");

		cleanup(pdir);
		cleanup(dir);

		createProject(pdir, "p1");

		var plr = new ProjectLogResource(pdir, dir);

		Optional<String> empty = Optional.empty();
		Optional<String> op1 = Optional.of("p1");
		Optional<String> obranch = Optional.of("badbranch");

		var e1 = Assertions.assertThrows(WebApplicationException.class, () -> {
			 	plr.getLog(op1, obranch, empty);
		    });
		Assertions.assertEquals("no branch badbranch for project p1", e1.getMessage());
		
		cleanup(pdir);
		cleanup(dir);
	}

	
	
	@Test
	@SneakyThrows
	@DisplayName("when log is missing")
	void test99() {
		Path pdir = Path.of("target/projects");
		Path dir = Path.of("target/plog");

		cleanup(pdir);
		cleanup(dir);

		createProject(pdir, "p1");

		var plr = new ProjectLogResource(pdir, dir);

		Optional<String> empty = Optional.empty();
		Optional<String> op1 = Optional.of("p1");

		var e1 = Assertions.assertThrows(WebApplicationException.class, () -> {
			 	plr.getLog(op1, empty, empty);
		    });
		Assertions.assertEquals("log file not found", e1.getMessage());
		
		cleanup(dir);
	}

	@SneakyThrows
	private void cleanup(Path dir) {
		if (Files.isDirectory(dir)) {
			try (var paths = Files.walk(dir)) {
				paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
		}
	}
}
