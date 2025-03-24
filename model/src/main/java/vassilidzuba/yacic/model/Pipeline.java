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

public interface Pipeline <T extends PipelineConfiguration> {
	/**
	 * Return the type of the pipeline<
	 * 
	 * @return the identifier of the pipeline<
	 */
	String getType();
	
	/**
	 * Return the identifier of the pipeline.
	 * 
	 * @return the identifier of the pipeline<
	 */
	String getId();
	
	/**
	 * Return the description of the pipeline.
	 * 
	 * @return the description of the pipeline<
	 */
	String getDescription();
	
	/**
	 * Run the pipeline until completion.
	 * @return the status
	 */
	PipelineStatus<T> run(T pctx, Path logFile);
	
	/**
	 * initialize the pipeline but not run any step.
	 * @paraù initialStep id of the current step
	 * @return the status
	 */
	PipelineStatus<T> initialize(String initialStep);
	
	/**
	 * Execute the next step.
	 * 
	 * @param ps the pipeline status
	 * @param logFile path to the log file
	 * @return true if pipeline should continue
	 */
	boolean runNextStep(PipelineStatus<T> ps, T pconfig, Path logFile);
	
	
	/**
	 * set the action context as a JSON string.
	 * @param data the JSON data
	 */
	void setActionContext(String data);
	
	/**
	 * get the action context as a JSON string.
	 * @return
	 */
	String getActionContext();
	
	/**
	 * set the data area.
	 * @param data tne JSON data
	 */
	void setDataArea(Path data);
	
	/**
	 * get the data area.
	 * @return
	 */
	Path getDataArea();
}
