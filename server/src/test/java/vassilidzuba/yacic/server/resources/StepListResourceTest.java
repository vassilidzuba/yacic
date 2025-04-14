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
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.persistence.PersistenceManager;

@Slf4j
class StepListResourceTest {

	@Test
	@SneakyThrows
	void test1() {
		var pm = new PersistenceManager();
		
		var branches = new HashMap<String, String>();
		branches.put("main", "b0");
		pm.storeProject("p101", "http://somewhere", branches);
		
		pm.storeBuild("p101", "main", "20250306122706", "OK", 1280);

		pm.storeStep("p101", "main", "20250306122706", "clone", 1, "OK", 1212);
		pm.storeStep("p101", "main", "20250306122706", "build", 1, "OK", 1519);
		
		var slr = new StepListResource();
		
		var sl = slr.listSteps(Optional.of("p101"), Optional.of("main"), Optional.of("20250306122706"));
		
		Assertions.assertEquals(2, sl.size());
		Assertions.assertEquals("clone", sl.get(0).getStepId());
		
		var json = new ObjectMapper().writeValueAsString(sl);
		
		log.info("ret: {}", json);
		
		Assertions.assertTrue(json.contains("p101"));
	}

}
