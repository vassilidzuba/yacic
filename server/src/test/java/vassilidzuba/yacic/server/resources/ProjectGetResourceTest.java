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

package vassilidzuba.yacic.server.resources;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;

@Slf4j
class ProjectGetResourceTest {

	@Test
	void test1()  {
		var pdir = Path.of("config/projects");
		var node = new Node("odin", "any");
		var nodes = List.of(node);
		
		var pgr = new ProjectGetResource(pdir, nodes);
		
		var data = pgr.getFile(Optional.of("hellogo"), Optional.of("feature/initial"), Optional.of("coverage.html"));
		
		var s = new String(data, StandardCharsets.UTF_8).trim();
		
		Assertions.assertTrue(s.startsWith("<!DOCTYPE html>"));
	}
}
