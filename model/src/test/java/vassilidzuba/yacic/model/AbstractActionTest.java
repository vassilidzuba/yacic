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

package vassilidzuba.yacic.model;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AbstractActionTest {

	@Test
	void test1() {
		var a = new MyAction();
		a.setContext("{}");
		a.setDataArea(Path.of("/ùnt/yacic"));
		a.setDescription("my action");
		a.setId("test");
		
		Assertions.assertEquals("test", a.getId());
		Assertions.assertEquals("{}", a.getContext());
		Assertions.assertEquals("my action", a.getDescription());
		Assertions.assertEquals("yacic", a.getDataArea().getFileName().toString());
		Assertions.assertEquals(1, a.getOnlyWhen().size());
		Assertions.assertEquals(2, a.getSkipWhen().size());
	}
	
	static class MyAction extends AbstractAction<PipelineConfiguration> {

		@Override
		public String run(PipelineConfiguration pconfig, OutputStream os, List<Node> nodes) {
			return "ok";
		}

		@Override
		public Set<String> getSkipWhen() {
			return Set.of("S1", "S2");
		}

		@Override
		public Set<String> getOnlyWhen() {
			return Set.of("O1");
		}
		
	}
}
