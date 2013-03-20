package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenu;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

@RunWith(RobolectricTestRunner.class)
public class ExpenditureActivityTest {

	private static final String CATEGORY_NAME = "Category One";

	private ExpenditureActivity expenditureActivity;

	private ObjectContainer db;

	private TestMenu menu;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		expenditureActivity = new ExpenditureActivity();
		Intent intent = new Intent(new OverviewActivity(), ExpenditureActivity.class);
		intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);

		db = Database.getInstance(expenditureActivity);
		Database.clear();
		db.store(new Category("not me"));
		db.store(new Category(CATEGORY_NAME));
		db.store(new Category("not me too"));

		expenditureActivity.setIntent(intent);
		expenditureActivity.onCreate(null);
		menu = new TestMenu();
		expenditureActivity.onCreateOptionsMenu(menu);
	}

	@Test
	public void hasLoadedCategory() {
		assertThat(expenditureActivity.category, is(notNullValue()));
		assertThat(expenditureActivity.category.name, is(CATEGORY_NAME));
	}

	@Test
	public void itShowsTheCategoryNameInTitle() {
		assertThat(expenditureActivity.getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void itHasAFieldToEnterADescription() {
		assertThat(expenditureActivity.findViewById(R.id.description), is(EditText.class));
	}

	@Test
	public void itHasACalculator() {
		assertThat(expenditureActivity.findViewById(R.id.calculator), is(Calculator.class));
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
		assertThat(expenditureActivity.findViewById(R.id.date_picker), is(DatePickerButtons.class));
	}

	@Test
	public void cancel_returnsToOverview() {
		expenditureActivity.onOptionsItemSelected(menu.findItem(R.id.cancel));
		assertThat(expenditureActivity.isFinishing(), is(true));
	}

	@Test
	public void cancel_resultIsCancelled() {
		expenditureActivity.onOptionsItemSelected(menu.findItem(R.id.cancel));
		assertThat(shadowOf(expenditureActivity).getResultCode(), is(Activity.RESULT_CANCELED));
	}

	@Test
	public void save_persistExpenditureInDb() {
		String description = "stuff";
		setAmount("30");
		setDescription(description);

		expenditureActivity.onOptionsItemSelected(menu.findItem(R.id.save));

		Category category = (Category) db.queryByExample(new Category(CATEGORY_NAME)).get(0);
		assertThat(category.expenditures.get(0), is(notNullValue()));
		assertThat(category.expenditures.get(0).amount, is(3000));
		assertThat(category.expenditures.get(0).date, is(notNullValue()));
		assertThat(category.expenditures.get(0).description, is(description));
	}

	@Test
	public void save_sendsOkToOverivew() {
		setAmount("30");
		setDescription("stuff");

		expenditureActivity.onOptionsItemSelected(menu.findItem(R.id.save));
		assertThat(expenditureActivity.isFinishing(), is(true));
		assertThat(shadowOf(expenditureActivity).getResultCode(), is(Activity.RESULT_OK));
	}

	private void setDescription(String description) {
		((EditText) expenditureActivity.findViewById(R.id.description)).setText(description);
	}

	private void setAmount(String amount) {
		((EditText) expenditureActivity.findViewById(R.id.calculator_amount)).setText(amount);
	}

	@Test
	public void save_amountNotValid_errorShown() {
		setAmount("-.-..0");

		expenditureActivity.onOptionsItemSelected(menu.findItem(R.id.save));

		assertToastIsShown(R.string.error_invalid_number);
	}

	private void assertToastIsShown(int stringId) {
		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(expenditureActivity.getString(stringId)));
	}

}
