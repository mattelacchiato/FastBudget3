package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Calendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenu;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.utils.view.Calculator;

@RunWith(RobolectricTestRunner.class)
public class CategoryActivityTest {

	private static final String ANY_NAME = "category name";
	private static final int ANY_BUDGET = 100;

	private CategoryActivity categoryActivity;

	private ObjectContainer db;
	private Menu menu;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		categoryActivity = buildActivity(CategoryActivity.class).create().get();
		db = Database.getInstance(categoryActivity);
		Database.clear();
		menu = new TestMenu();
		categoryActivity.onCreateOptionsMenu(menu);
		shadowOf(categoryActivity.findViewById(R.id.calculator)).callOnAttachedToWindow();
	}

	@Test
	public void itHasAFieldToEnterTheCategoryName() throws Exception {
		View nameTextEdit = categoryActivity.findViewById(R.id.name);
		assertThat(nameTextEdit, is(notNullValue()));
		assertThat(nameTextEdit, is(EditText.class));
	}

	@Test
	public void categoryNameHasFocus() throws Exception {
		EditText nameEdit = (EditText) categoryActivity.findViewById(R.id.name);
		assertThat(nameEdit.hasFocus(), is(true));
	}

	@Test
	public void categoryNameHasAnHint() throws Exception {
		EditText nameEdit = (EditText) categoryActivity.findViewById(R.id.name);
		String expectedHint = categoryActivity.getString(R.string.hint_category_name);
		assertThat(nameEdit.getHint().toString(), is(expectedHint));
	}

	@Test
	public void titleIsSet() throws Exception {
		String expectedTitle = categoryActivity.getString(R.string.add_category);
		assertThat(categoryActivity.getTitle().toString(), is(expectedTitle));
	}

	@Test
	public void ithasACalculator() {
		View calculator = categoryActivity.findViewById(R.id.calculator);
		assertThat(calculator, is(notNullValue()));
		assertThat(calculator, is(Calculator.class));
	}

	@Test
	public void itHasASaveButton() {
		assertThat(menu.findItem(R.id.save), is(notNullValue()));
	}

	@Test
	public void itHasACancelButton() {
		assertThat(menu.findItem(R.id.cancel), is(notNullValue()));
	}

	@Test
	public void clickSave_goesBackToOverview() {
		ShadowActivity shadowActivity = shadowOf(categoryActivity);

		fillBudget("1");
		fillName("bla");
		clickMenuItem(R.id.save);

		assertThat(shadowActivity.isFinishing(), is(true));
	}

	@Test
	public void clickSave_complainsAboutEmptyName_noIntentStarted() {
		clickMenuItem(R.id.save);

		assertToastIsShown(R.string.error_name_empty);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_complainsAboutDuplicateName_noIntentStarted() {
		String name = "duplicatedName";
		db.store(new Category(name, ANY_BUDGET, null));

		fillName(name);
		fillBudget("1.00");
		clickMenuItem(R.id.save);

		assertToastIsShown(R.string.error_name_duplicated);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_complainsAboutInvalidNumber() {
		fillName(ANY_NAME);
		fillBudget("-,.-");
		clickMenuItem(R.id.save);

		assertToastIsShown(R.string.error_invalid_number);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_addsCategoryToStorage() {
		assertThat(db.query().execute(), is(empty()));

		fillName(ANY_NAME);
		fillBudget("1.00");
		clickMenuItem(R.id.save);

		Category category = db.query(Category.class).get(0);
		assertThat(category, is(notNullValue()));
		assertThat(category.name, is(ANY_NAME));
		assertThat(category.budget, is(100));
		assertThat(category.date, is(notNullValue()));
	}

	@Test
	public void cancelButton_nothingAdded() {
		clickMenuItem(R.id.cancel);
		assertThat(db.query().execute(), is(empty()));
	}

	@Test
	public void cancel_resultIsCancelled() {
		clickMenuItem(R.id.cancel);
		assertThat(shadowOf(categoryActivity).getResultCode(), is(Activity.RESULT_CANCELED));
	}

	@Test
	public void itHasADatePicker() {
		assertThat(categoryActivity.findViewById(R.id.date_picker), is(notNullValue()));
		assertThat(categoryActivity.findViewById(R.id.category_date_hint), is(TextView.class));
		assertThat(((TextView) categoryActivity.findViewById(R.id.category_date_hint)).getText().toString(),
			is(not("")));
	}

	@Test
	public void itsDatePickerIsInitializedWith_1stJan() {
		Button button = (Button) categoryActivity.findViewById(R.id.date_field);

		int year = Calendar.getInstance().get(Calendar.YEAR);
		assertThat(button.getText().toString(), is("January 1, " + year));
	}

	private void assertNoIntentWasStarted() {
		Intent startedIntent = shadowOf(categoryActivity).getNextStartedActivity();
		assertThat("An intend was started, but shouldn't", startedIntent, is(nullValue()));
	}

	private void clickMenuItem(int itemId) {
		categoryActivity.onOptionsItemSelected(menu.findItem(itemId));
	}

	private void fillName(String name) {
		((EditText) categoryActivity.findViewById(R.id.name)).setText(name);
	}

	private void fillBudget(String string) {
		((EditText) categoryActivity.findViewById(R.id.calculator_amount)).setText(string);
	}

	private void assertToastIsShown(int stringId) {
		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(categoryActivity.getString(stringId)));
	}

}
