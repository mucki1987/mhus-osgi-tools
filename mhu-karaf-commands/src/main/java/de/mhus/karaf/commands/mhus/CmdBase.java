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
package de.mhus.karaf.commands.mhus;

import java.util.Map.Entry;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.osgi.framework.FrameworkUtil;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.system.IApi;
import de.mhus.lib.mutable.KarafBase;
import de.mhus.lib.mutable.KarafMApiImpl;
import de.mhus.osgi.services.util.OsgiBundleClassLoader;

@Command(scope = "mhus", name = "base", description = "Base Manipulation")
@Service
public class CmdBase extends MLog implements Action {

    @Reference
    private Session session;

	@Argument(index=0, name="cmd", required=true, description="Command:\n get <ifc>, clear, getall, dump", multiValued=false)
    String cmd;

	@Argument(index=1, name="paramteters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	// private Appender appender;

	@Override
	public Object execute() throws Exception {

		IApi s = MApi.get();
		if (! (s instanceof KarafMApiImpl)) {
			System.out.println("Karaf MApi not set");
			return null;
		}
		KarafMApiImpl api = (KarafMApiImpl)s;
		
		switch (cmd) {
		case "get": {
			OsgiBundleClassLoader cl = new OsgiBundleClassLoader();
			Class<?> ifc = cl.loadClass(parameters[0]);
			    System.out.println("From Bundle : " + cl.getLoadBundle());
			Object obj = MApi.lookup(ifc);
			if (obj != null) {
				System.out.println("Owner Bundle: " + FrameworkUtil.getBundle(ifc));
				System.out.println("Object Id   : " + MSystem.getObjectId(obj));
			}
			    System.out.println("toString    : " + obj);
			return obj;
		}
		case "getall": {
			OsgiBundleClassLoader cl = new OsgiBundleClassLoader();
			for (Entry<String, Class<?>> item : cl.loadAllClasses(parameters[0]).entrySet()) {
				try {
					Class<?> ifc = item.getValue();
   					    System.out.println("From Bundle  : " + item.getKey() + " " + ifc);
					Object obj = MApi.lookup(ifc);
					if (obj != null) {
						System.out.println(" Owner Bundle: " + FrameworkUtil.getBundle(ifc));
						System.out.println(" Object Id   : " + MSystem.getObjectId(obj));
					}
					    System.out.println(" toString    : " + obj);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
				
		} break;
		case "clear": {
			((KarafBase)api.getBaseControl().base()).clearCache();
			System.out.println("OK");
		} break;
		case "dump": {
			((KarafBase)api.getBaseControl().base()).dumpCache(System.out);
			System.out.println("OK");
		} break;
		default:
			System.out.println("Command unknown");
		}
		
		return null;
	}

}
