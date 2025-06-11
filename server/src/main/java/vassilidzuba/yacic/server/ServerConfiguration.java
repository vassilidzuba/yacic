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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.core.Configuration;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;
import vassilidzuba.yacic.simpleimpl.PodmanActionDefinitionFactory;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineFactory;

public class ServerConfiguration extends Configuration {
	@NotEmpty
	private String pipelineDirectory;

	@NotEmpty
	private String projectDirectory;

	@JsonIgnore
	@Getter
	private Map<String, Pipeline<SequentialPipelineConfiguration>> pipelines = new HashMap<>();

	@NotEmpty
	private String actionDefinitionDirectory;

	@NotEmpty
	private String logsDirectory;

	@NotEmpty
	private String authenticationFile;

	private int maxNbLogs;

	private List<Node> nodes;
	private DatabaseConfig database;
	
	@JsonIgnore
	@Getter
	private Map<String, PodmanActionDefinition> podmanActionDefinitions = new HashMap<>();

	@JsonProperty
	public String getPipelineDirectory() {
		return pipelineDirectory;
	}
	
	@JsonProperty
	public void setPipelineDirectory(String pipelineDirectory) {
		this.pipelineDirectory = pipelineDirectory;
	}

	@SneakyThrows
	public void loadPipelines() {
		try (var st = Files.list(Path.of(getPipelineDirectory()))) {
			st.filter(p -> p.getFileName().toString().endsWith(".xml")).forEach(this::loadPipeline);
		}
	}
	
	@SneakyThrows
	private void loadPipeline(Path path) {
		try (var is = Files.newInputStream(path)) {
			var pipeline = SequentialPipelineFactory.parse(is);
			pipelines.put(pipeline.getId(), pipeline);
		}
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

	@SneakyThrows
	public void loadActionDefinitions() {
		try (var st = Files.list(Path.of(getActionDefinitionDirectory()))) {
			st.forEach(this::loadActionDefinition);
		}
	}

	@SneakyThrows
	private void loadActionDefinition(Path path) {
		try (var is = Files.newInputStream(path)) {
			var pads = PodmanActionDefinitionFactory.parse(is);
			podmanActionDefinitions.putAll(pads);
		}
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
		loadPipelines();
		loadActionDefinitions();
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
