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
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

@RunWith(RobolectricTestRunner.class)
public class CategoryActivity_WithIntent_Test {

	private static final Date ANY_DATE = new Date();
	private static final String ANY_NAME = "category name";
	private static final int ANY_BUDGET = 100;

	private CategoryActivity categoryActivity;

	private ObjectContainer db;
	private Menu menu;
	private CategoryDao categoryDao;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);

		ActivityController<CategoryListActivity> categoryListController = buildActivity(CategoryListActivity.class);
		initDb(categoryListController.get().getApplicationContext());
		CategoryListActivity categoryListActivity = categoryListController.create().get();
		Intent intent = new Intent(categoryListActivity, CategoryActivity.class);
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
		db = Database.getClearedInstance(context);
		categoryDao = new CategoryDao(db);
		categoryDao.store(new Category(ANY_NAME, ANY_BUDGET, ANY_DATE));
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
	public void changeName_persisted() {
		String newName = "new category name";

		fillName(newName);
		clickMenuItem(R.id.save);

		Category oldCategory = categoryDao.findByName(ANY_NAME);
		assertThat(oldCategory, is(nullValue()));

		Category persistedCategory = categoryDao.findByName(newName);
		assertThat(persistedCategory, is(notNullValue()));
		assertThat(persistedCategory.name, is(newName));
		assertThat(persistedCategory.budget, is(ANY_BUDGET));
	}

	@Test
	public void changeBudget_persisted() {
		fillBudget("2.33");
		clickMenuItem(R.id.save);

		assertThat(ShadowToast.getTextOfLatestToast(), is(nullValue()));

		Category persistedCategory = categoryDao.findByName(ANY_NAME);
		assertThat(persistedCategory, is(notNullValue()));
		assertThat(persistedCategory.budget, is(233));
	}

	@Test
	public void onCreate_titleIsSet() throws Exception {
		String expectedTitle = categoryActivity.getString(R.string.edit);
		assertThat(categoryActivity.getTitle().toString(), is(expectedTitle));
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

}
