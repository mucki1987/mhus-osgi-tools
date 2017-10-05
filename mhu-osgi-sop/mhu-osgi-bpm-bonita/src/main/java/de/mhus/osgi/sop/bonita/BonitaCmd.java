package de.mhus.osgi.sop.bonita;

import java.util.HashMap;
import java.util.Map;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

@Command(scope = "sop", name = "bonita", description = "Bonitasoft BPM actions")
@Service
public class BonitaCmd implements Action {

	@Argument(index=0, name="cmd", required=true, description=
			"Command "
			+ " admin,"
			+ " idtree", multiValued=false)
	String cmd;
	
	@Argument(index=1, name="parameters", required=false, description="More Parameters", multiValued=true)
    String[] parameters;
	
	@Override
	public Object execute() throws Exception {
		
		final Map<String, String> parameters = new HashMap<>();
		   parameters.put("server.url", "http://localhost:8080");
		   //application name is the name of context, default is bonita
		   parameters.put("application.name", "bonita");

		 APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, parameters);

		 
		 final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
		 APISession session = loginAPI.login("mike", "nein");
		 ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
		 
		 System.out.println("Login: " + session);
		 
		 switch (cmd) {

		 case "processes": {
			 for (ProcessDeploymentInfo info : processAPI.getProcessDeploymentInfos(0, 1000, ProcessDeploymentInfoCriterion.NAME_ASC)) {
				 System.out.println(info);
			 }
		 } break;
		 case "start": {
			 ProcessDeploymentInfo process = null; 
			 for (ProcessDeploymentInfo info : processAPI.getProcessDeploymentInfos(0, 1000, ProcessDeploymentInfoCriterion.NAME_ASC)) {
				 if (info.getName().equals("bpm.simple-human")) {
					 process = info;
					 break;
				 }
			 }
			 if (process == null) {
				 System.out.println("Process not found");
				 return null;
			 }
			 ProcessInstance inst = processAPI.startProcess(process.getProcessId());
			 System.out.println(inst);
			 
		 } break;
		 }

		 
		 //		 processAPI.startProcess(processDefinitionId);
		 
		 
		 loginAPI.logout(session);
		 
		return null;
	}
	
	
	
}
