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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.SneakyThrows;

class SequentialPipelineTest {

	@Test
	@SneakyThrows
	@DisplayName("mainline, all actions OK")
	void test1() {
		var p = new SequentialPipeline("seq");
		p.addAction(new Action1());
		p.addAction(new Action2());
		
		var pconfig = new SequentialPipelineConfiguration();
		var ps = p.run(pconfig, Files.createTempFile(Path.of("target"), "temp", ".log"), null, new HashSet<>());
		Assertions.assertEquals("ok", ps.getStatus());
	}
	

	@Test
	@SneakyThrows
	@DisplayName("mainline, some actions KO")
	void test2() {
		var p = new SequentialPipeline("seq");
		p.addAction(new Action1());
		p.addAction(new BadAction1());
		p.addAction(new Action2());
		
		var pconfig = new SequentialPipelineConfiguration();
		var ps = p.run(pconfig, Files.createTempFile(Path.of("target"), "temp", ".log"), null, new HashSet<>());
		Assertions.assertEquals("badaction1:failure", ps.getStatus());
	}


	@Test
	@SneakyThrows
	@DisplayName("test an action was skipped")
	void test3() {
		var p = new SequentialPipeline("seq");
		p.addAction(new Action1());
		p.addAction(new Action2());
		
		var pconfig = new SequentialPipelineConfiguration();
		var listener = new MyStepEventListener();
		pconfig.getStepEventListeners().add(listener);
		var ps = p.run(pconfig, Files.createTempFile(Path.of("target"), "temp", ".log"), null, Set.of("NOACTION2"));
		Assertions.assertEquals("ok", ps.getStatus());
		
		Assertions.assertEquals(1, listener.getSkipped().size());
		Assertions.assertEquals("action2", listener.getSkipped().get(0));
	}

	@Test
	@SneakyThrows
	@DisplayName("test an action has mandatory flag")
	void test4() {
		var p = new SequentialPipeline("seq");
		p.addAction(new Action3());
		
		var pconfig = new SequentialPipelineConfiguration();
		var listener = new MyStepEventListener();
		pconfig.getStepEventListeners().add(listener);
		var ps = p.run(pconfig, Files.createTempFile(Path.of("target"), "temp", ".log"), null, Set.of("DOACTION3"));
		Assertions.assertEquals("ok", ps.getStatus());
		
		Assertions.assertEquals(0, listener.getSkipped().size());
	}

	@Test
	@SneakyThrows
	@DisplayName("test an action hasn't mandatory flag")
	void test5() {
		var p = new SequentialPipeline("seq");
		p.addAction(new Action1());
		p.addAction(new Action3());
		
		var pconfig = new SequentialPipelineConfiguration();
		var listener = new MyStepEventListener();
		pconfig.getStepEventListeners().add(listener);
		var ps = p.run(pconfig, Files.createTempFile(Path.of("target"), "temp", ".log"), null, null);
		Assertions.assertEquals("ok", ps.getStatus());
		
		Assertions.assertEquals(1, listener.getSkipped().size());
		Assertions.assertEquals("action3", listener.getSkipped().get(0));
	}

	class MyStepEventListener implements StepEventListener {
		@Getter
		private List<String> skipped = new ArrayList<>();
		
		@Override
		public void complete(String step, int seq, String status, int duration) {
			if ("skipped".equals(status)) {
				skipped.add(step);
			}
		}
	}
}
