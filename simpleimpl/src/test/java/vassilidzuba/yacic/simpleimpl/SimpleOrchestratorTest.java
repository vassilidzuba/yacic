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

class SimpleOrchestratorTest {

	@Test
	void test1() {
		var o = new SimpleOrchestrator();

		var p = new SequentialPipeline("seq1");
		p.addAction(new Action1());
		p.addAction(new Action2());
		o.run(p);

		var p2 = new SequentialPipeline("seq2");
		p2.addAction(new Action1());
		p2.addAction(new BadAction1());
		p2.addAction(new Action2());
		o.run(p2);
		
		var ostatus = o.shutdown();
		
		Assertions.assertTrue(ostatus);
		
		var h1 = o.getHistory("seq1");
		
		Assertions.assertEquals(1, h1.size());
		
		Assertions.assertEquals("ok", h1.get(0).getStatus());
		Assertions.assertEquals("seq1", h1.get(0).getPipeline().getType());
		Assertions.assertNotNull(h1.get(0).getStartDate());
		Assertions.assertNotNull(h1.get(0).getEndDate());
		
		var h2 = o.getHistory("seq2");
		
		Assertions.assertEquals("badaction1:failure", h2.get(0).getStatus());
		Assertions.assertEquals("seq2", h2.get(0).getPipeline().getType());
		Assertions.assertNotNull(h2.get(0).getStartDate());
		Assertions.assertNotNull(h2.get(0).getEndDate());
	}
}
