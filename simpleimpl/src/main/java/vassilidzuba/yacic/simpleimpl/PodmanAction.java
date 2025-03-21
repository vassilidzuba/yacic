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

package vassilidzuba.yacic.simpleimpl;

import java.nio.file.Path;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Action;
import vassilidzuba.yacic.model.ActionExecutionHandle;
import vassilidzuba.yacic.podmanutil.Podmanutil;

@Slf4j
public class PodmanAction implements Action<SequentialPipelineConfiguration>  {
	@Setter
    private String id;
	
	@Setter
	private String description;

	@Setter
	private String type;

	@Setter
	private String subcommand = "";


	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String run(SequentialPipelineConfiguration pconfig) {
		log.info("running podman action");
		log.info("    id          : {}", id);
		log.info("    description : {}", description);
		log.info("    type        : {}", type);
		log.info("    subcommand  : {}", subcommand);
		
		var properties = pconfig.getProperties();
		var pdef = pconfig.getPad().get(type);
		
		if (pdef == null) {
			log.error("podman action type unknown : {}", type);
			return "ko";
		}
		
		String exitStatus;
		if ("host".equals(pdef.getMode())) {
			exitStatus = new Podmanutil().runHost(properties, pdef, subcommand, System.out);
		} else {
			exitStatus = new Podmanutil().runGeneric(properties, pdef, subcommand, System.out);
		}
		
		if ("0".equals(exitStatus)) {
			return "ok";
		} else {
			return "ko " + exitStatus;
		}
	}

	@Override
	public ActionExecutionHandle<SequentialPipelineConfiguration> runAsynchronously() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataArea(Path data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Path getDataArea() {
		// TODO Auto-generated method stub
		return null;
	}

}
