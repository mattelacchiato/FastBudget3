package de.splitstudio.fastbudget3;

import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static de.splitstudio.utils.NumberUtils.formatAsDecimal;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.view.TestMenu;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.view.MenuItem;
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
public class ExpenseActivity_Edit_Test {

	private static final String CATEGORY_NAME = "Category One";

	private static final Date ANY_DATE = DateUtils.createFirstDayOfYear().getTime();

	private static final int ANY_BUDGET = 0;

	private static final String DESCRIPTION = "desc";

	private ExpenseActivity activity;

	private ObjectContainer db;

	private TestMenu menu;

	private Expense expense;

	private ExpenseDao expenseDao;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		CategoryListActivity categoryListActivity = buildActivity(CategoryListActivity.class).create().get();

		expense = new Expense(10, DateUtils.createFirstDayOfMonth().getTime(), DESCRIPTION);

		Intent intent = new Intent(categoryListActivity, ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
		intent.putExtra(Extras.Id.name(), expense.uuid);
		ActivityController<ExpenseActivity> activityController = buildActivity(ExpenseActivity.class)
				.withIntent(intent);
		activity = activityController.get();

		initDb();

		activityController.create();
		menu = new TestMenu();
		activity.onCreateOptionsMenu(menu);
		shadowOf(activity.findViewById(R.id.calculator)).callOnAttachedToWindow();
	}

	private void initDb() {
		db = Database.getClearedInstance(activity);
		CategoryDao categoryDao = new CategoryDao(db);

		Category category = new Category(CATEGORY_NAME, ANY_BUDGET, ANY_DATE);
		category.expenses.add(expense);
		categoryDao.store(category);
		expenseDao = new ExpenseDao(db);
	}

	@Test
	public void itShowsItsDescpription() {
		String description = findTextView(R.id.description).getText().toString();
		assertThat(description, is(expense.description));
	}

	@Test
	public void itShowsItsAmount() {
		String amount = findTextView(R.id.calculator_amount).getText().toString();
		assertThat(amount, is(formatAsDecimal(expense.amount)));
	}

	@Test
	public void itShowsItsDate() {
		String date = findTextView(R.id.date_field).getText().toString();
		assertThat(date, is(formatAsShortDate(expense.date)));
	}

	@Test
	public void save_updatesDescription() throws Exception {
		String newDescription = "new";
		findTextView(R.id.description).setText(newDescription);
		clickMenuItem(R.id.save);

		Expense persisted = expenseDao.findByUuid(expense.uuid);
		assertThat(persisted.description, is(newDescription));
	}

	private TextView findTextView(int id) {
		return (TextView) activity.findViewById(id);
	}

	private void clickMenuItem(int itemId) {
		MenuItem menuItem = menu.findItem(itemId);
		assertThat(menuItem, is(notNullValue()));
		activity.onOptionsItemSelected(menuItem);
	}

}
