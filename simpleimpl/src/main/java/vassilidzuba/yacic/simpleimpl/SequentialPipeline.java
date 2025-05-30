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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.AbstractPipeline;
import vassilidzuba.yacic.model.Action;
import vassilidzuba.yacic.model.ActionStatus;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.model.PipelineStatus;

@Slf4j
public class SequentialPipeline extends AbstractPipeline<SequentialPipelineConfiguration> {
	private List<Action<SequentialPipelineConfiguration>> actions = new ArrayList<>();

	public SequentialPipeline(String type) {
		setType(type);
	}

	public void addAction(Action<SequentialPipelineConfiguration> a) {
		actions.add(a);
	}

	@Override
	public boolean runNextStep(PipelineStatus<SequentialPipelineConfiguration> ps,
			SequentialPipelineConfiguration pconfig, Path logFile, List<Node> nodes, Set<String> flags) {
		var currentStep = ps.getCurrentStep();
		var oa = searchAction(currentStep);
		var ret = false;
		
		if (oa.isPresent()) {
			var start = LocalDateTime.now();
			
			var a = oa.get();
			a.setContext(getActionContext());
			a.setDataArea(getDataArea());

			if (needSkip(a, flags)) {
				log.warn("action skipped : {}", a.getId());
				pconfig.getStepEventListeners().forEach(l -> l.complete(a.getId(), getActionSeq(a.getId()), "skipped", 0));
				return hasNextAction(a.getId(), ps);
			}

			String status;
			try {
				try (var os = Files.newOutputStream(logFile, StandardOpenOption.APPEND)) {
					os.write(("*** starting action " + a.getId() + "\n").getBytes(StandardCharsets.UTF_8));
					status = a.run(pconfig, os, nodes);
					os.write(("*** action " + a.getId() + " completed with status " + status + "\n").getBytes(StandardCharsets.UTF_8));
				}
			} catch (Exception e) {
				log.error("exception during {}", a, e);
				status = "exception";
			}

			var as = new ActionStatus();
			as.setId(a.getId());
			as.setStatus(status);
			ps.getActionStatuses().add(as);

			setActionContext(a.getContext());
			if (!"ok".equals(status)) {
				ps.setStatus(a.getId() + ":" + status);
				ret = false;
			} else {
				ps.setStatus(status);
				ret = hasNextAction(a.getId(), ps);
			}
			
			var finish = LocalDateTime.now();
			var duration = (int) Duration.between(start, finish).toMillis();
			
			var st = status;
			pconfig.getStepEventListeners().forEach(l -> l.complete(a.getId(), getActionSeq(a.getId()), st, duration));
		}
		
		return ret;
	}
	
	private boolean needSkip(Action<SequentialPipelineConfiguration> a, Set<String> flags) {
		if (flags != null && a.getSkipWhen() != null && a.getSkipWhen().stream().anyMatch(flags::contains)) {
			return true;
		}
		return (a.getOnlyWhen() != null && ! a.getOnlyWhen().isEmpty() && (flags == null ||  a.getOnlyWhen().stream().allMatch(f -> ! flags.contains(f))));
	}
	
	private boolean hasNextAction(String currentStep, PipelineStatus<SequentialPipelineConfiguration> ps) {
		var nextAction = getNextAction(currentStep);
		if (!nextAction.isEmpty()) {
			ps.setCurrentStep(nextAction.get().getId());
			return true;
		} else {
			return false;
		}
	}

	private Optional<Action<SequentialPipelineConfiguration>> searchAction(String id) {
		return actions.stream().filter(a -> id.equals(a.getId())).findFirst();
	}

	private Optional<Action<SequentialPipelineConfiguration>> getNextAction(String id) {
		for (int ii = 0; ii < actions.size(); ii++) {
			if (id.equals(actions.get(ii).getId())) {
				if ((ii + 1) < actions.size()) {
					return Optional.of(actions.get(ii + 1));
				} else {
					break;
				}
			}
		}

		return Optional.empty();
	}
	

	private int getActionSeq(String id) {
		for (int ii = 0; ii < actions.size(); ii++) {
			if (id.equals(actions.get(ii).getId())) {
				return ii;
			}
		}

		return -1;
	}


	@Override
	public PipelineStatus<SequentialPipelineConfiguration> run(SequentialPipelineConfiguration pconfig, Path logFile,
			List<Node> nodes, Set<String> flags) {
		var ps = initialize(actions.get(0).getId());

		ps.setStartDate(LocalDateTime.now());

		while (runNextStep(ps, pconfig, logFile, nodes, flags))
			;

		ps.setEndDate(LocalDateTime.now());

		return ps;
	}
}
