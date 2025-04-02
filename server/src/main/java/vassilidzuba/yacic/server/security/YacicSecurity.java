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

package vassilidzuba.yacic.server.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import io.dropwizard.auth.basic.BasicCredentials;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Manage security.
 * Not secure at all at the moment...
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YacicSecurity {
	private static Map<String, BasicCredentials> credentialsMap = new HashMap<>();

	public static Optional<User> check(BasicCredentials credentials) {
		var bc = credentialsMap.get(credentials.getUsername());
		if (bc != null && credentials.getPassword().equals(bc.getPassword())) {
			return Optional.of(new User(bc.getUsername()));
		}

		return Optional.empty();
	}

	@SneakyThrows
	public static void init(Path securityFile) {
		var objectMapper = new ObjectMapper();
		try (var is = Files.newInputStream(securityFile)) {
			CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class,
					SecurityEntry.class);
			List<SecurityEntry> list = objectMapper.readValue(is, javaType);
			list.forEach(se -> credentialsMap.put(se.getUsername(), new BasicCredentials(se.getUsername(), se.getPassword())));
		}
	}

	static class SecurityEntry {
		@Setter
		@Getter
		private String username;
		@Setter
		@Getter
		private String password;
	}
}
