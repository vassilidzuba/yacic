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

import java.util.List;

public interface Orchestrator {

	/**
	 * Run a pipeline.
	 * @param pipeline
	 */
	void run(Pipeline pipeline);
	
	/**
	 * retrieve the history;
	 * @return the history
	 */
	List<PipelineStatus> getHistory();

	
	/**
	 * retrieve the history of the pipelines with a given type;
	 * @return the history
	 */
	List<PipelineStatus> getHistory(String pipelineType);
}
