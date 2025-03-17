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

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineFactory;

@Slf4j
class OrchestratorPersistenceTest {

	@Test
	void test1() {
		var op = new OrchestratorPersistence();
		
		var pipeline = getPipeline();
		
		var ps1 = pipeline.initialize("step1");
		var ps2 = pipeline.initialize("step1");
		
		op.store(ps1);
		op.store(ps2);
		
		var ids = op.listPipelines();
		
		ids.forEach(id -> log.info("-->  {}", id));
	}
	
	@SneakyThrows
	private Pipeline<SequentialPipelineConfiguration> getPipeline() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline1.xml")) {
			return SequentialPipelineFactory.parse(is);
		}
	}

}
