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

package vassilidzuba.yacic.model.exceptions;

import lombok.Getter;

public class NoSuchBranchException extends RuntimeException {
	private static final long serialVersionUID = -1382742825638983096L;
	@Getter
	private String branch;
	@Getter
	private String project;
	
	public NoSuchBranchException(String branch, String project) {
		super("No branch: " + branch + " in project " + project);
		this.branch = branch;
		this.project =project;
	}
}
