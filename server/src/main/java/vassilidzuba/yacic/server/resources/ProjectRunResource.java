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

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.GlobalConfiguration;
import vassilidzuba.yacic.model.ProjectConfiguration;
import vassilidzuba.yacic.model.RunStatus;
import vassilidzuba.yacic.persistence.PersistenceManager;
import vassilidzuba.yacic.server.project.ProjectFactory;

/**
 * Resource executing a pipeline on a specific project.
 */
@jakarta.ws.rs.Path("/yacic/project/run")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class ProjectRunResource {
	private GlobalConfiguration glconfig;

	private PersistenceManager pm = new PersistenceManager();
	
	/**
	 * Constructor.
	 * 
	 * @param glconfig the global configuration.
	 */
	public ProjectRunResource(GlobalConfiguration glconfig) {
		this.glconfig = glconfig;
	}


	@GET
	@Timed
	@SneakyThrows	
	public RunStatus run(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch) {
		
				
		var branch = obranch.orElse("main");
		
		if (oproject.isEmpty()) {
			throw new WebApplicationException("missing project name parameter", 400);
		}
		
		var projectName = oproject.get();
		
		var projectUtil = new ProjectFactory( glconfig.getProjectDirectory());
		
		Path pdir = glconfig.getProjectDirectory().resolve(projectName);
		var path = pdir.resolve(projectName + ".json");
		if (!Files.isReadable(path)) {
			throw new WebApplicationException("project config does not exist", 400);
		}

		var prconfig = projectUtil.loadProjectConfiguration(projectName);
		
		var obranchDir = prconfig.getBranchDir(branch);
		String branchDir = obranchDir.orElseThrow(() -> new WebApplicationException("branch is not defiuned : " + branch, 400));
		
		storeProject(prconfig);
		storeBranch(prconfig.getProject(), branch, branchDir);

		
		var project = new ProjectFactory(glconfig.getProjectDirectory()).createProject(prconfig, glconfig, branch);
		
		project.initialize(prconfig, glconfig, branch);

		return project.run();
	}
	


	private void storeProject(ProjectConfiguration prconf) {
		if (pm.getProject(prconf.getProject()).isEmpty()) {
			pm.storeProject(prconf.getProject(), prconf.getRepo(), prconf.getBranches());
		}
	}

	private void storeBranch(String project, String branch, String branchdir) {
		if (pm.getBranch(project, branch).isEmpty()) {
			pm.storeBranch(project, branch, branchdir);
		}
	}

}
