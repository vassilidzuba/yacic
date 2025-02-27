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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vassilidzuba.yacic.model.Pipeline;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SequentialPipelineFactory {
	static XMLInputFactory xmlif = XMLInputFactory.newInstance();

	@SneakyThrows
	public static Pipeline parse(InputStream is) {

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

		return ctx.getPipeline();
	}
	
	private static void processCharacters(XMLStreamReader xmlsr, Context ctx) {
		var data = xmlsr.getText();
		if (ctx.isInDescription()) {
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
		default:
			log.error("unexpected element: {}", name);
		}
		
	}

	@SuppressWarnings("unused") 
	private static void processStartDescription(XMLStreamReader xmlsr, Context ctx) {
		ctx.setDescription(new StringBuilder());
		ctx.setInDescription(true);
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
		var className = xmlsr.getAttributeValue(null, "class");
		var clazz = Class.forName(className);
		var action = (JavaAction) clazz.getDeclaredConstructor().newInstance();
		action.setId(id);
		ctx.setStep(action);
		ctx.setInStep(true);
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
		if (ctx.isInStep()) {
			ctx.getStep().setDescription(ctx.getDescription().toString());
		} else {
			ctx.getPipeline().setDescription(ctx.getDescription().toString());
		}
		ctx.setDescription(null);
		ctx.setInDescription(false);
	}

	@SuppressWarnings("unused") 
	private static void processEndPipeline(XMLStreamReader xmlsr, Context ctx) {
		// nothing special to do
	}

	@SuppressWarnings("unused") 
	private static void processEndStep(XMLStreamReader xmlsr, Context ctx) {
		ctx.getPipeline().addAction(ctx.getStep());
		ctx.setStep(null);
		ctx.setInStep(false);
	}

	static class Context {
		@Setter 
		@Getter
		private SequentialPipeline pipeline;
		@Setter 
		@Getter
		private JavaAction step;
		@Setter 
		@Getter
		private boolean inStep;
		@Setter 
		@Getter
		private boolean inDescription;
		@Setter 
		@Getter
		private StringBuilder description;

	}
}
