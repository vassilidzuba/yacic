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

package vassilidzuba.yacic.persistence;

import java.io.OutputStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.ActionExecutionHandle;
import vassilidzuba.yacic.simpleimpl.BuiltinAction;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

@Slf4j
public class Action2 extends BuiltinAction {

	@Override
	public String getId() {
		return "action2";
	}

	@Override
	@SneakyThrows
	public String run(SequentialPipelineConfiguration pctx) {
		log.info("starting {}", getId());
		log.info("completing {}", getId());
		return "ok";
	}

	@Override
	public ActionExecutionHandle<SequentialPipelineConfiguration> runAsynchronously() {
		throw new UnsupportedOperationException("not supportyed");
	}

	@Override
	public String getDescription() {
		return "Action Two";
	}

	@Override
	public String run(SequentialPipelineConfiguration pctx, OutputStream os) {
		return run(pctx);
	}
}
