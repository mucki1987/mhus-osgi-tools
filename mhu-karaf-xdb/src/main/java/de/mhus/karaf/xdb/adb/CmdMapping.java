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
package de.mhus.karaf.xdb.adb;

import java.util.Map;
import java.util.TreeSet;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.osgi.services.adb.AdbUtilKaraf;
import de.mhus.osgi.services.adb.DbManagerService;

@Command(scope = "adb", name = "mapping", description = "Print the mapping table of a ADB DataSource")
@Service
public class CmdMapping implements Action {

	@Argument(index=0, name="service", required=true, description="Service Class", multiValued=false)
    String serviceName;
		
	@Override
	public Object execute() throws Exception {
		
		DbManagerService service = AdbUtilKaraf.getService(serviceName);
		
		ConsoleTable table = new ConsoleTable();
		table.setHeaderValues("Key","Mapping");
		
		Map<String, Object> map = service.getManager().getNameMapping();
		for (String entry : new TreeSet<String>(map.keySet())) {
			table.addRowValues(entry, String.valueOf(map.get(entry)) );
		}
		
		table.print(System.out);
		return null;
	}
	

}
