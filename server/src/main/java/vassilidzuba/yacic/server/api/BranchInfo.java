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

import lombok.Getter;
import lombok.Setter;

public class BranchInfo {
	@Setter @Getter
	private String branchId;
	@Setter @Getter
	private String branchDir;
	
	public BranchInfo(String branchId, String branchDir) {
		this.branchId = branchId;
		this.branchDir = branchDir;
	}
}
