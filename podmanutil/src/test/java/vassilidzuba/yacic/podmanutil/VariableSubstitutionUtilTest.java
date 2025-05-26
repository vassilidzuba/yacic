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

package vassilidzuba.yacic.podmanutil;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import vassilidzuba.yacic.podmanutil.VariableSubtitutionUtil.SubtitutionFailedException;

class VariableSubstitutionUtilTest {

	private static Map<String, String> properties = new HashMap<>();
	
	@BeforeAll
	static void beforeAll() {
		properties.put("ALPHA", "alpha");
		properties.put("BETA", "beta");
		properties.put("GAMMA", "gamma");
	}
	
	@ParameterizedTest
	@CsvSource({
		"alpha,alpha",
		"@{ALPHA},alpha",
		"@@{ALPHA},@{ALPHA}",
		"xx@{ALPHA}@{BETA}yy,xxalphabetayy",
		"@{DZETA:dzeta},dzeta",
		"@{DZETA:dz@eta},dz@eta",
		"@{DZETA:dz:eta},dz:eta",
		"@{DZETA:},",
		})
	void testMainline(String template, String expected) {
		var result = VariableSubtitutionUtil.substitute(template, properties);
		Assertions.assertEquals(expected == null ? "" : expected, result);
		
	}
	
	@ParameterizedTest
	@CsvSource({
		"@{AL@PHA},unexpected state when reading '@': INVARNAME",
		"@{AL{PHA},unexpected state when reading '{': INVARNAME",
		"@}ALPHA,unexpected state when reading '}': AFTERAMPERSAND",
		"@:ALPHA,unexpected state when reading ':': AFTERAMPERSAND",
		"@{ALPHA,unexpected state when reading EOS: INVARNAME",
		"@{ALPHA:alpha,unexpected state when reading EOS: INDEFAULT",
		"www@,unexpected state when reading EOS: AFTERAMPERSAND",
		"@ALPHA,unexpected state when reading 'A': AFTERAMPERSAND",
		})
	void testExceptions(String template, String msg) {
		var exception = Assertions.assertThrows(SubtitutionFailedException.class, () -> {
			VariableSubtitutionUtil.substitute(template, properties);
	    });
		
		Assertions.assertEquals(msg, exception.getMessage());
		
	}

}
