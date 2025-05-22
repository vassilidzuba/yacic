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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.podmanutil.FileAccessUtil;
import vassilidzuba.yacic.podmanutil.Podmanutil;

@Slf4j
public class PodmanAction extends AbstractAction {
	private static ObjectMapper objectMapper = new ObjectMapper();

	private Map<String, String> context = new HashMap<>();

	@Setter
	private String id;

	@Setter
	@Getter
	private Path dataArea;

	@Setter
	private String description;

	@Setter
	private String type;

	@Setter
	private boolean uselocalproperties;

	@Setter
	private String subcommand = "";
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String run(SequentialPipelineConfiguration pconfig, OutputStream os, List<Node> nodes) {
		log.info("running podman action");
		log.info("    id          : {}", id);
		log.info("    description : {}", description);
		log.info("    type        : {}", type);
		log.info("    subcommand  : {}", subcommand);
		log.info("    skipwhen    : {}", getSkipWhen().stream().collect(Collectors.joining(" ")));
		log.info("    onlywhen    : {}", getOnlyWhen().stream().collect(Collectors.joining(" ")));
		log.info("    properties  :");
		
		
		var pdef = pconfig.getPad().get(type);

		if (pdef == null) {
			log.error("podman action type unknown : {}", type);
			return "ko";
		}
	
		var properties = new HashMap<String, String>(); 
		properties.putAll(pconfig.getProperties());
		if (pdef.isUselocalproperties()) {
			addLocalProperties(properties, nodes);
		}

		properties.forEach((k,v) -> log.info("        {} : {}", k, v));
		

		String exitStatus;
		var podmanutil = new Podmanutil();
		podmanutil.addNodes(nodes);
		
		if ("host".equals(pdef.getMode())) {
			exitStatus = podmanutil.runHost(properties, pdef, subcommand, os, pdef.getRole());
		} else {
			exitStatus = podmanutil.runGeneric(properties, pdef, subcommand, os, pdef.getRole());
		}

		if ("0".equals(exitStatus)) {
			return "ok";
		} else {
			return "ko " + exitStatus;
		}
	}

	@Override
	@SneakyThrows
	public void setContext(String data) {
		if (data == null) {
			context = new HashMap<>();
		} else {
			var typeRef = new TypeReference<HashMap<String, String>>() {
			};
			context = objectMapper.readValue(data, typeRef);
		}
	}

	@Override
	@SneakyThrows
	public String getContext() {
		return objectMapper.writeValueAsString(context);
	}
	

	

	@SneakyThrows
	private void addLocalProperties(Map<String, String> properties, List<Node> nodes) {
		var projectarea = properties.get("DATAAREA") + "/" + properties.get("PROJECT");
		// wa assume that all the hosts have the data area at the same place,
		// so any host will do.
		var host = nodes.get(0).getHost();
		var localconfigdata = new FileAccessUtil().readFile(host, "podman", projectarea + "/yacic-properties.json");
		if (localconfigdata != null) {
			try (var is = new ByteArrayInputStream(localconfigdata.getBytes(StandardCharsets.UTF_8))) {
				var localconfig = ProjectConfiguration.read(is);
				properties.putAll(localconfig.getProperties());
			}
		}
	}
}
