package de.splitstudio.fastbudget3.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static de.splitstudio.utils.DateUtils.createFirstDayOfYear;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.google.android.apps.common.testing.testrunner.ActivityLifecycleMonitor;
import com.google.android.apps.common.testing.testrunner.ActivityLifecycleMonitorRegistry;
import com.google.android.apps.common.testing.testrunner.Stage;

import android.app.Activity;
import android.app.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.Database;

public abstract class FilledStateTestCase<T extends ListActivity> extends ActivityInstrumentationTestCase2<T> {

	protected T initialActivity;
	protected Date firstExpenditureDate;

	public FilledStateTestCase(Class<T> klaas) {
		super(klaas);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initialActivity = getActivity();
		fillData();
		runSyncAdapter();
		refreshListView();
	}

	private void refreshListView() {
		onData(is(Object.class));
	}

	private void fillData() {
		Date categoryDate = createFirstDayOfYear().getTime();
		Category firstCategory = new Category("first category", 10, categoryDate);

		Calendar expenditureDate = DateUtils.createLastDayOfMonth();
		firstExpenditureDate = expenditureDate.getTime();
		firstCategory.expenses.add(new Expense(10, firstExpenditureDate, "first expenditure"));
		expenditureDate.add(DAY_OF_MONTH, -2);
		firstCategory.expenses.add(new Expense(20, expenditureDate.getTime(), "second expenditure"));
		expenditureDate.add(DAY_OF_MONTH, -2);
		firstCategory.expenses.add(new Expense(30, expenditureDate.getTime(), "third expenditure"));

		CategoryDao categoryDao = new CategoryDao(Database.getClearedInstance(initialActivity));
		categoryDao.store(firstCategory);
		categoryDao.store(new Category("second category", 20, categoryDate));
		categoryDao.store(new Category("third category", 30, categoryDate));
	}

	private void runSyncAdapter() {
		getInstrumentation().runOnMainSync(syncAdapter());
		assertThat(initialActivity.getListAdapter().getCount(), is(3));
	}

	protected abstract Runnable syncAdapter();

	public void assertTitleIs(final int titleId) throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivityLifecycleMonitor monitor = ActivityLifecycleMonitorRegistry.getInstance();
				Collection<Activity> startedActivities = monitor.getActivitiesInStage(Stage.RESUMED);
				assertThat(startedActivities.size(), is(1));
				for (Activity activity : startedActivities) {
					assertThat(activity.getTitle().toString(), is(activity.getString(titleId)));
				}
			}
		});
	}
}
