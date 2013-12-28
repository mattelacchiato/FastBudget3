package de.splitstudio.fastbudget3.test;

import android.test.ActivityInstrumentationTestCase2;
import de.splitstudio.fastbudget3.CategoryListActivity;
import de.splitstudio.utils.db.Database;

public abstract class EmptyStateTestCase extends ActivityInstrumentationTestCase2<CategoryListActivity> {

	public EmptyStateTestCase() {
		super(CategoryListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		final CategoryListActivity activity = getActivity();
		Database.clear();
		try {
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.updateView();
				}
			});
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

}
