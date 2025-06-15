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

import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.ws.rs.WebApplicationException;
import lombok.SneakyThrows;
import vassilidzuba.yacic.model.GlobalConfiguration;
import vassilidzuba.yacic.model.Project;
import vassilidzuba.yacic.model.ProjectConfiguration;

public class ProjectFactory {
	private Path projectsDirectory;
	
	public ProjectFactory(Path projectsDirectory) {
		this.projectsDirectory = projectsDirectory;
	}

	public ProjectConfiguration loadProjectConfiguration(String project) {
		Path pdir = projectsDirectory.resolve(project);
		var path = pdir.resolve(project + ".json");
		if (!Files.isReadable(path)) {
			throw new WebApplicationException("project config does not exist", 400);
		}

		var prconf = ProjectConfiguration.readProjectConfiguration(path);
		if (prconf == null) {
			throw new WebApplicationException("project config does not readable", 400);
		}
		
		return prconf;
	}
	
	public Project createProject(ProjectConfiguration prconfig, GlobalConfiguration glconfig, String branch) {
		var implementation = prconfig.getImplementation();
		var project = switch(implementation) {
		case "simple"-> buildSimpleProject();
		//case "kt" -> new vassilidzuba.yacic.ktimpl.KtProject();
		default -> throw new WebApplicationException("no such project implementation: " + implementation, 500);
		};
		
		project.initialize(prconfig, glconfig, branch);
		
		return project;
	}
	
	@SneakyThrows
	private Project buildSimpleProject() {
		var className = "vassilidzuba.yacic.simpleimpl.SimpleProject";
		return buildProject(className);
	}
	
	@SneakyThrows
	private Project buildKtProject() {
		var className = "vassilidzuba.yacic.ktimpl.KtProject";
		return buildProject(className);
	}

	
	@SneakyThrows
	private Project buildProject(String className) {
		var clazz = Class.forName(className);
		return (Project) clazz.getDeclaredConstructor().newInstance();
	}
}

