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

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import vassilidzuba.yacic.server.api.PipelineDescription;
import vassilidzuba.yacic.server.api.PipelineList;

/**
 * Resource returning the list of the names of the pipelines.
 */
@Path("/yacic/pipeline/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class PipelineListResource {
	private java.nio.file.Path pipelineDir;
	
	/**
	 * Constructor.
	 * 
	 * @param pipelines the pipeline map.
	 */
	public PipelineListResource(java.nio.file.Path pipelineDir) {
		this.pipelineDir = pipelineDir;
	}
	
	/**
	 * Compute the list of names of the pipelines.
	 * 
	 * @return the list of names. 
	 */
	@GET
    @Timed
    public PipelineList listPipelines() {
		return loadPipelinesDescription(pipelineDir);
    }
	
	@SneakyThrows
	private PipelineList loadPipelinesDescription(java.nio.file.Path dir) {
		var ret = new PipelineList();
		try (var st = Files.list(dir)) {
			st.map(this::loadPipelineDescription).forEach(pd -> ret.add(pd));
		}
		return ret;
	}
	
	private PipelineDescription loadPipelineDescription(java.nio.file.Path path) {
		var name = path.getFileName().toString();
		if (Files.isReadable(path.resolve(name + ".xml"))) {
			return new PipelineDescription(name, "simple");
		}
		if (Files.isReadable(path.resolve(name + ".kts"))) {
			return new PipelineDescription(name, "kt");
		}
		return new PipelineDescription(name, "undefined");
	}
}
