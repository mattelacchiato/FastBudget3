package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Locale;

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
		Locale.setDefault(Locale.US);
		this.overview = new OverviewActivity();
		Category category1 = new Category(NAME1, 111);
		Category category2 = new Category(NAME2, 222);
		Category category3 = new Category(NAME3, 333);
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
		assertThatTextAtPositionIs(R.id.category_name, 0, NAME1);
		assertThatTextAtPositionIs(R.id.category_name, 1, NAME2);
		assertThatTextAtPositionIs(R.id.category_name, 2, NAME3);
	}

	@Test
	public void showsTheAmountOfEachBudget() throws Exception {
		assertThatTextAtPositionIs(R.id.category_budget, 0, "$1.11");
		assertThatTextAtPositionIs(R.id.category_budget, 1, "$2.22");
		assertThatTextAtPositionIs(R.id.category_budget, 2, "$3.33");
	}

	private void assertThatTextAtPositionIs(int viewId, int position, String expected) {
		TextView name1 = (TextView) overview.getListView().getChildAt(position).findViewById(viewId);
		assertThat(name1, is(notNullValue()));
		assertThat(name1.getText().toString(), is(expected));
	}
}
