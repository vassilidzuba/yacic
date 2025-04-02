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

package vassilidzuba.yacic.server;


import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(DropwizardExtensionsSupport.class)
class ServerApplicationTest {
	private static final DropwizardAppExtension<ServerConfiguration> EXT = new DropwizardAppExtension<>(
            ServerApplication.class,
            "config/yacic-test.json"
        );


	@Test
	@DisplayName("mainline test")
	@SneakyThrows
	void test1() {
		var client = EXT.client();
		
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
		client.register(feature);
		
		var response = client.target(
                String.format("http://localhost:%d/yacic/pipeline/list", EXT.getLocalPort()))
               .request()
               .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, "vassili")
               .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, "sekret")
               .get();

		var data = response.readEntity(String.class);
		
		Assertions.assertEquals(200, response.getStatus());
		Assertions.assertTrue(data.contains("java-build"));
		
	}
	
	@Test
	@DisplayName("test no args")
	@SneakyThrows
	void test2() {
		Assertions.assertThrows(NoConfigurationAvailable.class, ServerApplication::main);
	}
	
	@Test
	@DisplayName("test configuration not readable")
	@SneakyThrows
	void test3() {
		Assertions.assertThrows(NoConfigurationAvailable.class, () -> {
			ServerApplication.main("server", "nor readable.json");
	    });
	}
}
