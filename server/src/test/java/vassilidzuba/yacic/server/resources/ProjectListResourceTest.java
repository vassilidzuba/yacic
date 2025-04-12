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

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.persistence.PersistenceManager;

@Slf4j
class ProjectListResourceTest {

	@Test
	@SneakyThrows
	void test1() {
		var pm = new PersistenceManager();
		
		var branches1 = new HashMap<String, String>();
		branches1.put("main", "b0");
		pm.storeProject("p1", "http://odin.manul.lan:3000/vassili/example1.git", branches1);

		var branches2 = new HashMap<String, String>();
		branches2.put("main", "b0");
		branches2.put("feature/initial", "b1");
		branches2.put("feature/somethingstrange", "b2");
		pm.storeProject("p2", "http://odin.manul.lan:3000/vassili/example1.git", branches2);
		
		
		var plr = new ProjectListResource();
		var lp = plr.listProjects();
		
		var json = new ObjectMapper().writeValueAsString(lp);
		
		log.info("ret: {}", json);
		
		Assertions.assertTrue(json.contains("p1"));
		Assertions.assertTrue(json.contains("p2"));		
	}
}
