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

import java.nio.file.Files;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sshtools.agent.client.SshAgentClient;
import com.sshtools.client.ExternalKeyAuthenticator;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;
import com.sshtools.client.tasks.CommandTask.CommandTaskBuilder;
import com.sshtools.client.tasks.Task;
import com.sshtools.common.util.IOUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;

@Slf4j
class PodmanutilTest {

	@Test
	@SneakyThrows
	void test1() {
		Assertions.assertDoesNotThrow(() -> {
			try (SshClient ssh = SshClientBuilder.create().withHostname("odin.manul.lan").withPort(22).withUsername("podman")
					.build()) {

				var cmd = "curl --unix-socket $XDG_RUNTIME_DIR/podman/podman.sock 'http://d/v5.0.0/libpod/images/json'";

				SshAgentClient agent = SshAgentClient.connectOpenSSHAgent("MyApp");
				ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

				Task t = CommandTaskBuilder.create().withClient(ssh).withCommand(cmd).withAutoConsume(false)
						.onTask((task, session) -> {
							IOUtils.copy(session.getInputStream(), System.out);
						}).build();

				ssh.addTask(t);
				t.waitFor(60000L);
			}
		});
	}

	@Test
	void test2() {
		var properties = new HashMap<String, String>();
		
		var pad = new PodmanActionDefinition();
		pad.setCommand("docker.io/library/debian:stable");
		
		var podmanutil = new Podmanutil();
		podmanutil.addNode(new Node("odin", "shell"));
		
		var result = podmanutil.runGeneric(properties, pad, "whoami", System.out, "shell");
		Assertions.assertEquals("0", result);
	}

	@Test
	void test3() {
		var properties = new HashMap<String, String>();
		
		var pad = new PodmanActionDefinition();

		var podmanutil = new Podmanutil();
		podmanutil.addNode(new Node("odin", "shell"));

		var result = podmanutil.runHost(properties, pad, "whoami", System.out, "shell");
		Assertions.assertEquals("0", result);
	}


	@Test
	void test4() {
		var properties = new HashMap<String, String>();
		
		var pad = new PodmanActionDefinition();

		var podmanutil = new Podmanutil();
		podmanutil.addNode(new Node("localhost", "shell"));

		var result = podmanutil.runHost(properties, pad, "hostname && echo foo", System.out, "shell");
		Assertions.assertEquals("0", result);
	}


	@Test
	@SneakyThrows
	void test5() {
		var podmanutil = new Podmanutil();
		podmanutil.addNode(new Node("localhost", "shell"));

		var path = Files.createTempFile("tst", ".txt");
		Files.writeString(path, "xxx");
		
		var remotePath = "/tmp/foo.txt";
		
		var result = podmanutil.copyFileToRemote("odin", path, remotePath);
		Assertions.assertTrue(result);
		
		var data = podmanutil.copyFileFromRemote("odin", remotePath);
		
		Assertions.assertEquals("xxx", new String(data.get()));
		
		Files.deleteIfExists(path);
	}
}
