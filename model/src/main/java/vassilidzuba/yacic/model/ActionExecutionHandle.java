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

public interface ActionExecutionHandle<T extends PipelineConfiguration> {

	/**
	 * return the action corresponding to the run.
	 * @return the action
	 */
	Action<T> getAction();
	
	/**
	 * Checks if the run is completed
	 * @return true is the run is completed.
	 */
	boolean isCompleted();
	
	/**
	 * returns the exit status of the action if it is completed or "*running*" otherwise.   
	 * @return the exit status
	 */
	String getExitStatus();
	
	
	/**
	 * add a listener.
	 * @param listener the listener
	 */
	void addListener(ActionListener<T> listener);
}
