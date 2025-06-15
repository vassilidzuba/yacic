package vassilidzuba.yacic.simpleimpl;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import vassilidzuba.yacic.model.ImplementationInitializer;
import vassilidzuba.yacic.podmanutil.PodmanActionDefinition;

public abstract class AbstractInitializer implements ImplementationInitializer {
	@Getter
	protected static Map<String, PodmanActionDefinition> podmanActionDefinitions = new HashMap<>();
	@Getter
	protected static Map<String, SequentialPipeline> pipelines = new HashMap<>();

}
