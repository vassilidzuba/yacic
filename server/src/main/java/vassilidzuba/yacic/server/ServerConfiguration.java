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

package vassilidzuba.yacic.server;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.core.Configuration;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import vassilidzuba.yacic.model.Node;

public class ServerConfiguration extends Configuration {
	@NotEmpty
	private String pipelineDirectory;

	@NotEmpty
	private String projectDirectory;

	@NotEmpty
	private String actionDefinitionDirectory;

	@NotEmpty
	private String logsDirectory;

	@NotEmpty
	private String authenticationFile;

	private int maxNbLogs;

	private List<Node> nodes;
	private DatabaseConfig database;

	@JsonProperty
	public String getPipelineDirectory() {
		return pipelineDirectory;
	}
	
	@JsonProperty
	public void setPipelineDirectory(String pipelineDirectory) {
		this.pipelineDirectory = pipelineDirectory;
	}

	@JsonProperty
	public String getActionDefinitionDirectory() {
		return actionDefinitionDirectory;
	}
	
	@JsonProperty
	public void setActionDefinitionDirectory(String actionDefinitionDirectory) {
		this.actionDefinitionDirectory = actionDefinitionDirectory;
	}


	@JsonProperty
	public String geAuthenticationFile() {
		return authenticationFile;
	}
	
	@JsonProperty
	public void setAuthenticationFile(String authenticationFile) {
		this.authenticationFile = authenticationFile;
	}

	@JsonProperty
	public String getProjectDirectory() {
		return projectDirectory;
	}

	@JsonProperty
	public void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}
	
	@JsonProperty
	public String getLogsDirectory() {
		return logsDirectory;
	}

	@JsonProperty
	public void setLogsDirectory(String logsDirectory) {
		this.logsDirectory = logsDirectory;
	}
	
	@JsonProperty
	public int getMaxNbLogs() {
		return maxNbLogs;
	}

	@JsonProperty
	public void setMaxNbLogs(int maxNbLogs) {
		this.maxNbLogs = maxNbLogs;
	}

	@JsonProperty
	public List<Node> getNodes() {
		return nodes;
	}
	
	@JsonProperty
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	@JsonProperty("database")
	public DatabaseConfig getDatatabaseConfig() {
		return database;
	}
	
	@JsonProperty("database")
	public void setDatabaseConfig(DatabaseConfig database) {
		this.database = database;
	}
	
	public void reload() {
//		loadPipelines();
//		loadActionDefinitions();
	}
	
	public static class DatabaseConfig {
		@Setter @Getter
		private String url;
		@Setter @Getter
		private String user;
		@Setter @Getter
		private String password;
	}
}
