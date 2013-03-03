package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowToast;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.utils.view.Calculator;

@RunWith(RobolectricTestRunner.class)
public class CategoryActivityTest {

	private static final String ANY_NAME = "category name";
	private CategoryActivity categoryActivity;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		this.categoryActivity = new CategoryActivity();
		categoryActivity.onCreate(null);
		categoryActivity.storage.init(10000);
	}

	@Test
	public void itHasAFieldToEnterTheCategoryName() throws Exception {
		View nameTextEdit = categoryActivity.findViewById(R.id.category_name);
		assertThat(nameTextEdit, is(notNullValue()));
		assertThat(nameTextEdit, is(EditText.class));
	}

	@Test
	public void categoryNameHasFocus() throws Exception {
		EditText nameEdit = (EditText) categoryActivity.findViewById(R.id.category_name);
		assertThat(nameEdit.hasFocus(), is(true));
	}

	@Test
	public void categoryNameHasAnHint() throws Exception {
		EditText nameEdit = (EditText) categoryActivity.findViewById(R.id.category_name);
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
		Button button = (Button) categoryActivity.findViewById(R.id.button_save);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void itHasACancelButton() {
		Button button = (Button) categoryActivity.findViewById(R.id.button_cancel);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void clickSave_goesBackToOverview() {
		ShadowActivity shadowActivity = shadowOf(categoryActivity);

		fillName("bla");
		clickSaveButton();

		Intent startedIntent = shadowActivity.getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(OverviewActivity.class.getName()));
	}

	@Test
	public void clickSave_complainsAboutEmptyName_noIntentStarted() {
		clickSaveButton();

		assertToastIsShown(R.string.error_name_empty);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_complainsAboutDuplicateName_noIntentStarted() {
		String name = "duplicatedName";
		categoryActivity.storage.push(new Category(name, 1));

		fillName(name);
		clickSaveButton();

		assertToastIsShown(R.string.error_name_duplicated);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_complainsAboutInvalidNumber() {
		fillName(ANY_NAME);
		fillBudget("-,.-");
		clickSaveButton();

		assertToastIsShown(R.string.error_invalid_number);
		assertNoIntentWasStarted();
	}

	@Test
	public void clickSave_addsCategoryToStorage() {

	}

	@Test
	public void cancelButton_nothingAdded() {

	}

	private void assertNoIntentWasStarted() {
		Intent startedIntent = shadowOf(categoryActivity).getNextStartedActivity();
		assertThat("An intend was started, but shouldn't", startedIntent, is(nullValue()));
	}

	private void clickSaveButton() {
		categoryActivity.findViewById(R.id.button_save).performClick();
	}

	private void fillName(String name) {
		EditText nameEdit = (EditText) categoryActivity.findViewById(R.id.category_name);
		nameEdit.setText(name);
	}

	private void fillBudget(String string) {
		EditText amountEdit = (EditText) categoryActivity.findViewById(R.id.calculator_amount);
		amountEdit.setText(string);

	}

	private void assertToastIsShown(int stringId) {
		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(categoryActivity.getString(stringId)));
	}

}
