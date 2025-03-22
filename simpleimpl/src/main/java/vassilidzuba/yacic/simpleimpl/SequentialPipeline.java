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
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.AbstractPipeline;
import vassilidzuba.yacic.model.Action;
import vassilidzuba.yacic.model.ActionStatus;
import vassilidzuba.yacic.model.PipelineStatus;

@Slf4j
public class SequentialPipeline extends AbstractPipeline<SequentialPipelineConfiguration> {
	private static AtomicInteger seqnum = new AtomicInteger(1);

	private List<Action<SequentialPipelineConfiguration>> actions = new ArrayList<>();
	private int num;

	public SequentialPipeline(String type) {
		this.num = seqnum.getAndIncrement();
		setType(type);
	}

	public void addAction(Action<SequentialPipelineConfiguration> a) {
		actions.add(a);
	}

	@Override
	public boolean runNextStep(PipelineStatus<SequentialPipelineConfiguration> ps,
			SequentialPipelineConfiguration pconfig, Path logFile) {
		var currentStep = ps.getCurrentStep();
		var oa = searchAction(currentStep);
		if (oa.isPresent()) {
			var a = oa.get();
			a.setContext(getActionContext());
			a.setDataArea(getDataArea());
			String status;
			try {
				if (logFile == null) {
					status = a.run(pconfig);
				} else {
					try (var os = Files.newOutputStream(logFile, StandardOpenOption.APPEND)) {
						status = a.run(pconfig, os);
					}
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
			} else {
				ps.setStatus(status);
				var nextAction = getNextAction(currentStep);
				if (!nextAction.isEmpty()) {
					ps.setCurrentStep(nextAction.get().getId());
					return true;
				}
			}

		}

		return false;
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

	@Override
	public PipelineStatus<SequentialPipelineConfiguration> run(SequentialPipelineConfiguration pconfig) {
		return run(pconfig, null);
	}
	

	@Override
	public PipelineStatus<SequentialPipelineConfiguration> run(SequentialPipelineConfiguration pconfig, Path logFile) {
		var ps = initialize(actions.get(0).getId());

		ps.setStartDate(LocalDateTime.now());

		while (runNextStep(ps, pconfig, logFile)) {
		}

		ps.setEndDate(LocalDateTime.now());

		return ps;
	}

}
