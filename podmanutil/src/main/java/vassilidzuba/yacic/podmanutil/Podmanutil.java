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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.sshtools.agent.client.SshAgentClient;
import com.sshtools.client.ExternalKeyAuthenticator;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;
import com.sshtools.client.tasks.CommandTask.CommandTaskBuilder;
import com.sshtools.client.tasks.Task;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilities for the yacic application;not intended to be a generix podman
 * utilities.
 */
@Slf4j
public class Podmanutil {

	@Setter
	@Getter
	private String username = "podman";

	public String runMaven(String project, String cmd) {
		return runMaven(project, cmd, System.out);
	}

	public String runMaven(String project, String cmd, OutputStream os) {
		var exitStatus = new StringBuilder();

		try (SshClient ssh = SshClientBuilder.create().withHostname("odin").withPort(22).withUsername("podman")
				.build()) {

			var command = "podman run -it --rm --name PROJECT -v \"$HOME/.m2:/root/.m2\" -v \"$HOME/maven/PROJECT\":/usr/src/PROJECT -w /usr/src/PROJECT maven:3.9.9-amazoncorretto-21-alpine CMD;echo MAVENTERMINATION $?"
					.replace("PROJECT", project).replace("CMD", cmd);

			SshAgentClient agent = SshAgentClient.connectOpenSSHAgent("yacic");
			ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

			Task t = CommandTaskBuilder.create().withClient(ssh).withCommand(command).withAutoConsume(false)
					.onTask((task, session) -> {
						exitStatus.append(process(session.getInputStream(), os));
					}).build();

			ssh.addTask(t);
			t.waitForever();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return exitStatus.toString();
	}

	@SneakyThrows
	private String process(InputStream is, OutputStream os) {
		var result = "";
		var buffer = new char[10000];

		try (var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
				var wr = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {

			for (;;) {
				var s = rd.readLine();
				if (s == null) {
					break;
				}
				if (s.contains("MAVENTERMINATION")) {
					result = StringUtils.substringAfter(s, "MAVENTERMINATION").trim();
				}
				wr.write(s);
				wr.write(System.lineSeparator());
			}
		}

		return result;
	}
}
