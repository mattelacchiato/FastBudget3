package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import android.content.Intent;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;

@RunWith(RobolectricTestRunner.class)
public class OverviewWithoutCategories {

	private Overview overview;

	@Before
	public void setUp() {
		this.overview = new Overview();
		overview.onCreate(null);
	}

	@Test
	public void containsAListWithOneHelpItem() throws Exception {
		assertThat(overview.getListView().getChildCount(), is(1));
	}

	@Test
	public void assignsAListAdapter() throws Exception {
		assertThat(overview.getListAdapter(), is(CategoryListAdapter.class));
	}

	@Test
	public void listFooter_click_categoryActivityStarts() throws Exception {
		ShadowActivity shadowActivity = shadowOf(overview);

		overview.findViewById(R.id.category_add).performClick();

		Intent startedIntent = shadowActivity.getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(CategoryActivity.class.getName()));
	}
}
