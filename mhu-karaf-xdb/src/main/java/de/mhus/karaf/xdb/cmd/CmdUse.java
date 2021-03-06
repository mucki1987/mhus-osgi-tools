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
package de.mhus.karaf.xdb.cmd;

import java.io.File;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;

import de.mhus.karaf.xdb.adb.AdbXdbApi;
import de.mhus.karaf.xdb.model.XdbApi;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.errors.MException;

@Command(scope = "xdb", name = "use", description = "Show or select the default api")
@Service
public class CmdUse implements Action {

	public static String api = AdbXdbApi.NAME;
	public static String service = null;
	public static String datasource = null;
	
	static {
		try {
			File f = getFile();
			if (f.exists()) {
				MUri uri = MUri.toUri(MFile.readFile(f).trim());
				if ("xdb".equals(uri.getScheme())) {
					if (uri.getPathParts().length > 0)
						api = uri.getPathParts()[0];
					if (uri.getPathParts().length > 1)
						service = uri.getPathParts()[1];
					if (uri.getPathParts().length > 2)
						datasource = uri.getPathParts()[2];
				}
				MLogUtil.log().i("XDB loaded",uri);
			}
		} catch (Throwable t) {}
	}
	@Argument(index=0, name="cmd", required=false, description="Command: save - save global settings, load - load global settings, set <uri>,apis,services", multiValued=false)
    String cmd = null;

	@Argument(index=1, name="uri", required=false, description="Uri to the current used service, e.g. xdb:api/service[/datasource]", multiValued=false)
    String uriName = null;
	
	@Option(name="-a", description="New api Name",required=false)
	String apiName = null;

	@Option(name="-s", description="Service Name",required=false)
	String serviceName = null;
	
	@Option(name="-d", description="Datasource Name",required=false)
	String dsName = null;
	
	@Option(name="-g", description="Set Global",required=false)
	boolean global = false;
	
    @Reference
    private Session session;

	@Override
	public Object execute() throws Exception {

		if ("load".equals(cmd)) {
			File f = getFile();
			if (!f.exists()) {
				System.out.println("File not found " + f);
			} else {
				uriName = MFile.readFile(f);
				cmd = "set";
			}
		}
		
		if ("set".equals(cmd) && uriName != null) {
			MUri uri = MUri.toUri(uriName);
			if (!"xdb".equals(uri.getScheme()))
				throw new MException("scheme is not xdb");
			apiName = uri.getPathParts()[0];
			if (uri.getPathParts().length > 1)
				serviceName = uri.getPathParts()[1];
			if (uri.getPathParts().length > 2)
				dsName = uri.getPathParts()[2];
						
		}
		
		if (global) {
			if (apiName != null) {
				XdbUtil.getApi(apiName);
				api = apiName;
			}
			
			if (serviceName != null) {
				XdbApi a = XdbUtil.getApi(api);
				a.getService(serviceName);
				service = serviceName;
			}
	
			if (dsName != null) {
				datasource = dsName; //check?
			}
		}

		if (apiName != null || serviceName != null || dsName != null)
			XdbUtil.setSessionUse(session, XdbUtil.getApiName(session, apiName), XdbUtil.getServiceName(session, serviceName), XdbUtil.getDatasourceName(session, dsName));

		System.out.println("Global : xdb:" + api + "/" + service + (datasource != null ? "/" + datasource : ""));

		System.out.println("Session: xdb:" + XdbUtil.getApiName(session, null) + "/" + XdbUtil.getServiceName(session, null) + (XdbUtil.getDatasourceName(session, null) != null ? "/" + XdbUtil.getDatasourceName(session, null) : ""));

		if ("apis".equals(cmd))
			for (String n : XdbUtil.getApis())
				System.out.println("Available Api: " + n);

		if ("services".equals(cmd)) {
			String an = XdbUtil.getApiName(session, null);
			XdbApi a = XdbUtil.getApi(an);
			System.out.println("Services in " + an + ":");
			for (String n : a.getServiceNames())
				System.out.println("  " + n);
		}
		
		if ("save".equals(cmd)) {
			File f = getFile();
			String content = "xdb:" + MUri.encode(api) + "/" + MUri.encode(service) + "/" + MUri.encode(datasource);
			MFile.writeFile(f, content);
			System.out.println("Written global settings! " + content);
		}
			
		return null;
	}
	
	private static File getFile() {
		return new File("etc/" + CmdUse.class.getCanonicalName() + ".cfg");
	}


}
