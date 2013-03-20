package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenu;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.Database;

@RunWith(RobolectricTestRunner.class)
public class OverviewActivityTestWithoutCategories {

	private OverviewActivity overview;
	private ObjectContainer db;
	private Menu menu;

	@Before
	public void setUp() {
		this.overview = new OverviewActivity();
		db = Database.getInstance(overview);
		Database.clear();
		overview.onCreate(null);
		menu = new TestMenu();
		overview.onCreateOptionsMenu(menu);
	}

	@Test
	public void hasAnAddView() throws Exception {
		menu.findItem(R.id.add_category);
	}

	@Test
	public void hasAnEmptyList() throws Exception {
		assertThat(overview.getListView().getAdapter().getCount(), is(0));
	}

	@Test
	public void assignsAListAdapter() throws Exception {
		assertThat(overview.getListAdapter(), is(CategoryListAdapter.class));
	}

	@Test
	public void addCategoryClick_categoryActivityStarts() throws Exception {
		overview.onOptionsItemSelected(menu.findItem(R.id.add_category));

		Intent startedIntent = shadowOf(overview).getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(CategoryActivity.class.getName()));
	}

	@Test
	public void listFooter_click_dbChange_resultRecieved_listUpdate() throws Exception {
		overview.onOptionsItemSelected(menu.findItem(R.id.add_category));

		String categoryName = "i was added";
		db.store(new Category(categoryName, 123, new Date()));
		shadowOf(overview).receiveResult(new Intent(overview, CategoryActivity.class), Activity.RESULT_OK, null);

		TextView name1 = (TextView) overview.getListView().getChildAt(0).findViewById(R.id.name);
		assertThat(name1, is(notNullValue()));
		assertThat(name1.getText().toString(), is(categoryName));
	}
}
