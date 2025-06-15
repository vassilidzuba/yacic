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

import java.nio.file.Files;
import java.nio.file.Path;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.GlobalConfiguration;

@Slf4j
public class Initializer extends AbstractInitializer {

	@Override
	public void initialize(GlobalConfiguration glconfig) {
		if (podmanActionDefinitions.isEmpty()) {
			loadActionDefinitions(glconfig.getActionDefinitionDirectory());
		}
		if (pipelines.isEmpty()) {
			loadPipelines(glconfig.getPipelineDirectory());
		}
	}
	

	@SneakyThrows
	public static void loadActionDefinitions(Path dir) {
		try (var st = Files.list(dir)) {
			st.forEach(Initializer::loadActionDefinition);
		}
	}

	@SneakyThrows
	private static void loadActionDefinition(Path path) {
		log.info("loading {}", path);
		try (var is = Files.newInputStream(path)) {
			var pads = PodmanActionDefinitionFactory.parse(is);
			podmanActionDefinitions.putAll(pads);
		}
	}
	
	@SneakyThrows
	public static void loadPipelines(Path dir) {
		try (var st = Files.list(dir)) {
			st.filter(p -> p.getFileName().toString().endsWith(".xml")).forEach(Initializer::loadPipeline);
		}
	}
	
	@SneakyThrows
	private static void loadPipeline(Path path) {
		log.info("loading pipeline {}", path);
		try (var is = Files.newInputStream(path)) {
			var pipeline = SequentialPipelineFactory.parse(is);
			pipelines.put(pipeline.getId(), pipeline);
		}
	}



}
