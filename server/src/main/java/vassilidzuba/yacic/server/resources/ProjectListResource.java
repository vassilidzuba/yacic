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
import java.util.List;

import com.codahale.metrics.annotation.Timed;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;

/**
 * Resource returning the list of the names of the projects.
 */
@Path("/yacic/project/list")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class ProjectListResource {
	private java.nio.file.Path projectDirectory;
	
	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the list of the projects.
	 */
	public ProjectListResource(java.nio.file.Path projectDirectory) {
		this.projectDirectory = projectDirectory;
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
    public List<String> listProjects() {
		try (var st = Files.list(projectDirectory)) {
			return st.filter(Files::isDirectory).map(java.nio.file.Path::getFileName).map(java.nio.file.Path::toString).toList();
		}
    }
}
