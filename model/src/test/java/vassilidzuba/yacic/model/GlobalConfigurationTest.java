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
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GlobalConfigurationTest {

	@Test
	void test1() {
		var gc = new GlobalConfiguration();
		gc.setActionDefinitionDirectory(Path.of("../server/config/actionDefinitions"));
		gc.setPipelineDirectory(Path.of("../server/config/pipelines"));
		gc.setProjectDirectory(Path.of("../server/config/projects"));
		gc.setLogsDirectory(Path.of("target/logs"));
		gc.setNodes(List.of(new Node("odin", "gcc", "git")));
		gc.setMaxNbLogs(6);

		
		Assertions.assertEquals("actionDefinitions", gc.getActionDefinitionDirectory().getFileName().toString());
		Assertions.assertEquals("logs", gc.getLogsDirectory().getFileName().toString());
		Assertions.assertEquals("pipelines", gc.getPipelineDirectory().getFileName().toString());
		Assertions.assertEquals("projects", gc.getProjectDirectory().getFileName().toString());
		Assertions.assertEquals("odin", gc.getNodes().get(0).getHost());
		Assertions.assertEquals(6, gc.getMaxNbLogs());
	}
}
