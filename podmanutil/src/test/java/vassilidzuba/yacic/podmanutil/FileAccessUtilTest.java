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

package vassilidzuba.yacic.podmanutil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class FileAccessUtilTest {

	@Test
	void test1() {
		var s = new FileAccessUtil().readFile("localhost", null, "README.md");
		
		Assertions.assertNotNull(s);
	}

	@Test
	void test2() {
		var s = new FileAccessUtil().readFile("localhost", null, "nosuchfile");
		
		Assertions.assertNull(s);
	}

	@Test
	void test3() {
		var s = new FileAccessUtil().readFile("odin.manul.lan", "podman", "/mnt/yacic/app/config/yacic.yaml");
		Assertions.assertNotNull(s);
		log.info("{}", s);
	}


	@Test
	void test4() {
		var s = new FileAccessUtil().readFile("odin.manul.lan", "podman", "/mnt/yacic/app/config/nosuchfile");
		Assertions.assertNull(s);
	}
}
