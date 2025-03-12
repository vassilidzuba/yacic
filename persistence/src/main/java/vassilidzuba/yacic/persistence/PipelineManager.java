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
package vassilidzuba.yacic.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineFactory;

public final class PipelineManager {
	private Map<String, Pipeline> pipelines = new HashMap<>();
	

	@SneakyThrows
	public PipelineManager(String pipelineDirectory) {
		try (var stream = Files.list(Path.of(pipelineDirectory))) {
			stream.forEach(this::load);
		}
	}


	@SneakyThrows
	private void load(Path path) {
		try (var is = Files.newInputStream(path)) {
			var pipeline =  SequentialPipelineFactory.parse(is);
			pipelines.put(pipeline.getId(), pipeline);
		}
	}
}
