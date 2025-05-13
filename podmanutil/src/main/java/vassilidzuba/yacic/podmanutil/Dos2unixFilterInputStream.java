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

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;

import lombok.SneakyThrows;

public class Dos2unixFilterInputStream extends FilterInputStream {

	public Dos2unixFilterInputStream(InputStream in) {
		super(new BufferedInputStream(in));
	}

	@Override
	@SneakyThrows
	public int read() {
		var ch = in.read();
		if (ch == '\r') {
			return read();
		}
		return ch;
	}
	
	@Override
	@SneakyThrows
	public int read(byte[] b,
            int off,
            int len) {
		
		var count = 0;
		for (var ii = off; ii < off + len; ii++) {
			var ch = read();
			if (ch != -1) {
				b[ii] = (byte) ch;
				count++;
			} else {
				break;
			}
		}
		
		return count;
	}
}
