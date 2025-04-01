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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Project configuration. 
 */
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
	@Setter @Getter
	private String pipeline;
	
	@SneakyThrows
	public static ProjectConfiguration read(InputStream is)  {
		var pc = objectMapper.readValue(is, ProjectConfiguration.class);
		
		if (pc.propertiesdArray != null) {
			pc.propertiesdArray.forEach(p -> pc.getProperties().put(p.getKey(), p.getValue()));
			pc.propertiesdArray = null;
		}
		if (pc.branchesArray != null) {
			pc.branchesArray.forEach(b -> pc.getBranches().put(b.getId(), b.getName()));
			pc.branchesArray = null;
		}
		
		return pc;
	}

	
	static class Property {
		@Setter @Getter
		private String key;
		@Setter @Getter
		private String value;
	}
	
	static class Branch {
		@Setter @Getter
		private String id;
		@Setter @Getter
		private String name;
	}
}
