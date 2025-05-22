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

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.podmanutil.Podmanutil;

@Slf4j
public class ScriptAction  extends AbstractAction {
	@Setter 
    private String id;

	@Setter @Getter
	private String description;

	@Setter @Getter
	private String context;

	@Setter @Getter
	private Path dataArea;

	@Setter @Getter
	private String script; 

	@Setter @Getter
	private String role; 
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	@SneakyThrows
	public String run(SequentialPipelineConfiguration pconfig, OutputStream os, List<Node> nodes) {
		log.info("running script action");
		log.info("    id          : {}", id);
		log.info("    description : {}", description);
		log.info("    script      : {}", script);
		log.info("    role        : {}", role);

		log.warn("running script {} {}", id, script);
		
		var scriptsDirectory = pconfig.getScriptDirectory();
		var scriptPath = scriptsDirectory.resolve(script);
		
		if (! Files.isReadable(scriptPath)) {
			log.error("Script file doesn't exists: {}", scriptPath);
			return "** no script file **";
		}
		
		var podmanutil = new Podmanutil();
		podmanutil.addNodes(nodes);

		var host = podmanutil.getHost(role);
		
		var timestamp =  DateTimeFormatter.ofPattern("yyyyMMddHHmmssnn").format(LocalDateTime.now());
		 
		var dest = "/tmp/" + timestamp + "-" + script;

		var scriptData = Files.readString(scriptPath, StandardCharsets.UTF_8);
		scriptData = scriptData.replace("\r", "");
		for (var e : pconfig.getProperties().entrySet() ) {
			scriptData = scriptData.replace("$" + e.getKey(), e.getValue());
		}
		
		var tempFile = Files.createTempFile("script", ".sscript");
		Files.writeString(tempFile,  scriptData);
		
		if ("localhost".equals(dest)) {
			Files.copy(tempFile, Path.of(dest));
			Files.setPosixFilePermissions(Path.of(dest), Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_EXECUTE));
		} else {
			podmanutil.copyFileToRemote(host, tempFile, dest);
		}
		
		podmanutil.run(os, dest, role);
		
		Files.deleteIfExists(tempFile);
		
		return "ok";
	}
}
