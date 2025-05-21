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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Project configuration. 
 */
@Slf4j
public class ProjectConfiguration {
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Getter
	@JsonIgnore
	private Map<String, String> properties = new HashMap<>();
	@Getter
	@JsonIgnore
	private Map<String, String> branches = new HashMap<>();

	@JsonProperty("properties")
	@Getter @Setter
	private List<Property> propertiesdArray;
	@JsonProperty("branches")
	@Getter @Setter
	private List<Branch> branchesArray;
	
	@Setter @Getter
	private String project;
	@Setter @Getter
	private String repo;
	@Setter @Getter
	private String root;
	@Setter
	private String pipeline;
	@Setter
	private List<PipelineByBranch> pipelines = new ArrayList<>();
	@Setter @Getter
	private Set<String> flags = new HashSet<>();
	
	@SneakyThrows
	public static ProjectConfiguration read(InputStream is)  {
		var pc = objectMapper.readValue(is, ProjectConfiguration.class);
		
		if (pc.propertiesdArray != null) {
			pc.propertiesdArray.forEach(p -> pc.getProperties().put(p.getKey(), p.getValue()));
			pc.propertiesdArray = null;
		}
		if (pc.branchesArray != null) {
			pc.branchesArray.forEach(b -> pc.getBranches().put(b.getName(), b.getDir()));
			pc.branchesArray = null;
		}
		
		return pc;
	}
	
	public String getPipeline(String branch) {
		var op = pipelines.stream().filter(pbb -> branch.equals(pbb.getBranch())).map(PipelineByBranch::getPipeline).findFirst();
		if (op.isPresent()) {
			return op.get();
		}
		return pipeline; 
	}

	public static ProjectConfiguration readProjectConfiguration(Path path) {
		try (var is = Files.newInputStream(path)) {
			return ProjectConfiguration.read(is);
		} catch (Exception e) {
			log.error("unable to read project configuration: {}", e.getMessage());
			return null;
		}
	}


	public Optional<String> getBranchDir(String branch) {
		if (StringUtils.isBlank(branch)) {
			return getBranchDir("main");
		}
		var dir = getBranches().get(branch);
		if (dir != null) {
			return Optional.of(dir);
		} else {
			return Optional.empty();
		}
	}

	
	static class Property {
		@Setter @Getter
		private String key;
		@Setter @Getter
		private String value;
	}
	
	static class Branch {
		@Setter @Getter
		private String name;
		@Setter @Getter
		private String dir;
	}
	
	static class PipelineByBranch {
		@Setter @Getter
		private String branch; 
		@Setter @Getter
		private String pipeline; 
	}
}
