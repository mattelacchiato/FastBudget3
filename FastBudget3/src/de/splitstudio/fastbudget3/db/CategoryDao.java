package de.splitstudio.fastbudget3.db;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import de.splitstudio.utils.db.GenericBaseDao;

public class CategoryDao extends GenericBaseDao<Category> {

	public CategoryDao(ObjectContainer db) {
		super(db);
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

	@Override
	public void delete(Category category) {
		db.delete(category.expenses);
		super.delete(category);
	}

	public void delete(String name) {
		Category category = findByName(name);
		delete(category);
	}

}
