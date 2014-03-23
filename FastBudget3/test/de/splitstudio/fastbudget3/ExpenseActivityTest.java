package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isA;
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
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

@RunWith(RobolectricTestRunner.class)
public class ExpenseActivityTest {

	private static final String CATEGORY_NAME = "Category One";

	private static final Date ANY_DATE = DateUtils.createFirstDayOfYear().getTime();

	private static final int ANY_BUDGET = 0;

	private ExpenseActivity activity;

	private TestMenu menu;

	private CategoryDao categoryDao;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		CategoryListActivity categoryListActivity = buildActivity(CategoryListActivity.class).get();
		Intent intent = new Intent(categoryListActivity, ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
		ActivityController<ExpenseActivity> activityController = buildActivity(ExpenseActivity.class)
				.withIntent(intent);
		activity = activityController.get();

		ObjectContainer db = Database.getClearedInstance(activity);
		categoryDao = new CategoryDao(db);
		categoryDao.store(new Category("not me", ANY_BUDGET, ANY_DATE));
		categoryDao.store(new Category(CATEGORY_NAME, ANY_BUDGET, ANY_DATE));
		categoryDao.store(new Category("not me too", ANY_BUDGET, ANY_DATE));

		activityController.create();
		menu = new TestMenu();
		activity.onCreateOptionsMenu(menu);
		shadowOf(activity.findViewById(R.id.calculator)).callOnAttachedToWindow();
	}

	@Test
	public void hasLoadedCategory() {
		assertThat(activity.category, is(notNullValue()));
		assertThat(activity.category.name, is(CATEGORY_NAME));
	}

	@Test
	public void itShowsTheCategoryNameInTitle() {
		assertThat(activity.getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void itHasAFieldToEnterADescription() {
		assertThat((EditText) activity.findViewById(R.id.description), isA(EditText.class));
	}

	@Test
	public void itHasACalculator() {
		assertThat((Calculator) activity.findViewById(R.id.calculator), isA(Calculator.class));
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
		assertThat((DatePickerButtons) activity.findViewById(R.id.date_picker), isA(DatePickerButtons.class));
	}

	@Test
	public void cancel_returnsToCategoryList() {
		clickOnMenuItem(R.id.cancel);
		assertThat(activity.isFinishing(), is(true));
	}

	@Test
	public void cancel_resultIsCancelled() {
		clickOnMenuItem(R.id.cancel);
		assertThat(shadowOf(activity).getResultCode(), is(Activity.RESULT_CANCELED));
	}

	@Test
	public void save_persistExpenseInDb() {
		String description = "stuff";
		setAmount("30");
		setDescription(description);

		clickOnMenuItem(R.id.save);

		Category category = categoryDao.findByName(CATEGORY_NAME);
		Expense persistedExpense = category.getExpenses().get(0);
		assertThat(persistedExpense, is(notNullValue()));
		assertThat(persistedExpense.amount, is(3000));
		assertThat(persistedExpense.date, is(notNullValue()));
		assertThat(persistedExpense.description, is(description));
	}

	@Test
	public void save_sendsOkToOverivew() {
		setAmount("30");
		setDescription("stuff");

		clickOnMenuItem(R.id.save);
		assertThat(activity.isFinishing(), is(true));
		assertThat(shadowOf(activity).getResultCode(), is(Activity.RESULT_OK));
	}

	@Test
	public void save_amountNotValid_errorShown() {
		setAmount("-.-..0");

		clickOnMenuItem(R.id.save);

		assertToastIsShown(R.string.error_invalid_number);
	}

	@Test
	public void onCreate_noIntentGiven_isFinishing() {
		ExpenseActivity activityWithoutIntent = buildActivity(ExpenseActivity.class).create().get();
		assertThat(activityWithoutIntent.isFinishing(), is(true));
	}

	@Test
	public void itHasNoTextInAmount() throws Exception {
		assertThat(((EditText) activity.findViewById(R.id.calculator_amount)).getText().toString(), is(""));
	}

	private void assertToastIsShown(int stringId) {
		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(activity.getString(stringId)));
	}

	private void setDescription(String description) {
		((EditText) activity.findViewById(R.id.description)).setText(description);
	}

	private void setAmount(String amount) {
		((EditText) activity.findViewById(R.id.calculator_amount)).setText(amount);
	}

	private void clickOnMenuItem(int buttonId) {
		activity.onOptionsItemSelected(menu.findItem(buttonId));
	}
}
