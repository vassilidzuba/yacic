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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class SequentialPipelineFactoryTest {

	@Test
	@SneakyThrows
	void test1() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline1.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var ps = pipeline.run();
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test2() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline2.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var ps = pipeline.run();
			
			Assertions.assertEquals("badaction1:failure", ps.getStatus());
		}
	}
}
