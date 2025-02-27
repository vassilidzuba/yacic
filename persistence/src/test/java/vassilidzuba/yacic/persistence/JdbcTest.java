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

package vassilidzuba.yacic.persistence;

import java.sql.ResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import vassilidzuba.yacic.persistence.Jdbc.Mapper;

class JdbcTest {
	private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    
    static {
        config.setJdbcUrl( "jdbc:h2:mem:yacic;INIT=RUNSCRIPT FROM 'src/test/resources/h2init.sql';" );
        config.setUsername( "sa" );
        config.setPassword( "sa" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setDriverClassName("org.h2.Driver");
        ds = new HikariDataSource( config );
    }

    
	@Test
	void test1() {
		var jdbc = new Jdbc(ds);
		
		jdbc.store("insert into users(name, email) values('john', 'john@mycompany.com')");
		
		var names = jdbc.select("select name from users");
		
		Assertions.assertEquals(1, names.size());
		Assertions.assertEquals("john", names.get(0));

		var users = jdbc.select("select name, email from users", new UserMapper());
		Assertions.assertEquals(1, users.size());
		Assertions.assertEquals("john", users.get(0).getName());
		Assertions.assertEquals("john@mycompany.com", users.get(0).getEmail());
	}
	
	static class User {
		@Setter @Getter 
		String name;
		@Setter @Getter 
		String email;
	}
	
	static class UserMapper implements Mapper<User> {
		@Override
		@SneakyThrows
		public User map(ResultSet rs) {
			var user = new User();
			user.setName(rs.getString(1));
			user.setEmail(rs.getString(2));
			return user;
		}
		
	}
}
