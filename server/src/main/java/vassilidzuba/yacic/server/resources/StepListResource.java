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
import vassilidzuba.yacic.server.api.StepInfo;

/**
 * Resource returning the list of the names of the projects.
 */
@Path("/yacic/step/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class StepListResource {

	
	/**
	 * Compute the list.
	 * It(s simply the names of the subdirectories in the project directory.
	 * 
	 * @return the list of the projects.
	 */
	@GET
    @Timed
    @SneakyThrows
    public List<StepInfo> listSteps(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch, @QueryParam("timestamp") Optional<String> otimestamp) {
		
		var project = oproject.orElseThrow(() -> new WebApplicationException("missing project name parameter", 400));
		var branch = obranch.orElseThrow(() -> new WebApplicationException("missing branch name parameter", 400));
		var timestamp = otimestamp.orElseThrow(() -> new WebApplicationException("missing timestamp parameter", 400));
		
		var pm = new PersistenceManager();
		
		var steps = pm.listSteps(project, branch, timestamp);
		
		return steps.stream().map(this::build2stepinfo).toList(); 
    }
	
	private StepInfo build2stepinfo(PersistenceManager.Step step) {
		var si = new StepInfo();
		
		si.setProjectId(step.getProjectId());
		si.setBranchId(step.getBranchId());
		si.setTimestamp(step.getTimestamp());
		si.setStepId(step.getStepId());
		si.setSeq(step.getSeq());
		si.setStatus(step.getStatus());
		si.setDuration(step.getDuration());
		
		return si;
	}

}
