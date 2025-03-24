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

package vassilidzuba.yacic.server.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PipelineListTest {

	@Test
	void test1() {
		var pl= new PipelineList();
		
		pl.add("pip1");
		
		Assertions.assertEquals(1, pl.size());
		Assertions.assertEquals("pip1", pl.get(0));
	}
}
