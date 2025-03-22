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

import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.ActionExecutionHandle;

@Slf4j
public class BadAction1 extends BuiltinAction {

	@Override
	public String getId() {
		return "badaction1";
	}

	@Override
	public String run(SequentialPipelineConfiguration pctx) {
		log.info("running {}", getId());
		return "failure";
	}

	@Override
	public ActionExecutionHandle<SequentialPipelineConfiguration> runAsynchronously() {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public String getDescription() {
		return "bad action";
	}


	@Override
	public String run(SequentialPipelineConfiguration pctx, OutputStream os) {
		return run(pctx);
	}
}
