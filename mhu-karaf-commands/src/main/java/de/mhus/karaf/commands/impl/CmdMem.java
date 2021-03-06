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
package de.mhus.karaf.commands.impl;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "java", name = "mem", description = "Print the current memory situation of the JVM")
@Service
public class CmdMem implements Action {

	@Override
	public Object execute() throws Exception {
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();

		System.out.println("Free : " + free + " (" + (free / 1024 / 1024) + " MB)" );
		System.out.println("Total: " + total + " (" + (total / 1024 / 1024) + " MB)");
		System.out.println("Max  : " + max + " (" + (max / 1024 / 1024) + " MB)");
		return null;
	}

}
