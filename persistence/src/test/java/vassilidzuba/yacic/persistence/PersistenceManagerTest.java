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

package vassilidzuba.yacic.persistence;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.simpleimpl.ProjectConfiguration;

@Slf4j
class PersistenceManagerTest {
	private static PersistenceManager pm;

	@BeforeAll
	static void setup() {
		PersistenceManager.setDatabaseConfig("sa");
		pm = new PersistenceManager();
	}

	@Test
	@SneakyThrows
	void test1() {
		try (var st = Files.list(Path.of("../server/config/projects"))) {
			st.forEach(this::load);
		}
		
		var projects = pm.listProjects();
		log.info("projects : {}", projects);
		Assertions.assertTrue(projects.stream().anyMatch(p -> p.getProjectId().equals("example1")));
		var project = pm.getProject("example1");
		Assertions.assertTrue(project.isPresent());
		Assertions.assertEquals("http://odin.manul.lan:3000/vassili/example1.git", project.get().getRepo());
		var noproject= pm.getProject("nosuchproject");
		Assertions.assertFalse(noproject.isPresent());
		
		var branches = pm.listBranches("example1");
		log.info("branches of example1 : {}", branches);
		Assertions.assertTrue(branches.stream().anyMatch(p -> p.getBranchId().equals("feature/initial")));
		var branch = pm.getBranch("example1", "feature/initial");
		Assertions.assertTrue(branch.isPresent());
		Assertions.assertEquals("b1", branch.get().getBranchdir());
		var nobranch = pm.getBranch("example1", "nosuchbranch");
		Assertions.assertFalse(nobranch.isPresent());
		
		pm.storeBuild("example1", "main", "20250512160000", "OK", 100);
		pm.storeBuild("example1", "main", "20250512170000", "OK", 1300);
		pm.storeBuild("example1", "main", "20250512180000", "OK", 900);
		
		var builds = pm.listBuilds("example1", "main");
		log.info("builds of example1/main : {}", builds);
		Assertions.assertTrue(builds.stream().anyMatch(b -> b.getTimestamp().equals("20250512170000")));
		var build = pm.getBuild("example1", "main", "20250512170000");
		Assertions.assertTrue(build.isPresent());
		Assertions.assertEquals("OK", build.get().getStatus());
		Assertions.assertEquals(1300, build.get().getDuration());
		Assertions.assertEquals(2, build.get().getBuildId());
		var nobuild = pm.getBuild("example1", "main", "10000512170000");
		Assertions.assertFalse(nobuild.isPresent());

		pm.storeStep("example1", "main", "20250512170000", "clone", 1, "OK", 200);
		pm.storeStep("example1", "main", "20250512170000", "build", 1, "OK", 300);
		pm.storeStep("example1", "main", "20250512170000", "sonar", 1, "KO", 400);
		
		var steps = pm.listSteps("example1", "main", "20250512170000");
		log.info("steps of example1/main/20250512170000 : {}", steps);
		Assertions.assertTrue(steps.stream().anyMatch(s -> s.getStepId().equals("sonar")));
		var step = pm.getStep("example1", "main", "20250512170000", "build");
		Assertions.assertTrue(step.isPresent());
		Assertions.assertEquals("OK", step.get().getStatus());
		Assertions.assertEquals(300, step.get().getDuration());
		var nostep = pm.getStep("example1", "main", "20250512170000", "nosuchstep");
		Assertions.assertFalse(nostep.isPresent());
	}

	private void load(Path p) {
		var configpath = p.resolve(p.getFileName() + ".json");
		var pc = ProjectConfiguration.readProjectConfiguration(configpath);
		pm.storeProject(pc.getProject(), pc.getRepo(), pc.getBranches());
	}
}
