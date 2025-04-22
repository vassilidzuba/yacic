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
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import vassilidzuba.yacic.persistence.PersistenceManager;
import vassilidzuba.yacic.server.api.BuildInfo;

/**
 * Resource returning the list of the names of the projects.
 */
@Path("/yacic/build/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class BuildListResource {

	
	/**
	 * Compute the list.
	 * It(s simply the names of the subdirectories in the project directory.
	 * 
	 * @return the list of the projects.
	 */
	@GET
    @Timed
    @SneakyThrows
    public List<BuildInfo> listBuilds(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch) {
		
		var project = oproject.orElseThrow(() -> new WebApplicationException("missing project name parameter", 400));
		var branch = obranch.orElse("main");
		
		var pm = new PersistenceManager();
		
		var builds = pm.listBuilds(project, branch);
		
		return builds.stream().map(this::build2buildinfo).toList(); 
    }
	
	private BuildInfo build2buildinfo(PersistenceManager.Build build) {
		var bi = new BuildInfo();
		
		bi.setProjectId(build.getProjectId());
		bi.setBranchId(build.getBranchId());
		bi.setTimestamp(build.getTimestamp());
		bi.setStatus(build.getStatus());
		bi.setDuration(build.getDuration());
		
		return bi;
	}

}
