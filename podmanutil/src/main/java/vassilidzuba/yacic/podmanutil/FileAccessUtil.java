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
import java.nio.file.Path;

import com.sshtools.agent.client.SshAgentClient;
import com.sshtools.client.ExternalKeyAuthenticator;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileAccessUtil {
	@Setter
	@Getter
	private String username = Constants.DEFAULT_USERNAME;

	@SneakyThrows
	public String readFile(String host, String username, String filepath) {
		var hostname = getHostName();
		
		log.info("hostname is {}", hostname);
				
		if ("localhost".equals(host) || host.equalsIgnoreCase(hostname) || host.startsWith(hostname + ".")) {
			var path = Path.of(filepath);
			if (Files.isReadable(path)) {
				return Files.readString(path);
			}
		} else {
			try (SshClient ssh = SshClientBuilder.create().withHostname(host).withPort(22).withUsername(username)
					.build()) {
				SshAgentClient agent = SshAgentClient.connectOpenSSHAgent(Constants.YACIC);
				ssh.authenticate(new ExternalKeyAuthenticator(agent), 30000);

				var tmpFile = Files.createTempFile(Path.of("."), "temp", ".dat");
				ssh.getFile(filepath, tmpFile.toFile());
				var s = Files.readString(tmpFile);
				Files.delete(tmpFile);
				return s;

			} catch (Exception e) {
				log.debug("exception: {}", e.getMessage());
				return null;
			}
		}

		return null;
	}

	private String getHostName() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			return System.getenv("COMPUTERNAME");
		} else  {
			return System.getenv("HOSTNAME");
		}
	}
}
