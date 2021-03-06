/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.karaf.commands.editor;

import java.util.LinkedList;

public class Content {

	private LinkedList<Line> lines = new LinkedList<>();
	@SuppressWarnings("unused")
	private Editor editor;
	
	public void init(Editor editor) {
		this.editor = editor;
	}

	public int lines() {
		return lines.size();
	}
	
	public int lineSize(int line) {
		if (line < 0 || line >= lines.size()) return 0;
		return lines.get(line).size();
	}
	
	public Line getLine(int line) {
		if (line < 0 || line >= lines.size()) return null;
		return lines.get(line);
	}	
}
