package de.splitstudio.fastbudget3.db;

import android.content.Context;

import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.DatabaseMigration;

public class FastBudgetMigration extends DatabaseMigration {

	public FastBudgetMigration(Context context) {
		super(context);
	}

	@Override
	protected void migrate(int lastVersion, int currentVersion, ObjectContainer db) {
		if (lastVersion == 0) {
			deleteNullExpenses(db);
		}
	}

	private void deleteNullExpenses(ObjectContainer db) {
		for (Category category : db.query(Category.class)) {
			while (category.getExpenses().remove(null)) {}
			db.store(category);
		}
		db.commit();
	}

}
