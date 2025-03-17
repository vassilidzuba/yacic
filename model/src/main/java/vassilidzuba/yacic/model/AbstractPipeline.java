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

package vassilidzuba.yacic.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

import lombok.Setter;

public abstract class AbstractPipeline<T extends PipelineConfiguration> implements Pipeline<T> {
	@Setter
	private String id;
	@Setter
	private String description;
	@Setter
	private String type;
	@Setter
	private String currentStep;
	private String actionContext;
	private Path dataArea;

	
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
	
	public String getCurrentStep() {
		return this.currentStep;
	}
	
	@Override
	public String getActionContext() {
		return this.actionContext;
	}
	
	@Override
	public void setActionContext(String actionContext) {
		this.actionContext = actionContext;
	}
	
	@Override
	public Path getDataArea() {
		return this.dataArea;
	}
	
	@Override
	public void setDataArea(Path dataArea) {
		this.dataArea = dataArea;
	}
	
	@Override
	public PipelineStatus<T> initialize(String initialStep) {
		var ps = new PipelineStatus<T>(this);
		ps.setId(getId());
		ps.setStartDate(LocalDateTime.now());
		ps.setCurrentStep(initialStep);
		return ps;
	}
}
