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

import java.util.List;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import vassilidzuba.yacic.persistence.PersistenceManager;
import vassilidzuba.yacic.server.api.BranchInfo;
import vassilidzuba.yacic.server.api.ProjectInfo;

/**
 * Resource returning the list of the names of the projects.
 */
@Path("/yacic/project/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class ProjectListResource {

	private PersistenceManager pm = new PersistenceManager();

	
	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the list of the projects.
	 */
	public ProjectListResource() {
		//nothing to do
	}
	
	/**
	 * Compute the list.
	 * It(s simply the names of the subdirectories in the project directory.
	 * 
	 * @return the list of the projects.
	 */
	@GET
    @Timed
    @SneakyThrows
    public List<ProjectInfo> listProjects() {
		
		var projects = pm.listProjects();
		
		var lp = projects.stream().map(this::project2projectinfo).toList();
		lp.forEach(this::retriveBranches);
		
		return lp;
    }
	
	private ProjectInfo project2projectinfo(PersistenceManager.Project p) {
		var pi = new ProjectInfo();
		pi.setProjectId(p.getProjectId());
		pi.setRepo(p.getRepo());
		return pi;
	}
	
	private void retriveBranches(ProjectInfo pi) {
		var branches = pm.listBranches(pi.getProjectId());
		branches.stream().sorted((a,b) -> a.getBranchdir().compareTo(b.getBranchdir())).forEach(b -> addBranch(pi, b));
	}
	
	private void addBranch(ProjectInfo pi, PersistenceManager.Branch branch) {
		pi.getBranches().add(new BranchInfo(branch.getBranchId(), branch.getBranchdir()));
	}
}
