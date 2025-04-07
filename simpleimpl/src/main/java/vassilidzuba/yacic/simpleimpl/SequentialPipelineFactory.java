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
import java.util.ArrayDeque;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Action;
import vassilidzuba.yacic.model.Pipeline;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SequentialPipelineFactory {
	static XMLInputFactory xmlif = XMLInputFactory.newInstance();

	@SneakyThrows
	public static Pipeline<SequentialPipelineConfiguration> parse(InputStream is) {

		var xmlsr = xmlif.createXMLStreamReader(is);
		var ctx = new Context();
		ctx.getElementTypes().push(ElementType.TOP);

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

		return ctx.getPipeline();
	}

	private static void processCharacters(XMLStreamReader xmlsr, Context ctx) {
		var data = xmlsr.getText();
		if (ctx.getElementTypes().peek() == ElementType.DESCRIPTION) {
			ctx.getDescription().append(data);
		}

	}

	private static void processStartElement(XMLStreamReader xmlsr, Context ctx) {
		String name = xmlsr.getName().getLocalPart();
		switch (name) {
		case "pipeline":
			processPipeline(xmlsr, ctx);
			break;
		case "step":
			processStartStep(xmlsr, ctx);
			break;
		case "description":
			processStartDescription(xmlsr, ctx);
			break;
		case "skipwhen":
			// ignored
			break;
		case "flag":
			processStartFlag(xmlsr, ctx);
			break;
		default:
			log.error("unexpected element: {}", name);
		}

	}

	private static void processStartFlag(XMLStreamReader xmlsr, Context ctx) {
		var flag = xmlsr.getAttributeValue(null, "name");
		ctx.getStep().getSkipWhen().add(flag);
	}

	@SuppressWarnings("unused")
	private static void processStartDescription(XMLStreamReader xmlsr, Context ctx) {
		ctx.setDescription(new StringBuilder());
		ctx.getElementTypes().push(ElementType.DESCRIPTION);
	}

	private static void processPipeline(XMLStreamReader xmlsr, Context ctx) {
		var type = xmlsr.getAttributeValue(null, "type");
		var id = xmlsr.getAttributeValue(null, "id");
		var pipeline = new SequentialPipeline(type);
		pipeline.setId(id);
		ctx.setPipeline(pipeline);
	}

	@SneakyThrows
	private static void processStartStep(XMLStreamReader xmlsr, Context ctx) {
		var id = xmlsr.getAttributeValue(null, "id");
		var category = xmlsr.getAttributeValue(null, "category");
		if (category == null || "builtin".equals(category)) {
			var className = xmlsr.getAttributeValue(null, "class");
			var clazz = Class.forName(className);
			var action = (BuiltinAction) clazz.getDeclaredConstructor().newInstance();
			action.setId(id);
			ctx.setStep(action);
			ctx.getElementTypes().push(ElementType.BUILTIN_STEP);
			return;
		}
		if ("podman".equals(category)) {
			var type = xmlsr.getAttributeValue(null, "type");
			var command = xmlsr.getAttributeValue(null, "subcommand");
			var action = new PodmanAction();
			action.setId(id);
			action.setType(type);
			action.setSubcommand(command);
			ctx.setStep(action);
			ctx.getElementTypes().push(ElementType.PODMAN_STEP);
			return;
		}
		log.error("unexpected step type: {}", category);
	}

	private static void processEndElement(XMLStreamReader xmlsr, Context ctx) {
		String name = xmlsr.getName().getLocalPart();
		switch (name) {
		case "pipeline":
			processEndPipeline(xmlsr, ctx);
			break;
		case "step":
			processEndStep(xmlsr, ctx);
			break;
		case "description":
			processEndDescription(xmlsr, ctx);
			break;
		default:
			log.error("unexpected element: {}", name);
		}
	}

	@SuppressWarnings("unused")
	private static void processEndDescription(XMLStreamReader xmlsr, Context ctx) {
		ctx.getElementTypes().pop();
		switch (ctx.getElementTypes().peek()) {
		case ElementType.BUILTIN_STEP:
			BuiltinAction.class.cast(ctx.getStep()).setDescription(ctx.getDescription().toString());
			break;
		case ElementType.PODMAN_STEP:
			PodmanAction.class.cast(ctx.getStep()).setDescription(ctx.getDescription().toString());
			break;
		case ElementType.TOP:
			ctx.getPipeline().setDescription(ctx.getDescription().toString());
			break;
		default:
			log.error("unexpected description end");
		}

		ctx.setDescription(null);
	}

	@SuppressWarnings("unused")
	private static void processEndPipeline(XMLStreamReader xmlsr, Context ctx) {
		// nothing special to do
	}

	@SuppressWarnings("unused")
	private static void processEndStep(XMLStreamReader xmlsr, Context ctx) {
		ctx.getPipeline().addAction(ctx.getStep());
		ctx.setStep(null);
		ctx.getElementTypes().pop();
	}

	static class Context {
		@Setter
		@Getter
		private SequentialPipeline pipeline;
		@Setter
		@Getter
		private Action<SequentialPipelineConfiguration> step;
		@Setter
		@Getter
		private ArrayDeque<ElementType> elementTypes = new ArrayDeque<>();
		@Setter
		@Getter
		private StringBuilder description;

	}
	
	enum ElementType {
		TOP, DESCRIPTION, BUILTIN_STEP, PODMAN_STEP;
	}
	
}
