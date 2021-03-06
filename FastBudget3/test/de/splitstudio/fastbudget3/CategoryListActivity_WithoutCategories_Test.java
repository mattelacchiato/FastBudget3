package de.splitstudio.fastbudget3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenu;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.utils.db.Database;

@RunWith(RobolectricTestRunner.class)
public class CategoryListActivity_WithoutCategories_Test {

	private CategoryListActivity categoryList;
	private ObjectContainer db;
	private Menu menu;
	private CategoryDao categoryDao;
	private ActivityController<CategoryListActivity> activityController;

	@Before
	public void setUp() {
		activityController = buildActivity(CategoryListActivity.class);
		categoryList = activityController.get();
		db = Database.getClearedInstance(categoryList);
		categoryDao = new CategoryDao(db);
		activityController.create();
		menu = new TestMenu();
		categoryList.onCreateOptionsMenu(menu);
	}

	@Test
	public void menu_has_createCategory() throws Exception {
		menu.findItem(R.id.button_create_category);
	}

	@Test
	public void menu_has_createCsv() throws Exception {
		menu.findItem(R.id.button_create_csv);
	}

	@Test
	public void hasAnEmptyList() throws Exception {
		assertThat(categoryList.getListView().getAdapter().getCount(), is(0));
	}

	@Test
	public void assignsAListAdapter() throws Exception {
		assertThat(categoryList.getListAdapter(), is(not(nullValue())));
	}

	@Test
	public void addCategoryClick_categoryActivityStarts() throws Exception {
		categoryList.onOptionsItemSelected(menu.findItem(R.id.button_create_category));

		Intent startedIntent = shadowOf(categoryList).getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(CategoryActivity.class.getName()));
	}

	@Test
	public void createCategory_click_dbChange_resultRecieved_listUpdate() throws Exception {
		categoryList.onOptionsItemSelected(menu.findItem(R.id.button_create_category));

		String categoryName = "i was added";
		categoryDao.store(new Category(categoryName, 123, new Date()));
		activityController.resume();

		TextView name1 = (TextView) findListView(R.id.name);
		assertThat(name1, is(notNullValue()));
		assertThat(name1.getText().toString(), is(categoryName));
	}

	private View findListView(int viewId) {
		assertThat(categoryList.getListAdapter().getCount()).isGreaterThan(0);
		return categoryList.getListAdapter().getView(0, null, null).findViewById(viewId);
	}
}
