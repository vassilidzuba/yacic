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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Pipeline status
 * @param <T> pipeline configuration
 */
public class PipelineStatus <T extends PipelineConfiguration> {
	@Setter @Getter
	private Pipeline<T> pipeline;
	@Setter @Getter
	private String id;
	@Setter @Getter
	private String status;
	@Setter @Getter
	private LocalDateTime startDate;
	@Setter @Getter
	private LocalDateTime endDate;
	@Setter @Getter
	private String currentStep;
	@Setter @Getter
	private List<ActionStatus> actionStatuses = new ArrayList<>();
	
	/**
	 * Constructor.
	 * 
	 * @param pipeline the pipeline;
	 */
	public PipelineStatus(Pipeline<T> pipeline) {
		this.pipeline = pipeline;
	}
	
	@Override
	public String toString() {
		var sb = new StringBuilder();
		
		sb.append("[id=");
		sb.append(id);
		sb.append(" status=");
		sb.append(status);
		sb.append(" currentStep=");
		sb.append(currentStep);
		sb.append(" pipeline=");
		sb.append(pipeline.getId());
		sb.append("]");
			
		return sb.toString();
	}
}
