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
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Node;
import vassilidzuba.yacic.simpleimpl.BuiltinAction;
import vassilidzuba.yacic.simpleimpl.SequentialPipelineConfiguration;

@Slf4j
public class GitAction extends BuiltinAction {

	@Override
	@SneakyThrows
	public String run(SequentialPipelineConfiguration pctx, OutputStream os, List<Node> nodez) {
		log.info("running Git Action");
		
		var dataArea = getDataArea();
		
		var javaPath = dataArea.resolve("src/main/java/foo.java");
		Files.createDirectories(javaPath.getParent());
		Files.writeString(javaPath, "class foo {}");
		
		return "ok";
	}
}
