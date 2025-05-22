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
import vassilidzuba.yacic.persistence.PersistenceManager;
import vassilidzuba.yacic.simpleimpl.ProjectConfiguration;

/**
 * Resource returning a log of a build. The log is in a file in the specific
 * project directory, with the name of the project and the extension <i>.log</i>.
 */
@jakarta.ws.rs.Path("/yacic/build/log")
@Produces(MediaType.TEXT_PLAIN)
@PermitAll
@Slf4j
public class BuildLogResource {
	private Path projectsDirectory;
	private Path logsDirectory;

	/**
	 * Constructor.
	 * 
	 * @param projectDirectory the projects directory.
	 */
	public BuildLogResource(Path projectsDirectory, Path logsDirectory) {
		this.projectsDirectory = projectsDirectory;
		this.logsDirectory = logsDirectory;
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
	public String getLog(@QueryParam("project") Optional<String> oproject,
			@QueryParam("branch") Optional<String> obranch, @QueryParam("timestamp") Optional<String> otimestamp) {

		var project = oproject.orElseThrow(() -> new WebApplicationException("project not specified", 400));
		var timestamp = otimestamp.orElse(null);

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

		var dir = logsDirectory.resolve(project);

		if (Files.isDirectory(dir)) {
			var filename = project + "_" + branchdir + "_" + getTimestamp(project, branch, timestamp) + ".log";
			var log = dir.resolve(filename);
			if (Files.isReadable(log)) {
				return Files.readString(log, StandardCharsets.UTF_8);
			} else {
				throw new WebApplicationException("log file not found :" + log, 404);
			}
		}

		throw new WebApplicationException("no log directory :" + dir, 404);
	}

	private String getTimestamp(String project, String branch, String timestamp) {
		if (timestamp == null) {
			var pm = new PersistenceManager();
			var builds = pm.listBuilds(project, branch);
			if (! builds.isEmpty()) {
				return builds.get(0).getTimestamp();
			} else {
				return "xxx";
			}
		} else {
			return timestamp;
		}
	}
}