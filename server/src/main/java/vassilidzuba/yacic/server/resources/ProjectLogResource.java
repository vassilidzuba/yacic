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
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;
import com.sshtools.common.logger.Log;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;

/**
 * Resource returning a log of a run.
 * The log is in a file in the specific project direcory, with the name of the project and the extension <i>.log</i>.
 */
@Path("/yacic/project/log")
@Produces(MediaType.TEXT_PLAIN)
@PermitAll
public class ProjectLogResource {
	private java.nio.file.Path projectDirectory;
	
	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the projects directory.
	 */
	public ProjectLogResource(java.nio.file.Path projectDirectory) {
		this.projectDirectory = projectDirectory;
	}
	
	/**
	 * Reads and return the log.
	 * 
	 * @param project the project name.
	 * @return the text of the log
	 */
	@GET
	@Timed
	@SneakyThrows
	public String getLog(@QueryParam("project") Optional<String> project) {
		if (project.isEmpty()) {
			Log.info("missing project");
			return "";
		}
		
		var logFile = projectDirectory.resolve(project.get()).resolve(project.get() + ".log");
		if (Files.isReadable(logFile)) {
			return Files.readString(logFile, StandardCharsets.UTF_8);
		}
		
		return "";
	}
}
