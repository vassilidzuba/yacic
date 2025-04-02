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

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.dropwizard.auth.basic.BasicCredentials;
import lombok.SneakyThrows;

class YacicSecurityTest {

	@BeforeAll
	static void init() {
		YacicSecurity.init(Path.of("config/security.json"));
	}
	
	@Test
	@DisplayName("test successfull authentication")
	@SneakyThrows
	void test1() {
		var authenticator = new YacicAuthenticator();
		
		var bc = new BasicCredentials("vassili", "sekret");
		var ouser = authenticator.authenticate(bc);
		
		Assertions.assertEquals("vassili", ouser.get().getName());
	}


	@Test
	@SneakyThrows
	@DisplayName("test unsuccessfull authentication with unknown user")
	void test2() {
		var authenticator = new YacicAuthenticator();
		
		var bc = new BasicCredentials("blackhat", "anonymous");
		var ouser = authenticator.authenticate(bc);
		
		Assertions.assertTrue(ouser.isEmpty());
	}
	
	@Test
	@SneakyThrows
	@DisplayName("test unsuccessfull authentication with bad password")
	void test3() {
		var authenticator = new YacicAuthenticator();
		
		var bc = new BasicCredentials("vassili", "wrongpassword");
		var ouser = authenticator.authenticate(bc);
		
		Assertions.assertTrue(ouser.isEmpty());
	}

	@Test
	void test4() {
		var authorizer = new YacicAuthorizer();
		
		var authorized = authorizer.authorize(new User("myself"), "developer", null);
		
		Assertions.assertTrue(authorized);
	}
}
