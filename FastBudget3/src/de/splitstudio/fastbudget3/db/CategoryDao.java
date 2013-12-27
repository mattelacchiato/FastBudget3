package de.splitstudio.fastbudget3.db;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import de.splitstudio.utils.db.GenericBaseDao;

public class CategoryDao extends GenericBaseDao<Category> {

	public CategoryDao(ObjectContainer db) {
		super(db);
	}

	public Category findCategory(final String name) {
		ObjectSet<Category> set = db.query(new Predicate<Category>() {
			private static final long serialVersionUID = 1L;

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

}
