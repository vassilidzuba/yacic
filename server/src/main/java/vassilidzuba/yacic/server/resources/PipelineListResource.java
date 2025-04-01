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

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.server.api.PipelineList;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

/**
 * Resource returning the list of the names of the pipelines.
 */
@Path("/yacic/pipeline/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class PipelineListResource {
	private Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();
	
	/**
	 * Constructor.
	 * 
	 * @param pipelines the pipeline map.
	 */
	public PipelineListResource(Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines) {
		this.pipelines.putAll(pipelines);
	}
	
	/**
	 * Compute the list of names of the pipelines.
	 * 
	 * @return the list of names. 
	 */
	@GET
    @Timed
    public PipelineList listPipelines() {
		var pipelineList = new PipelineList();
		pipelineList.addAll(this.pipelines.keySet());
		return pipelineList;
    }
}
