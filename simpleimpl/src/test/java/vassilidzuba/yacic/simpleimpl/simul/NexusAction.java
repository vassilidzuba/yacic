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

package vassilidzuba.yacic.simpleimpl.simul;

import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import lombok.SneakyThrows;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.simpleimpl.BuiltinAction;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

public class NexusAction extends BuiltinAction {

	@Override
	@SneakyThrows
	public String run(SequentialPipelineConfiguration pctx, OutputStream os, List<Node> nodez) {
		var src = getDataArea().resolve("src/main/java/foo.java");
		if (! Files.isRegularFile(src)) {
			return "missingfile";
		}
		
		var reportfile = getDataArea().resolve("src/main/java/foo.report");
		Files.writeString(reportfile, "report");
		
		return "ok";
	}
}
