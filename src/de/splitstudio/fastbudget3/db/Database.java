package de.splitstudio.fastbudget3.db;

import java.io.File;

import android.content.Context;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;

public class Database {

	private static EmbeddedObjectContainer db;

	private Database() {}

	public static ObjectContainer getInstance(Context context) {
		if (db == null) {
			String databaseFileName = new File(context.getFilesDir(), "db").getAbsolutePath();
			db = Db4oEmbedded.openFile(databaseFileName);
		}
		return db;
	}

	/** test-only */
	public static void clear() {
		for (Object object : db.query().execute()) {
			db.delete(object);
		}
	}

}
