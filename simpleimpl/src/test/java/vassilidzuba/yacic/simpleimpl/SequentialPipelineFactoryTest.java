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
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

class SequentialPipelineFactoryTest {

	@Test
	@SneakyThrows
	void test1() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline1.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var ps = pipeline.run(null);
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test2() {
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline2.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var ps = pipeline.run(null);
			
			Assertions.assertEquals("badaction1:failure", ps.getStatus());
		}
	}

	@Test
	@SneakyThrows
	void test3() {
		var pconf = new SequentialPipelineConfiguration();
		pconf.getProperties().put("PROJECT", "example1");
		pconf.getProperties().put("REPO", "http://odin.manul.lan:3000/vassili/example1.git");
		
		var padClone = new PodmanActionDefinition();
		padClone.setImage("docker.io/alpine/git");
		padClone.setHost("odin");
		padClone.setUsername("podman");
		padClone.setCommand("--name clone-PROJECT -v ${HOME}:/root -v $(pwd)/yacic:/git docker.io/alpine/git");
		padClone.setSetup("rm -rf $(pwd)/yacic/PROJECT;");
		
		var padMaven = new PodmanActionDefinition();
		padMaven.setImage("maven:3.9.9-amazoncorretto-21-alpine");
		padMaven.setHost("odin");
		padMaven.setUsername("podman");
		padMaven.setCommand("--name build-PROJECT -v \"$HOME/.m2:/root/.m2\" -v \"$HOME/yacic/PROJECT\":/usr/src/PROJECT -w /usr/src/PROJECT maven:3.9.9-amazoncorretto-21-alpine");

		pconf.getPad().put("clone", padClone);
		pconf.getPad().put("maven", padMaven);
		
		var classloader = getClass().getClassLoader();
		try (var is = classloader.getResourceAsStream("pipelines/pipeline3-podman.xml")) {
			var pipeline = SequentialPipelineFactory.parse(is);
			
			var ps = pipeline.run(pconf);
			
			Assertions.assertEquals("ok", ps.getStatus());
		}
	}
}
