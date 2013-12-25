package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;

@RunWith(RobolectricTestRunner.class)
public class ExpenseListActivity_DifferentItems_Test {

	private static final String CATEGORY_NAME = "foobar";
	private static final String ANY_DESCRIPTION = "description";

	private Category category;

	private ActivityController<ExpenseListActivity> activityController;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(ExpenseListActivity.class).withIntent(createIntent());
		initDb();
	}

	private void initDb() {
		Context context = activityController.get().getApplicationContext();
		ObjectContainer db = Database.getInstance(context);
		Database.clear();

		category = new Category(CATEGORY_NAME);
		db.store(category);
		db.commit();
	}

	private Intent createIntent() {
		Intent intent = new Intent(new OverviewActivity(), ExpenseListActivity.class);
		return intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
	}

	private ExpenseListActivity createActivity() {
		return activityController.create().get();
	}

	@Test
	public void itShowsTheDescription() throws Exception {
		String description = "jo!";
		category.expenses.add(new Expense(20, new Date(), description));

		ExpenseListActivity activity = createActivity();

		ListAdapter listAdapter = activity.getListAdapter();
		assertThat(listAdapter.getCount(), is(greaterThan(0)));
		View row = listAdapter.getView(0, null, null);
		assertThat(row, is(notNullValue()));
		TextView descriptionTextView = (TextView) row.findViewById(R.id.description);
		assertThat(descriptionTextView, is(notNullValue()));
		assertThat(descriptionTextView.getText().toString(), is(description));
	}

	@Test
	public void itShowsTheAmount() throws Exception {
		category.expenses.add(new Expense(20, new Date(), "bla"));

		View row = createActivity().getListAdapter().getView(0, null, null);
		TextView amountTextView = (TextView) row.findViewById(R.id.amount);
		assertThat(amountTextView, is(notNullValue()));
		assertThat(amountTextView.getText().toString(), is("$0.20"));
	}

	@Test
	public void itShowsTheDate() throws Exception {
		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date date = cal.getTime();
		category.expenses.add(new Expense(20, date, "bla"));

		View row = createActivity().getListAdapter().getView(0, null, null);
		TextView dateTextView = (TextView) row.findViewById(R.id.date_field);
		assertThat(dateTextView, is(notNullValue()));
		assertThat(dateTextView.getText().toString(), is(DateUtils.formatAsShortDate(date)));
	}

	@Test
	public void itShowsOnlyExpensesForCurrentPeriod() throws Exception {
		Calendar cal = Calendar.getInstance();
		category.expenses.add(new Expense(30, cal.getTime(), ANY_DESCRIPTION));
		category.expenses.add(new Expense(30, cal.getTime(), ANY_DESCRIPTION));
		cal.add(Calendar.MONTH, -1);
		category.expenses.add(new Expense(30, cal.getTime(), ANY_DESCRIPTION));

		ExpenseListActivity activity = createActivity();

		assertThat(activity.getListAdapter().getCount(), is(2));
	}

	@Test
	public void update_listViewUpdated() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		category.expenses.add(new Expense(30, cal.getTime(), null));
		ExpenseListActivity activity = createActivity();
		assertThat(activity.getListAdapter().getCount(), is(0));

		activity.start.add(Calendar.MONTH, -1);
		activity.update.run();

		assertThat(activity.getListAdapter().getCount(), is(1));
	}

}
