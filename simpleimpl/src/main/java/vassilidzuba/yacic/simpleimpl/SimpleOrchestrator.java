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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.Orchestrator;
import vassilidzuba.yacic.model.Pipeline;
import vassilidzuba.yacic.model.PipelineStatus;

@Slf4j
public class SimpleOrchestrator implements Orchestrator<SequentialPipelineConfiguration> {
	private List<PipelineStatus<SequentialPipelineConfiguration>> history = new ArrayList<>();
	private ExecutorService executor = Executors.newFixedThreadPool(2);

	@Override
	public void run(Pipeline<SequentialPipelineConfiguration> pipeline, SequentialPipelineConfiguration pctx, Path logFile, List<Node> nodes, Set<String> flags) {
		executor.submit(() -> execute(pipeline, pctx, logFile, nodes, flags));
	}

	public void execute(Pipeline<SequentialPipelineConfiguration> pipeline, SequentialPipelineConfiguration pctx, Path logFile, List<Node> nodes, Set<String> flags) {
		var ps = pipeline.run(pctx, logFile, nodes, flags);
		history.add(ps);
	}

	@Override
	public List<PipelineStatus<SequentialPipelineConfiguration>> getHistory() {
		return history;
	}

	@Override
	public List<PipelineStatus<SequentialPipelineConfiguration>> getHistory(String pipelineType) {
		return history.stream().filter(ps -> pipelineType.equals(ps.getPipeline().getType())).toList();
	}

	@SneakyThrows
	public boolean shutdown() {
		executor.shutdown();
		return executor.awaitTermination(10, TimeUnit.MINUTES);
	}

	public void logHistory() {
		for (var ps : history) {
			log.info("run of {}", ps.getPipeline().getType());
			log.info("    id      : {}", ps.getId());
			log.info("    desc    : {}", ps.getPipeline().getDescription());
			log.info("    start   : {}", ps.getStartDate());
			log.info("    end     : {}", ps.getEndDate());
			log.info("    status  : {}", ps.getStatus());
			log.info("    actions :");
			for (var as : ps.getActionStatuses()) {
				log.info("          {}", as);
			}
		}
	}
}
