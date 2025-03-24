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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PodmanActionDefinitionFactory {
	static XMLInputFactory xmlif = XMLInputFactory.newInstance();

	@SneakyThrows
	public static Map<String, PodmanActionDefinition> parse(InputStream is) {

		var xmlsr = xmlif.createXMLStreamReader(is);
		var ctx = new Context();

		while (xmlsr.hasNext()) {
			var eventType = xmlsr.next();

			switch (eventType) {
			case XMLStreamConstants.START_ELEMENT:
				processStartElement(xmlsr, ctx);
				break;
			case XMLStreamConstants.END_ELEMENT:
				processEndElement(xmlsr, ctx);
				break;
			case XMLStreamConstants.CHARACTERS:
				processCharacters(xmlsr, ctx);
				break;
			default:
				break;
			}
		}

		return ctx.getPodmanActionDefinitions();
	}

	private static void processStartElement(XMLStreamReader xmlsr, Context ctx) {
		String name = xmlsr.getName().getLocalPart();
		switch (name) {
		case "podmanactiondefinitions":
			break;
		case "podmanactiondefinition":
			processStartAction(xmlsr, ctx);
			break;
		case "image":
			ctx.setInImage(true);
			break;
		case "host":
			ctx.setInHost(true);
			break;
		case "username":
			ctx.setInUsername(true);
			break;
		case "command":
			ctx.setInCommand(true);
			break;
		case "setup":
			ctx.setInSetup(true);
			break;
		case "cleanup":
			ctx.setInCleanup(true);
			break;
		default:
			log.error("unexpected element: {}", name);
		}
		ctx.setSb(new StringBuilder());
	}

	private static void processCharacters(XMLStreamReader xmlsr, Context ctx) {
		var data = xmlsr.getText();
		ctx.getSb().append(data);
	}

	private static void processStartAction(XMLStreamReader xmlsr, Context ctx) {
		var id = xmlsr.getAttributeValue(null, "id");
		var mode = xmlsr.getAttributeValue(null, "mode");
		var pad = new PodmanActionDefinition();
		pad.setId(id);
		pad.setMode(mode);
		ctx.setPodmanActionDefinition(pad);
	}
	
	private static void processEndElement(XMLStreamReader xmlsr, Context ctx) {
		String name = xmlsr.getName().getLocalPart();
		switch (name) {
		case "podmanactiondefinitions":
			break;
		case "podmanactiondefinition":
			processEndAction(ctx);
			break;
		case "image":
			ctx.getPodmanActionDefinition().setImage(ctx.getSb().toString());
			break;
		case "host":
			ctx.getPodmanActionDefinition().setHost(ctx.getSb().toString());
			break;
		case "username":
			ctx.getPodmanActionDefinition().setUsername(ctx.getSb().toString());
			break;
		case "command":
			ctx.getPodmanActionDefinition().setCommand(ctx.getSb().toString());
			break;
		case "setup":
			ctx.getPodmanActionDefinition().setSetup(ctx.getSb().toString());
			break;
		case "cleanup":
			ctx.getPodmanActionDefinition().setCleanup(ctx.getSb().toString());
			break;
		default:
			log.error("unexpected element: {}", name);
		}
	}

	private static void processEndAction(Context ctx) {
		var pad = ctx.getPodmanActionDefinition();
		ctx.getPodmanActionDefinitions().put(pad.getId(), pad);
	}

	static class Context {
		@Setter
		@Getter
		private Map<String, PodmanActionDefinition> podmanActionDefinitions = new HashMap<>();
		@Setter
		@Getter
		private PodmanActionDefinition podmanActionDefinition;
		@Setter
		@Getter
		private boolean inImage;
		@Setter
		@Getter
		private boolean inHost;
		@Setter
		@Getter
		private boolean inUsername;
		@Setter
		@Getter
		private boolean inCommand;
		@Setter
		@Getter
		private boolean inSetup;
		@Setter
		@Getter
		private boolean inCleanup;
		@Setter
		@Getter
		private StringBuilder sb;
	}
}
