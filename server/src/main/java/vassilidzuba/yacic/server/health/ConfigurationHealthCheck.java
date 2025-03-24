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

package vassilidzuba.yacic.server.health;

import java.nio.file.Files;
import java.nio.file.Path;

import com.codahale.metrics.health.HealthCheck;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.server.ServerConfiguration;

@Slf4j
public class ConfigurationHealthCheck  extends HealthCheck {
	private ServerConfiguration config;
	
	public ConfigurationHealthCheck(ServerConfiguration config) {
		this.config = config;
	}

	@Override
	protected Result check() throws Exception {
		log.info("running health checks");
		
		var add = config.getActionDefinitionDirectory();
		if (! Files.isDirectory(Path.of(add))) {
			return Result.unhealthy("action definition directory doesn't exist");
		}

		var ppld = config.getPipelineDirectory();
		if (! Files.isDirectory(Path.of(ppld))) {
			return Result.unhealthy("pipeline directory doesn't exist");
		}

		var prd = config.getProjectDirectory();
		if (! Files.isDirectory(Path.of(prd))) {
			return Result.unhealthy("project directory doesn't exist");
		}

		return Result.healthy();
	}
}
