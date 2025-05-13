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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sshtools.agent.client.SshAgentClient;
import com.sshtools.client.ExternalKeyAuthenticator;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;
import com.sshtools.client.sftp.SftpClient;
import com.sshtools.client.sftp.SftpClient.SftpClientBuilder;
import com.sshtools.client.tasks.CommandTask.CommandTaskBuilder;
import com.sshtools.client.tasks.Task;
import com.sshtools.common.sftp.PosixPermissions.PosixPermissionsBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;

/**
 * Utilities for the yacic application;not intended to be a generix podman
 * utilities.
 */
@Slf4j
public class Podmanutil {
	private static final String S_EXCEPTION = "exception";

	@Setter
	@Getter
	private static boolean dryrun = false;

	@Setter
	@Getter
	private String username = Constants.DEFAULT_USERNAME;

	@Setter
	@Getter
	private List<Node> nodes;

	public Podmanutil() {
		this.nodes = new ArrayList<>();
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void addNodes(List<Node> nodes) {
		this.nodes.addAll(nodes);
	}

	public String runGeneric(Map<String, String> properties, PodmanActionDefinition pad, String subcommand,
			OutputStream os, String role) {
		log.info("in runGeneric");
		
		var setup = substitute(pad.getSetup(), properties);
		var cleanup = substitute(pad.getCleanup(), properties);
		var command = substitute(pad.getCommand(), properties);
		var subcommand2 = substitute(subcommand, properties);

		var fullcommand = setup + "podman run -it --rm " + command + " " + subcommand2 + "; echo PODMANTERMINATION $?; "
				+ cleanup;

		return run(os, fullcommand, role);
	}

	public String runHost(Map<String, String> properties, PodmanActionDefinition pad, String subcommand,
			OutputStream os, String role) {
		log.info("in runHost");
		var setup = substitute(pad.getSetup(), properties);
		var cleanup = substitute(pad.getCleanup(), properties);
		var command = substitute(pad.getCommand(), properties);
		var subcommand2 = substitute(subcommand, properties);

		var fullcommand = setup + command + " " + subcommand2 + "; echo PODMANTERMINATION $?; " + cleanup;

		return run(os, fullcommand, role);
	}
	
	@SneakyThrows
	public boolean copyFileToRemote(String host, Path input, String output) {

		try (SshClient ssh = SshClientBuilder.create().withHostname(host).withPort(22).withUsername(username).build();
		     var is = new Dos2unixFilterInputStream(Files.newInputStream(input))) {

			SshAgentClient agent = SshAgentClient.connectOpenSSHAgent(Constants.YACIC);
			ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

			try (SftpClient sftp = SftpClientBuilder.create()
					.withClient(ssh)
					.build()) {
				
				sftp.put(is, output);
				sftp.chmod(PosixPermissionsBuilder.create()
						.fromBitmask(0755).build(), output);
			}
		}
		
		return true;
	}

	public String run(OutputStream os, String fullcommand, String role) {
		log.info("  command : {}", fullcommand);

		if (dryrun) {
			return "OK";
		}

		var host = getHost(role);

		if ("localhost".equals(host)) {
			return runLocal(fullcommand, os);
		} else {
			return runRemote(host, fullcommand, os);
		}

	}

	@SneakyThrows
	private String runLocal(String fullcommand, OutputStream os) {
		var command = fullcommand.replace("; echo PODMANTERMINATION $?", "").trim();

		var processbuilder = new ProcessBuilder(completeCommand(command)).redirectErrorStream(true);

		var process = processbuilder.start();
		inheritIO(process.getInputStream(), os);

		var ret = process.waitFor();

		if (ret != 0 || process.exitValue() != 0) {
			return "" + process.exitValue();
		}

		return "0";
	}

	private List<String> completeCommand(String cmd) {
		String system = System.getProperty("os.name").toLowerCase();
		if (system.contains("win")) {
			return List.of("cmd.exe", "/C", cmd);
		} else {
			return List.of("/usr/bin/bash", "-c", cmd);
		}
	}

	private String runRemote(String host, String command, OutputStream os) {
		var exitStatus = new StringBuilder();

		try (SshClient ssh = SshClientBuilder.create().withHostname(host).withPort(22).withUsername(username).build()) {

			SshAgentClient agent = SshAgentClient.connectOpenSSHAgent(Constants.YACIC);
			ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

			Task t = CommandTaskBuilder.create().withClient(ssh).withCommand(command).withAutoConsume(false)
					.onTask((task, session) -> {
						try (var is = session.getInputStream()) {
							exitStatus.append(process(is, os));
						}
					}).build();

			ssh.addTask(t);
			t.waitForever();

		} catch (Exception e) {
			log.error(S_EXCEPTION, e);
			return S_EXCEPTION;
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

	public String getHost(String role) {
		var node = nodes.stream().filter(n -> n.getRoles().contains(role)).findAny();
		return node.orElseThrow(() -> new NoHostFoundException("no host found for role: " + role)).getHost();
	}

	private void inheritIO(final InputStream is, final OutputStream os) {
		new Thread(() -> copy(is, os)).start();
	}
	
	private void copy(final InputStream is, final OutputStream os) {
		var buffer = new byte[1024];
		for (;;) {
			try {
				var nbbytes = is.read(buffer);
				if (nbbytes < 0) {
					return;
				} else {
					os.write(buffer, 0, nbbytes);
				}
			} catch (IOException e) {
				log.error("exception when redirecting process output");
			}
		}
	}
}