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
package de.mhus.karaf.commands.testit;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MHousekeeper;
import de.mhus.lib.core.MHousekeeperTask;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.MThreadPool;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.base.service.LockManager;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.core.lang.ValueProvider;
import de.mhus.lib.core.system.DefaultHousekeeper;
import de.mhus.osgi.services.util.OsgiBundleClassLoader;

public class MhusShit implements ShitIfc {

	private long doitTime;

	@Override
	public void printUsage() {
		System.out.println("lookup <ifc> [<def>]");
		System.out.println("housekeepertest");
		System.out.println("housekeepertasks");
		System.out.println("locks - print known locks from LockManager");
		System.out.println("releaselock <id>");
		System.out.println("createlock <name> - create managed lock");
		System.out.println("setlock <id>");
	}

	@Override
	public Object doExecute(String cmd, String[] parameters) throws Exception {
		if (cmd.equals("createlock")) {
			Lock lock = MApi.lookup(LockManager.class).getLock(parameters[0]);
			System.out.println("Created " + lock.hashCode());
		} else
		if (cmd.equals("setlock")) {
			int id = M.c(parameters[0], 0);
			for (Lock lock : MApi.lookup(LockManager.class).getRegisteredLocks())
				if (id == lock.hashCode()) {
					System.out.println("Set " + id);
					lock.lock();
					return null;
				}
			for (Lock lock : MApi.lookup(LockManager.class).managedLocks())
				if (id == lock.hashCode()) {
					System.out.println("Set " + id);
					lock.lock();
					return null;
				}
			System.out.println("Not found");
		} else
		if (cmd.equals("releaselock")) {
			int id = M.c(parameters[0], 0);
			for (Lock lock : MApi.lookup(LockManager.class).getRegisteredLocks())
				if (id == lock.hashCode()) {
					System.out.println("Unlock " + id);
					lock.unlockHard();
					return null;
				}
			for (Lock lock : MApi.lookup(LockManager.class).managedLocks())
				if (id == lock.hashCode()) {
					System.out.println("Unlock " + id);
					lock.unlockHard();
					return null;
				}
			System.out.println("Not found");
		} else
		if (cmd.equals("locks")) {
			ConsoleTable out = new ConsoleTable(false);
			long now = System.currentTimeMillis();
			out.setHeaderValues("Id","Name","Locked","Privacy","Locker","Time","Since","Managed");
			for (Lock lock : MApi.lookup(LockManager.class).managedLocks())
				out.addRowValues(
						lock.hashCode(), 
						lock.getName(),
						lock.isLocked(),
						lock.isPrivacy(),
						lockerName(lock.getLocker()),
						lock.isLocked() ? new Date(lock.getLockTime()) : "",
						lock.isLocked() ? MTimeInterval.getIntervalAsStringSec(now - lock.getLockTime()) : "",
								"true"
						);
			for (Lock lock : MApi.lookup(LockManager.class).getRegisteredLocks())
				out.addRowValues(
						lock.hashCode(), 
						lock.getName(),
						lock.isLocked(),
						lock.isPrivacy(),
						lockerName(lock.getLocker()),
						lock.isLocked() ? new Date(lock.getLockTime()) : "",
						lock.isLocked() ? MTimeInterval.getIntervalAsStringSec(now - lock.getLockTime()) : "",
								"false"
						);
			out.print();
		} else
		if (cmd.equals("lookup")) {
			OsgiBundleClassLoader loader = new OsgiBundleClassLoader();
			Class<?> ifc = loader.loadClass(parameters[0]);
			Object obj = null;
			if (parameters.length > 1) {
				Class<?> def = loader.loadClass(parameters[1]);
				Method method = MApi.class.getMethod("lookup", Class.class, Class.class);
				obj = method.invoke(null, ifc, def);
			} else {
				obj = MApi.lookup(ifc);
			}
			
			if (obj != null) {
				System.out.println(obj.getClass());
			}
			return obj;
		}
		if (cmd.equals("housekeepertasks")) {
			Map<MHousekeeperTask, Long> map = DefaultHousekeeper.getAll();
			ConsoleTable table = new ConsoleTable(false);
			table.setHeaderValues("Name","Class","Sleep");
			for (Entry<MHousekeeperTask, Long> entry : map.entrySet())
				table.addRowValues(entry.getKey().getName(),MSystem.getCanonicalClassName(entry.getKey().getClass()),entry.getValue());
			table.print(System.out);
		}
		if (cmd.equals("housekeepertest")) {
			MHousekeeper housekeeper = MApi.lookup(MHousekeeper.class);
			doitTime = 0;
			MHousekeeperTask task = new MHousekeeperTask() {
								
				@Override
				protected void doit() throws Exception {
					System.out.println("--- doit");
					doitTime = System.currentTimeMillis();
				}
			};
			housekeeper.register(task, 1000);
			
			String res = MThreadPool.getWithTimeout(new ValueProvider<String>() {

				@Override
				public String getValue() throws Exception {
					System.out.println("--- check");
					if (doitTime == 0)
						return null;
					return "ok";
				}
			}, 30000, false);
			System.out.println(res + " " + doitTime);
		}
		return null;
	}

	private String lockerName(Thread locker) {
		if (locker == null) return "";
		return locker.getId() + " " + locker.getName();
	}

}
