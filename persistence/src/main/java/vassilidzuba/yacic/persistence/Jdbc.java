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
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import lombok.SneakyThrows;

public class Jdbc {
	private DataSource ds;

	public Jdbc(DataSource ds) {
		this.ds = ds;
	}

	@SneakyThrows
	public void store(String sql, Object... parameters) {
		try (var conn = ds.getConnection();
			 var pstmt = conn.prepareStatement(sql)) {
			for (int ii = 0; ii < parameters.length; ii++) {
				var param = parameters[ii];
				if (param instanceof String) {
					pstmt.setString(ii + 1, String.class.cast(param));
				} else if (param instanceof Integer) {
					pstmt.setInt(ii + 1, Integer.class.cast(param));
				} else {
					throw new UnexpectedParameterClass("class is " + param.getClass().getName() + ", should be Integer or String");
				}
			}
			pstmt.executeUpdate();
		}
	}

	@SneakyThrows
	public List<String> select(String sql, String... parameters) {
		var result = new ArrayList<String>();
		try (var conn = ds.getConnection();
			 var pstmt = conn.prepareStatement(sql)) {
			for (int ii = 0; ii < parameters.length; ii++) {
				pstmt.setString(ii + 1, parameters[ii]);
			}
			try (var rs = pstmt.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getString(1));
				}
			}
		}
		return result;
	}
	

	@SneakyThrows
	public <T> List<T> select(String sql, Mapper<T> mapper, String... parameters) {
		var result = new ArrayList<T>();
		try (var conn = ds.getConnection();
			 var pstmt = conn.prepareStatement(sql)) {
			for (int ii = 0; ii < parameters.length; ii++) {
				pstmt.setString(ii + 1, parameters[ii]);
			}
			try (var rs = pstmt.executeQuery()) {
				while (rs.next()) {
					result.add(mapper.map(rs));
				}
			}
		}
		return result;
	}
	
	public static interface Mapper<T> {
		T map(ResultSet rs);
	}
}
