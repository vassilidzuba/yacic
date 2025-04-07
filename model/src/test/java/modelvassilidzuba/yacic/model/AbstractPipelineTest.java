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

package modelvassilidzuba.yacic.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.AbstractPipeline;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.PipelineConfiguration;
import vassilidzuba.yacic.model.PipelineStatus;

@Slf4j
class AbstractPipelineTest {

	@Test
	void test1() {
		var mypip = new MyPipeline();
		mypip.setId("mypip");
		mypip.setCurrentStep("firststep");
		mypip.setType("my");
		
		var ps = mypip.run(null, null, null, null);
		
		log.info("ps:{}", ps);
		
		Assertions.assertEquals("mypip", ps.getId());
	}
	
	static class MyPipeline extends AbstractPipeline<PipelineConfiguration> {

		@Override
		public PipelineStatus<PipelineConfiguration> run(PipelineConfiguration pconfig, Path logFile, List<Node> nodes,
				Set<String> flags) {

			return initialize("firststep");
		}

		@Override
		public boolean runNextStep(PipelineStatus<PipelineConfiguration> ps, PipelineConfiguration pconfig,
				Path logFile, List<Node> nodes, Set<String> flags) {
			
			return false;
		}
	}
}
