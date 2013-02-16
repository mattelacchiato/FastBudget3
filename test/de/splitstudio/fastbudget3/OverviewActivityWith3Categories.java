package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.widget.TextView;
import de.splitstudio.fastbudget3.db.Category;

@RunWith(RobolectricTestRunner.class)
public class OverviewActivityWith3Categories {

	private OverviewActivity overview;

	private static final String NAME1 = "First Category";
	private static final String NAME2 = "Second Category";
	private static final String NAME3 = "Third Category";

	@Before
	public void setUp() {
		this.overview = new OverviewActivity();
		Category category1 = new Category(NAME1);
		Category category2 = new Category(NAME2);
		Category category3 = new Category(NAME3);
		overview.categories.addAll(Arrays.asList(category1, category2, category3));
		overview.onCreate(null);
	}

	@Test
	public void hasAnAddView() throws Exception {
		assertThat(overview.getListView().findViewById(R.id.category_add), is(notNullValue()));
	}

	@Test
	public void containsAListWith3Items() throws Exception {
		assertThat(overview.getListView().getAdapter().getCount(), is(3));
	}

	@Test
	public void showsTheNameOfEachCategory() throws Exception {
		assertThatChildNameIs(0, NAME1);
		assertThatChildNameIs(1, NAME2);
		assertThatChildNameIs(2, NAME3);
	}

	private void assertThatChildNameIs(int position, String expected) {
		TextView name1 = (TextView) overview.getListView().getChildAt(position).findViewById(R.id.category_name);
		assertThat(name1, is(notNullValue()));
		assertThat(name1.getText().toString(), is(expected));
	}
}
