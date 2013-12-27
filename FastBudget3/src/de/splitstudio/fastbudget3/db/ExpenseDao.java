package de.splitstudio.fastbudget3.db;

import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.GenericBaseDao;

public class ExpenseDao extends GenericBaseDao<Expense> {

	public ExpenseDao(ObjectContainer db) {
		super(db);
	}

}
