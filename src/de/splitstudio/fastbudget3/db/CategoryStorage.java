package de.splitstudio.fastbudget3.db;

import android.content.Context;

import com.eyeem.storage.Storage;

public class CategoryStorage extends Storage<Category> {

	private static final int MAX_ITEMS = 10000;
	private static CategoryStorage instance;

	//Singleton
	private CategoryStorage(Context context) {
		super(context);
	}

	@Override
	public String id(Category category) {
		return category.name;
	}

	@Override
	public Class<Category> classname() {
		return Category.class;
	}

	public static CategoryStorage getInstance(Context context) {
		if (instance == null) {
			instance = new CategoryStorage(context);
			instance.init(MAX_ITEMS);
		}
		return instance;
	}

}
