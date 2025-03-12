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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import vassilidzuba.yacic.model.PipelineStatus;

public class OrchestratorPersistence {
	private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    
    static {
        config.setJdbcUrl( "jdbc:h2:mem:yacic;INIT=RUNSCRIPT FROM 'yacic.sql';" );
        config.setUsername( "sa" );
        config.setPassword( "sa" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setDriverClassName("org.h2.Driver");
        ds = new HikariDataSource( config );
    }

	public void store(PipelineStatus ps) {
		var db = new Jdbc(ds);
		
		db.store("""
				insert into pipelines(pipeline_id, id, status, start_date, end_date, current_step)
				values(?, ?, ?, ?, ?, ?);
				""",
				ps.getPipeline().getId(), ps.getId(), ps.getStatus(), date2string(ps.getStartDate()), date2string(ps.getEndDate()), ps.getCurrentStep());
		
	}


	public List<String> listPipelines() {
		var db = new Jdbc(ds);
		
		return db.select("select id from pipelines");
	}

	private String date2string(LocalDateTime ldt) {
		if (ldt == null) {
			return "";
		} else {
		    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt);
		}
	}
}
