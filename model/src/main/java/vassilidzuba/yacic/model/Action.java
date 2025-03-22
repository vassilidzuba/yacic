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

import java.io.OutputStream;
import java.nio.file.Path;

public interface Action<T extends PipelineConfiguration> {
	
	/**
	 * Returns the identifier of the action; should be unique.
	 * @return the identifier of the action
	 */
	String getId();
	
	/**
	 * Returns the description of the action.
	 * @return the description of the action
	 */
	String getDescription();
	
	/**
	 * Executes an action synchronously.
	 * @return the exit status of the run. Should be "OK" for a sucessfull run. 
	 */
	String run(T pctx);

	/**
	 * Executes an action synchronously.
	 * @param pconfig the connfiguration
	 * @param os a stream to the log 
	 * @return the exit status of the run. Should be "OK" for a sucessfull run. 
	 */
	String run(T pctx, OutputStream os);

	/**
	 * Executes an action asynchronously.
	 * @return the handle to the execution. 
	 */
	ActionExecutionHandle<T> runAsynchronously();
	
	/**
	 * set the context qs q JSON string.
	 * @param data thne JSON data
	 */
	void setContext(String data);
	
	/**
	 * get the contect as a JSON string.
	 * @return
	 */
	String getContext();
	
	/**
	 * set the data area.
	 * @param data thne JSON data
	 */
	void setDataArea(Path data);
	
	/**
	 * get the data area.
	 * @return
	 */
	Path getDataArea();
}
