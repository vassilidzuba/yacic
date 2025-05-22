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

package vassilidzuba.yacic.server.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

/**
 * Project run status.
 * This is the return value of the resource ProjectRunResource.
 */
public class RunStatus {
	@JsonInclude(Include.NON_NULL)
	@Getter
	private String projectId;
	@JsonInclude(Include.NON_NULL)
	@Getter
	private String branchId;
	@JsonInclude(Include.NON_NULL)
	@Getter
	private String timestamp;
	@JsonInclude(Include.NON_NULL)
	@Getter
	private String status;
	@JsonInclude(Include.NON_NULL)
	@Getter
	private int duration;
	@Getter
	private String pipeline;
	
	public RunStatus(String projectId, String branchId, String timestamp, String status, int duration, String pipeline) {
		this.projectId = projectId;
		this.branchId = branchId;
		this.timestamp = timestamp;
		this.status = status;
		this.duration = duration;
		this.pipeline = pipeline;
	}
	
	@Override
	public String toString() {
		return "[RunStatus " + projectId + " " + branchId + " timestamp:" + timestamp + " status:" + status + " duration:" + duration + "]";
	}
}
