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

import java.nio.charset.StandardCharsets;
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

/**
 * Resource returning a file from a build. It could be used for instance
 * to retrieve the coverage result..
 */
@jakarta.ws.rs.Path("/yacic/project/getconfig")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class ProjectGetconfigResource {
	private Path projectsDirectory;
	
	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the projects directory.
	 */
	public ProjectGetconfigResource(Path projectsDirectory) {
		this.projectsDirectory = projectsDirectory;
	}

	/**
	 * Reads and return the project configuration.
	 * 
	 * @param project the project name.
	 * @return the text of the project configuration
	 */
	@GET
	@Timed
	@SneakyThrows
	public String getConfig(@QueryParam("project") Optional<String> oproject) {

		var project = oproject.orElseThrow(() -> new WebApplicationException("project not specified", 400));

		var pdir = projectsDirectory.resolve(project);
		var path = pdir.resolve(project + ".json");
		if (!Files.isReadable(path)) {
			throw new WebApplicationException("project does not exists: " + project, 404);
		}

		return Files.readString(path, StandardCharsets.UTF_8);
	}
}