package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenu;
import org.robolectric.util.ActivityController;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

@RunWith(RobolectricTestRunner.class)
public class ExpenseActivityTest {

	private static final String CATEGORY_NAME = "Category One";

	private static final Date ANY_DATE = DateUtils.createFirstDayOfYear().getTime();

	private static final int ANY_BUDGET = 0;

	private ExpenseActivity expenseActivity;

	private ObjectContainer db;

	private TestMenu menu;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		OverviewActivity overviewActivity = buildActivity(OverviewActivity.class).create().get();
		Intent intent = new Intent(overviewActivity, ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
		ActivityController<ExpenseActivity> activityController = buildActivity(ExpenseActivity.class)
				.withIntent(intent);
		expenseActivity = activityController.get();

		db = Database.getInstance(expenseActivity);
		Database.clear();
		db.store(new Category("not me", ANY_BUDGET, ANY_DATE));
		db.store(new Category(CATEGORY_NAME, ANY_BUDGET, ANY_DATE));
		db.store(new Category("not me too", ANY_BUDGET, ANY_DATE));

		activityController.create();
		menu = new TestMenu();
		expenseActivity.onCreateOptionsMenu(menu);
		shadowOf(expenseActivity.findViewById(R.id.calculator)).callOnAttachedToWindow();
	}

	@Test
	public void hasLoadedCategory() {
		assertThat(expenseActivity.category, is(notNullValue()));
		assertThat(expenseActivity.category.name, is(CATEGORY_NAME));
	}

	@Test
	public void itShowsTheCategoryNameInTitle() {
		assertThat(expenseActivity.getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void itHasAFieldToEnterADescription() {
		assertThat(expenseActivity.findViewById(R.id.description), is(EditText.class));
	}

	@Test
	public void itHasACalculator() {
		assertThat(expenseActivity.findViewById(R.id.calculator), is(Calculator.class));
	}

	@Test
	public void itHasACancelButton() {
		assertThat(menu.findItem(R.id.cancel), is(notNullValue()));
	}

	@Test
	public void itHasASaveButton() {
		assertThat(menu.findItem(R.id.save), is(notNullValue()));
	}

	@Test
	public void itHasADatePicker() {
		assertThat(expenseActivity.findViewById(R.id.date_picker), is(DatePickerButtons.class));
	}

	@Test
	public void cancel_returnsToOverview() {
		expenseActivity.onOptionsItemSelected(menu.findItem(R.id.cancel));
		assertThat(expenseActivity.isFinishing(), is(true));
	}

	@Test
	public void cancel_resultIsCancelled() {
		expenseActivity.onOptionsItemSelected(menu.findItem(R.id.cancel));
		assertThat(shadowOf(expenseActivity).getResultCode(), is(Activity.RESULT_CANCELED));
	}

	@Test
	public void save_persistExpenseInDb() {
		String description = "stuff";
		setAmount("30");
		setDescription(description);

		//TODO (Dec 27, 2013): write helper method for clicking sth.
		expenseActivity.onOptionsItemSelected(menu.findItem(R.id.save));

		//TODO (Dec 27, 2013): use dao!
		Category category = (Category) db.queryByExample(new Category(CATEGORY_NAME)).get(0);
		Expense persistedExpense = null;
		for (Expense expense : category.expenses) {
			persistedExpense = expense;
		}
		assertThat(persistedExpense, is(notNullValue()));
		assertThat(persistedExpense.amount, is(3000));
		assertThat(persistedExpense.date, is(notNullValue()));
		assertThat(persistedExpense.description, is(description));
	}

	@Test
	public void save_sendsOkToOverivew() {
		setAmount("30");
		setDescription("stuff");

		expenseActivity.onOptionsItemSelected(menu.findItem(R.id.save));
		assertThat(expenseActivity.isFinishing(), is(true));
		assertThat(shadowOf(expenseActivity).getResultCode(), is(Activity.RESULT_OK));
	}

	@Test
	public void save_amountNotValid_errorShown() {
		setAmount("-.-..0");

		expenseActivity.onOptionsItemSelected(menu.findItem(R.id.save));

		assertToastIsShown(R.string.error_invalid_number);
	}

	@Test
	public void onCreate_noIntentGiven_isFinishing() {
		ExpenseActivity activityWithoutIntent = buildActivity(ExpenseActivity.class).create().get();
		assertThat(activityWithoutIntent.isFinishing(), is(true));
	}

	private void assertToastIsShown(int stringId) {
		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(expenseActivity.getString(stringId)));
	}

	private void setDescription(String description) {
		((EditText) expenseActivity.findViewById(R.id.description)).setText(description);
	}

	private void setAmount(String amount) {
		((EditText) expenseActivity.findViewById(R.id.calculator_amount)).setText(amount);
	}

}
