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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

@Slf4j
class PodmanActionTest {

	@Test
	@DisplayName("mainline")
	@SneakyThrows
	void test1() {
		var node = new Node();
		node.setHost("odin");
		node.setRoles(List.of("test"));
		
		var nodes = new ArrayList<Node>();
		nodes.add(node);
		
		var pad = new PodmanActionDefinition();
		pad.setId("list");
		pad.setCommand("ls /mnt");
		pad.setRole("test");
		pad.setMode("host");
		
		var config = new SequentialPipelineConfiguration(); 
		config.getPad().put("list", pad);

		var a = new PodmanAction();
		a.setId("test");
		a.setType("list");
		
		try (var os = new ByteArrayOutputStream()) {
			var ret = a.run(config, os, nodes);
			log.info("ret : {}", ret);
			Assertions.assertEquals("ok",  ret);
			
			var logdata = new String(os.toByteArray(), StandardCharsets.UTF_8);
			log.info("log : {}", logdata);
			
			Assertions.assertTrue(logdata.contains("yacic"));
		}
	}
}
