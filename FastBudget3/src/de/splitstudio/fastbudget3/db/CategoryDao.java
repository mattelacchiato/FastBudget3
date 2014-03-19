package de.splitstudio.fastbudget3.db;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import de.splitstudio.utils.db.GenericBaseDao;

public class CategoryDao extends GenericBaseDao<Category> {

	private final ExpenseDao expenseDao;

	public CategoryDao(ObjectContainer db) {
		super(db);
		expenseDao = new ExpenseDao(db);
	}

	@SuppressWarnings("serial")
	public Category findByName(final String name) {
		ObjectSet<Category> set = db.query(new Predicate<Category>() {
			@Override
			public boolean match(Category category) {
				return category.name.equals(name);
			}
		});

		if (set.isEmpty()) {
			return null;
		}
		return set.get(0);
	}

	public void delete(String name) {
		delete(findByName(name));
	}

	public void moveExpense(String expenseUuid, String oldCategoryName, String newCategoryName) {
		Category oldCategory = findByName(oldCategoryName);
		Category newCategory = findByName(newCategoryName);
		Expense expense = expenseDao.findByUuid(expenseUuid);

		oldCategory.getExpenses().remove(expense);
		newCategory.add(expense);

		store(oldCategory);
		store(newCategory);
	}

	public void deleteExpense(Category category, Expense expense) {
		category.remove(expense);
		store(category);
		expenseDao.delete(expense);
	}
}
