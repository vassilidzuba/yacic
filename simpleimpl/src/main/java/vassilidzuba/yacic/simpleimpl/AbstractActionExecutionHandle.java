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

import java.util.ArrayList;
import java.util.List;

import vassilidzuba.yacic.model.Action;
import vassilidzuba.yacic.model.ActionExecutionHandle;
import vassilidzuba.yacic.model.ActionListener;

public abstract class AbstractActionExecutionHandle implements  ActionExecutionHandle<SequentialPipelineConfiguration> {
	private Action<SequentialPipelineConfiguration> action;
	private List<ActionListener<SequentialPipelineConfiguration>> listeners = new ArrayList<>(); 
	
	protected AbstractActionExecutionHandle(Action<SequentialPipelineConfiguration> action) {
		this.action = action;
	}
	
	public Action<SequentialPipelineConfiguration> getAction() {
		return action;
	}

	@Override
	public void addListener(ActionListener<SequentialPipelineConfiguration> listener) {
		this.listeners.add(listener);
	}
}
