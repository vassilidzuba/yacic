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
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
	private static final String YACIC = "yacic";

	@Setter @Getter
	private static boolean dryrun = false;

	@Setter
	@Getter
	private String username = "podman";
	

	public String runGeneric(Map<String, String> properties, PodmanActionDefinition pdef, String subcommand) {
		return runGeneric(properties, pdef, subcommand, System.out);
	}

	public String runGeneric(Map<String, String> properties, PodmanActionDefinition pad, String subcommand, OutputStream os) {
		log.info("in runGeneric");
		var setup = substitute(pad.getSetup(), properties);
		var cleanup = substitute(pad.getCleanup(), properties);
		var command = substitute(pad.getCommand(), properties);
		var subcommand2 = substitute(subcommand, properties);

		var fullcommand = setup + "podman run -it --rm " + command + " " + subcommand2 + ";echo PODMANTERMINATION $?; " + cleanup; 
		
		log.info("  command : {}", fullcommand);
		
		if (dryrun) {
			return "OK";
		}
		
		var exitStatus = new StringBuilder();

		try (SshClient ssh = SshClientBuilder.create().withHostname("odin").withPort(22).withUsername("podman")
				.build()) {

			SshAgentClient agent = SshAgentClient.connectOpenSSHAgent(YACIC);
			ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

			Task t = CommandTaskBuilder.create().withClient(ssh).withCommand(fullcommand).withAutoConsume(false)
					.onTask((task, session) -> 
						exitStatus.append(process(session.getInputStream(), os))
					).build();

			ssh.addTask(t);
			t.waitForever();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return exitStatus.toString();
	}
	
	private String substitute(String cmd, Map<String, String> properties) {
		var command = cmd;
		if (command == null) {
			return "";
		}
		for (var e : properties.entrySet()) {
			command = command.replace(e.getKey(), e.getValue());
		}
		return command;
	}

	@SneakyThrows
	private String process(InputStream is, OutputStream os) {
		var result = "";

		try (var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

			for (;;) {
				var s = rd.readLine();
				if (s == null) {
					break;
				}
				if (s.contains("PODMANTERMINATION")) {
					result = StringUtils.substringAfter(s, "PODMANTERMINATION").trim();
				}
				os.write(s.getBytes(StandardCharsets.UTF_8));
				os.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
			}
		}

		return result;
	}
}
