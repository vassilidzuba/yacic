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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class VariableSubtitutionUtil {

	enum State {
		START, AFTERAMPERSAND, INVARNAME, INDEFAULT
	}

	public static String substitute(String template, Map<String, String> properties) {
		if (template == null) {
			return "";
		}
		var substitution = new Substitution();

		for (var ch : buildCharArray(template)) {
			switch (ch) {
			case '@':
				substitution.processAmpersand();
				break;
			case '{':
				substitution.processOpenBrace();
				break;
			case '}':
				substitution.processCloseBrace(properties);
				break;
			case ':':
				substitution.processColon();
				break;
			case '\0':
				return substitution.processEOS();
			default:
				substitution.processDefault(ch);
				break;
			}
		}

		throw new SubtitutionFailedException("unexpected exception");
	}

	private static char[] buildCharArray(String template) {
		var ta = template.toCharArray();
		var in = new char[template.length() + 1];
		System.arraycopy(ta, 0, in, 0, ta.length);
		in[ta.length] = '\0';
		return in;
	}

	static class Substitution {
		private StringBuilder sb;
		private StringBuilder sbv;
		private StringBuilder sbd;

		private State state;

		public Substitution() {
			sb = new StringBuilder();
			state = State.START;
		}

		public void processAmpersand() {
			switch (state) {
			case State.START:
				state = State.AFTERAMPERSAND;
				break;
			case State.AFTERAMPERSAND:
				state = State.START;
				sb.append("@");
				break;
			case State.INDEFAULT:
				sbd.append("@");
				break;
			default:
				throw new SubtitutionFailedException("unexpected state when reading '@': " + state);
			}
		}

		public void processOpenBrace() {
			switch (state) {
			case State.START:
				sb.append("{");
				break;
			case State.AFTERAMPERSAND:
				state = State.INVARNAME;
				sbv = new StringBuilder();
				break;
			case State.INDEFAULT:
				sbd.append("{");
				break;
			default:
				throw new SubtitutionFailedException("unexpected state when reading '{': " + state);
			}
		}
		
		public void processCloseBrace(Map<String, String> properties) {
			switch (state) {
			case State.START:
				sb.append("}");
				break;
			case State.INVARNAME: {
				state = State.START;
				var propname = sbv.toString();
				if (StringUtils.isBlank(propname)) {
					throw new SubtitutionFailedException("no variable name: " + propname);
				}
				var propval = properties.get(propname);
				if (propval != null) {
					sb.append(propval);
				} else {
					throw new SubtitutionFailedException("undefined variable: " + propname);
				}
				sbv = null;
			}
				break;
			case State.INDEFAULT: {
				state = State.START;
				var propname = sbv.toString();
				if (StringUtils.isBlank(propname)) {
					throw new SubtitutionFailedException("no variable name: " + propname);
				}
				var defval = sbd.toString();
				var propval = properties.get(propname);
				if (propval != null) {
					sb.append(propval);
				} else {
					sb.append(defval);
				}
				sbv = null;
				sbd = null;
			}
				break;
			default:
				throw new SubtitutionFailedException("unexpected state when reading '}': " + state);
		}
		}
			
			
		public void processColon() {
			switch (state) {
			case State.START:
				sb.append(":");
				break;
			case State.INVARNAME:
				sbd = new StringBuilder();
				state = State.INDEFAULT;
				break;
			case State.INDEFAULT:
				sbd.append(":");
				break;
			default:
				throw new SubtitutionFailedException("unexpected state when reading ':': " + state);
			}
		}

		public String processEOS() {
			if  (state == State.START) {
				return sb.toString();
			} else {
				throw new SubtitutionFailedException("unexpected state when reading EOS: " + state);
			}
		}
		
		public void processDefault(char ch) {
			switch (state) {
			case State.START:
				sb.append(ch);
				break;
			case State.INVARNAME:
				sbv.append(ch);
				break;
			case State.INDEFAULT:
				sbd.append(ch);
				break;
			default:
				throw new SubtitutionFailedException("unexpected state when reading '" + ch + "': " + state);
			}
		}
	}

	static class SubtitutionFailedException extends RuntimeException {
		private static final long serialVersionUID = -4354569041589465182L;

		public SubtitutionFailedException(String msg) {
			super(msg);
		}
	}
}
