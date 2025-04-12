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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import vassilidzuba.yacic.model.PipelineConfiguration;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

public class SequentialPipelineConfiguration extends PipelineConfiguration {
	@Getter
	private Map<String, PodmanActionDefinition> pad = new HashMap<>();

	@Setter @Getter
	private List<StepEventListener> stepEventListeners = new ArrayList<>();
}
