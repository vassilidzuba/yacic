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

import com.codahale.metrics.annotation.Timed;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.server.ServerConfiguration;
import vassilidzuba.yacic.server.api.ReloadStatus;

/**
 * Resource that reloads the pipelines and action definitions from the directories.
 */
@Slf4j
@Path("/yacic/config/reload")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigReloadResource {
	private ServerConfiguration configuration;

	/**
	 * Constructor.
	 * 
	 * @param configuration the server configuration.
	 */
	public ConfigReloadResource(ServerConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * perform the reload.
	 * @return status indicating if the reload was successful.
	 */
	@GET
	@Timed
	public ReloadStatus reload() {
		try {
			configuration.reload();
			return new ReloadStatus(true);
		} catch (Exception e) {
			log.info("exception when reloading configuration", e);
			return new ReloadStatus(false);
		}
	}
}
