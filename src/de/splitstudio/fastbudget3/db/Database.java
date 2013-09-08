package de.splitstudio.fastbudget3.db;

import java.io.File;

import android.content.Context;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

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
		config.common().objectClass(Category.class).cascadeOnDelete(true);
		return config;
	}

	public static ObjectContainer clear() {
		for (Object object : db.query().execute()) {
			db.delete(object);
		}
		return db;
	}

	public static <T> T store(T object) {
		db.store(object);
		db.commit();
		return object;
	}

	public static Category findCategory(final String name) {
		return db.query(new Predicate<Category>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(Category category) {
				return category.name.equals(name);
			}
		}).get(0);
	}

}
