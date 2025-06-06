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

import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;

/**
 * An action that executes Java code.
 */
public abstract class BuiltinAction extends AbstractAction {
	@Setter @Getter 
    private String id;
	
	@Setter
	private String description;
	
	@Setter @Getter
	private boolean asynchronous;
	
	private String context;
	private Path dataArea;
	
	@Override
	public String getDescription()  {
		return description;
	}

	
	@Override
	public void setContext(String ctx)  {
		this.context = ctx;
	}
	
	@Override
	public String getContext()  {
		return context;
	}
	
	@Override
	public void setDataArea(Path da)  {
		this.dataArea = da;
	}
	
	@Override
	public Path getDataArea()  {
		return dataArea;
	}
}
