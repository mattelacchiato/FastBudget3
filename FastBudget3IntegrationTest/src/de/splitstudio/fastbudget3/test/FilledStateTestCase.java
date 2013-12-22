package de.splitstudio.fastbudget3.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static de.splitstudio.utils.DateUtils.createFirstDayOfMonth;
import static de.splitstudio.utils.DateUtils.createFirstDayOfYear;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;

public abstract class FilledStateTestCase<T extends ListActivity> extends ActivityInstrumentationTestCase2<T> {

	protected T initialActivity;

	public FilledStateTestCase(Class<T> klaas) {
		super(klaas);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initialActivity = getActivity();
		Database.clear();
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

		Calendar expenditureDate = createFirstDayOfMonth();
		firstCategory.expenditures.add(new Expenditure(10, expenditureDate.getTime(), "first expenditure"));
		expenditureDate.add(DAY_OF_MONTH, 1);
		firstCategory.expenditures.add(new Expenditure(20, expenditureDate.getTime(), "second expenditure"));
		expenditureDate.add(DAY_OF_MONTH, 1);
		firstCategory.expenditures.add(new Expenditure(30, expenditureDate.getTime(), "third expenditure"));

		Database.store(firstCategory);
		Database.store(new Category("second category", 20, categoryDate));
		Database.store(new Category("third category", 30, categoryDate));
	}

	private void runSyncAdapter() {
		getInstrumentation().runOnMainSync(syncAdapter());
		assertThat(initialActivity.getListAdapter().getCount(), is(3));
	}

	protected abstract Runnable syncAdapter();
}
