package de.splitstudio.fastbudget3.db;

import java.io.File;

import android.content.Context;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.ObjectClass;

public class Database {

	private static EmbeddedObjectContainer db;

	private Database() {}

	public static ObjectContainer getInstance(Context context) {
		if (db == null) {
			String databaseFileName = new File(context.getFilesDir(), "db").getAbsolutePath();
			db = Db4oEmbedded.openFile(createConfig(), databaseFileName);
		}
		return db;
	}

	private static EmbeddedConfiguration createConfig() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		ObjectClass objectClass = config.common().objectClass(Category.class);
		objectClass.cascadeOnUpdate(true);
		objectClass.cascadeOnDelete(true);
		objectClass.cascadeOnActivate(true);
		ObjectClass object2Class = config.common().objectClass(Expenditure.class);
		object2Class.cascadeOnUpdate(true);
		object2Class.cascadeOnDelete(true);
		object2Class.cascadeOnActivate(true);
		return config;
	}

	public static void clear() {
		for (Object object : db.query().execute()) {
			db.delete(object);
		}
	}

}
