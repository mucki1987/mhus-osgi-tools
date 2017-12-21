package de.mhus.karaf.mongo;

import de.mhus.lib.errors.MException;
import de.mhus.lib.mongo.MoManager;
import de.mhus.lib.mongo.MoSchema;

public interface MoManagerService {

	void doOpen() throws MException;
	
	void doInitialize();

	void doClose();

	String getServiceName();

	MoManager getManager();

	String getMongoDataSourceName();
	
}