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

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.ActionExecutionHandle;

@Slf4j
public class Action1 extends BuiltinAction {
	@Setter @Getter
	private int sleepDuration = 3;
	
	@Override
	public String getId() {
		return "action1";
	}

	@Override
	@SneakyThrows
	public String run(SequentialPipelineConfiguration pctx) {
		log.info("starting {}", getId());
		Thread.sleep(Duration.ofSeconds(sleepDuration));
		log.info("completing {}", getId());
		return "ok";
	}

	@Override
	public ActionExecutionHandle<SequentialPipelineConfiguration> runAsynchronously() {
		throw new UnsupportedOperationException("not supportyed");
	}

	@Override
	public String getDescription() {
		return "Action One";
	}

}
