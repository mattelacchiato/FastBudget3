package de.splitstudio.fastbudget3;

import static java.util.Calendar.DAY_OF_MONTH;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.db.ExpenseDao;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.Database;

@RunWith(RobolectricTestRunner.class)
public class ExpenseListActivityWith3ItemsTest {

	private static final String CATEGORY_NAME = "foobar";

	private Category category;

	private ActivityController<ExpenseListActivity> activityController;
	private ExpenseListActivity activity;

	private Expense firstExpense;

	private ExpenseDao expenseDao;
	private CategoryDao categoryDao;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(ExpenseListActivity.class).withIntent(createIntent());
		initDb();
		activity = activityController.create().get();
	}

	private void initDb() {
		ObjectContainer db = Database.getClearedInstance(activityController.get());
		categoryDao = new CategoryDao(db);
		expenseDao = new ExpenseDao(db);

		category = new Category(CATEGORY_NAME);

		Calendar cal = DateUtils.createLastDayOfMonth();
		firstExpense = new Expense(20, cal.getTime(), "first");
		category.expenses.add(firstExpense);

		cal.add(DAY_OF_MONTH, -1);
		category.expenses.add(new Expense(20, cal.getTime(), "second"));

		cal.add(DAY_OF_MONTH, -1);
		category.expenses.add(new Expense(20, cal.getTime(), "third"));

		categoryDao.store(category);
	}

	private Intent createIntent() {
		Intent intent = new Intent(new CategoryListActivity(), ExpenseListActivity.class);
		return intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
	}

	@Test
	public void onStart_noContextIsVisible() throws Exception {
		for (int i = 0; i < 3; ++i) {
			View contextRow = getRow(i).findViewById(R.id.context_row);
			String msg = "Row " + i;
			assertThat(msg, contextRow, is(notNullValue()));
			assertThat(msg, contextRow.getVisibility(), is(View.GONE));
		}
	}

	@Test
	public void ordersItemsByDate() throws Exception {
		assertThat(((TextView) getRow(0).findViewById(R.id.description)).getText().toString(), is("first"));
		assertThat(((TextView) getRow(1).findViewById(R.id.description)).getText().toString(), is("second"));
		assertThat(((TextView) getRow(2).findViewById(R.id.description)).getText().toString(), is("third"));
	}

	@Test
	public void delete_expenditureNotVisibleAnymore() throws Exception {
		getRow(0).performClick();
		getRow(0).findViewById(R.id.button_delete).performClick();

		String firstDescription = ((TextView) getRow(0).findViewById(R.id.description)).getText().toString();
		assertThat(firstDescription, is("second"));
	}

	@Test
	public void delete_expenditureDeletedInDb() throws Exception {
		assertThat(expenseDao.findAll(Expense.class), hasSize(3));

		getRow(0).performClick();
		getRow(0).findViewById(R.id.button_delete).performClick();

		assertThat(expenseDao.findAll(Expense.class), hasSize(2));
		assertThat(expenseDao.findByUuid(firstExpense.description), is(nullValue()));
	}

	@Test
	public void delete_expenditureNotInCategory() throws Exception {
		getRow(0).performClick();
		getRow(0).findViewById(R.id.button_delete).performClick();

		Category persistedCategory = categoryDao.findByName(CATEGORY_NAME);
		assertThat(persistedCategory.expenses, not(hasItem(firstExpense)));
	}

	private View getRow(int rowIndex) {
		return activity.getListAdapter().getView(rowIndex, null, null);
	}

}
