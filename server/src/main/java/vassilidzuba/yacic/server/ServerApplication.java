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

package vassilidzuba.yacic.server;

import java.nio.file.Files;
import java.nio.file.Path;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.server.health.ConfigurationHealthCheck;
import vassilidzuba.yacic.server.resources.PipelineListResource;
import vassilidzuba.yacic.server.resources.ProjectListResource;
import vassilidzuba.yacic.server.resources.ProjectLogResource;
import vassilidzuba.yacic.server.resources.ProjectRunResource;

@Slf4j
public class ServerApplication extends Application<ServerConfiguration> {
    public static void main(String... args) throws Exception {
    	if (args.length < 2) {
    		log.error("too few arguments");
    		throw new NoConfigurationAvailable();
    	}

    	if (! Files.isReadable(Path.of(args[1]))) {
    		log.error("the configuration file is not readable");
       		throw new NoConfigurationAvailable();
    	}

        new ServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "yacic";
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        log.info("initialization");
    }


	@Override
	public void run(ServerConfiguration configuration, Environment environment) throws Exception {
		
		configuration.loadPipelines();
		configuration.loadActionDefinitions();
		
        initResources(environment.jersey(), configuration);
        
        // to run healthchecks: curl http://localhost:8081/healthcheck
        var healthCheck = new ConfigurationHealthCheck(configuration);
        environment.healthChecks().register("config", healthCheck);
	}


	private void initResources(JerseyEnvironment jersey, ServerConfiguration configuration) {
		jersey.register(new PipelineListResource(configuration.getPipelines()));
        jersey.register(new ProjectRunResource(configuration.getPipelines(), configuration.getPodmanActionDefinitions(), configuration.getProjectDirectory()));
        jersey.register(new ProjectListResource(Path.of(configuration.getProjectDirectory())));
        jersey.register(new ProjectLogResource(Path.of(configuration.getProjectDirectory())));
	}
}
