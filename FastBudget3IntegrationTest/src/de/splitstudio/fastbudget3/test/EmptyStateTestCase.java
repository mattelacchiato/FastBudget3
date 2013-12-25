package de.splitstudio.fastbudget3.test;

import android.test.ActivityInstrumentationTestCase2;
import de.splitstudio.fastbudget3.OverviewActivity;
import de.splitstudio.fastbudget3.db.Database;

public abstract class EmptyStateTestCase extends ActivityInstrumentationTestCase2<OverviewActivity> {

	public EmptyStateTestCase() {
		super(OverviewActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		final OverviewActivity activity = getActivity();
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
