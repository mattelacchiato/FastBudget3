package de.splitstudio.fastbudget3;

import static de.splitstudio.fastbudget3.enums.Extras.CategoryName;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

@RunWith(RobolectricTestRunner.class)
public class CategoryActivityWithIntentTest {

	private static final Date ANY_DATE = new Date();
	private static final String ANY_NAME = "category name";
	private static final int ANY_BUDGET = 100;

	private CategoryActivity categoryActivity;

	private ObjectContainer db;
	private Menu menu;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);

		ActivityController<OverviewActivity> overviewController = buildActivity(OverviewActivity.class);
		initDb(overviewController.get().getApplicationContext());
		OverviewActivity overviewActivity = overviewController.create().get();
		Intent intent = new Intent(overviewActivity, CategoryActivity.class);
		intent.putExtra(CategoryName.name(), ANY_NAME);
		ActivityController<CategoryActivity> categoryController = buildActivity(CategoryActivity.class).withIntent(
			intent);

		categoryActivity = categoryController.get();
		menu = new TestMenu();
		categoryController.create();
		categoryActivity.onCreateOptionsMenu(menu);
		shadowOf(categoryActivity.findViewById(R.id.calculator)).callOnAttachedToWindow();
	}

	private void initDb(Context context) {
		//TODO (Dec 22, 2013): create method getClearedInstance(..)?
		db = Database.getInstance(context);
		Database.clear();
		db.store(new Category(ANY_NAME, ANY_BUDGET, ANY_DATE));
	}

	@Test
	public void itDisplaysCategory() {
		String name = ((EditText) categoryActivity.findViewById(R.id.name)).getText().toString();
		String amount = ((Calculator) categoryActivity.findViewById(R.id.calculator)).getAmount();
		DatePickerButtons datePicker = (DatePickerButtons) categoryActivity.findViewById(R.id.date_picker);

		assertThat(name, is(ANY_NAME));
		assertThat(datePicker.getDate().getTime(), is(ANY_DATE));
		assertThat(amount, is("1.00"));
	}

	@Test
	public void changeName_updatesCategory() {
		String newName = "new category name";

		fillName(newName);
		clickMenuItem(R.id.save);

		Category oldCategory = Database.findCategory(ANY_NAME);
		assertThat(oldCategory, is(nullValue()));

		Category persistedCategory = Database.findCategory(newName);
		assertThat(persistedCategory, is(notNullValue()));
		assertThat(persistedCategory.name, is(newName));
		assertThat(persistedCategory.budget, is(ANY_BUDGET));
	}

	@Test
	public void changeBudget_updatesCategory() {
		fillBudget("2.33");
		clickMenuItem(R.id.save);

		assertThat(ShadowToast.getTextOfLatestToast(), is(nullValue()));

		Category persistedCategory = Database.findCategory(ANY_NAME);
		assertThat(persistedCategory, is(notNullValue()));
		assertThat(persistedCategory.budget, is(233));
	}

	@Test
	public void titleIsSet() throws Exception {
		String expectedTitle = categoryActivity.getString(R.string.edit_category);
		assertThat(categoryActivity.getTitle().toString(), is(expectedTitle));
	}

	private void assertNoIntentWasStarted() {
		Intent startedIntent = shadowOf(categoryActivity).getNextStartedActivity();
		assertThat("An intend was started, but shouldn't", startedIntent, is(nullValue()));
	}

	private void clickMenuItem(int itemId) {
		MenuItem menuItem = menu.findItem(itemId);
		assertThat(menuItem, is(notNullValue()));
		categoryActivity.onOptionsItemSelected(menuItem);
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
