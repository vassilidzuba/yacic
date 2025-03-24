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

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Pipeline;

class SimulatedPipelineTest {

	@Test
	@SneakyThrows
	@DisplayName("simulate a simple java build pipeline")
	void testMainline() {
		var o = new SimpleOrchestrator();
		var pipeline = getPipeline();
		
		var dataArea = Path.of("target/dataarea");
		Files.createDirectories(dataArea);
		pipeline.setDataArea(dataArea);
		
		o.run(pipeline, null, Files.createTempFile(Path.of("target"), "temp", ".log"));
		var ostatus = o.shutdown();
		
		Assertions.assertTrue(ostatus);
		
		o.logHistory();
	}
	
	@SneakyThrows
	private Pipeline<SequentialPipelineConfiguration> getPipeline() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/simulatedpipeline.xml")) {
			return SequentialPipelineFactory.parse(is);
		}
	}
}
