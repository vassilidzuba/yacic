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
import java.util.List;
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
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.podmanutil.Podmanutil;
import vassilidzuba.yacic.simpleimpl.ProjectConfiguration;

/**
 * Resource returning a file from a build. It could be used for instance
 * to retrieve the coverage result..
 */
@jakarta.ws.rs.Path("/yacic/project/get")
@Produces(MediaType.TEXT_PLAIN)
@PermitAll
@Slf4j
public class ProjectGetResource {
	private Path projectsDirectory;
	private List<Node> nodes;
	
	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the projects directory.
	 */
	public ProjectGetResource(Path projectsDirectory, List<Node> nodes) {
		this.projectsDirectory = projectsDirectory;
		this.nodes = nodes;
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
	public byte[] getFile(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch, 
			@QueryParam("file") Optional<String> ofile) {

		var project = oproject.orElseThrow(() -> new WebApplicationException("project not specified", 400));
		var file = ofile.orElseThrow(() -> new WebApplicationException("project not specified", 400));

		Path pdir = projectsDirectory.resolve(project);
		var path = pdir.resolve(project + ".json");
		if (!Files.isReadable(path)) {
			throw new WebApplicationException("project does not exists: " + project, 404);
		}

		var prconf = ProjectConfiguration.readProjectConfiguration(path);
		if (prconf == null) {
			throw new WebApplicationException("bad project configuration file: " + project, 500);
		}

		var branch = obranch.orElse("main");
		var branchdir = prconf.getBranchDir(branch)
				.orElseThrow(() -> new WebApplicationException("no branch " + branch + " for project " + project, 404));

		var projectDir = prconf.getRoot() + "/" + project + "/" + branchdir + "/" + project;
		
		var podmanutil = new Podmanutil();
		var host = nodes.get(0).getHost();
		
		var obytes = podmanutil.copyFileFromRemote(host, projectDir + "/" + file);
		return obytes.orElseThrow(() -> new WebApplicationException("file not found: " + file, 404));
	}
}