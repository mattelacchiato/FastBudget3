package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;

import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.enums.Extras;

@RunWith(RobolectricTestRunner.class)
public class OverviewActivityWith3Categories {

	private OverviewActivity overview;

	private ObjectContainer db;

	private static final String NAME1 = "First Category";
	private static final String NAME2 = "Second Category";
	private static final String NAME3 = "Third Category";

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		overview = new OverviewActivity();
		db = Database.getInstance(overview);
		Database.clear();

		db.store(new Category(NAME1, 111));
		db.store(new Category(NAME2, 222));
		db.store(new Category(NAME3, 333));
		overview.onCreate(null);
	}

	@Test
	public void hasAnAddView() throws Exception {
		assertThat(overview.findViewById(R.id.button_add_category), is(notNullValue()));
	}

	@Test
	public void containsAListWith3Items() throws Exception {
		assertThat(overview.getListView().getAdapter().getCount(), is(3));
	}

	@Test
	public void showsTheNameOfEachCategory() throws Exception {
		assertThatTextAtPositionIs(R.id.name, 0, NAME1);
		assertThatTextAtPositionIs(R.id.name, 1, NAME2);
		assertThatTextAtPositionIs(R.id.name, 2, NAME3);
	}

	@Test
	public void showsTheAmountOfEachBudget() throws Exception {
		assertThatTextAtPositionIs(R.id.category_budget, 0, "$1.11");
		assertThatTextAtPositionIs(R.id.category_budget, 1, "$2.22");
		assertThatTextAtPositionIs(R.id.category_budget, 2, "$3.33");
	}

	@Test
	public void add_sendsCategoryNameToExpenditureActivity() {
		overview.findViewById(R.id.button_add_expenditure).performClick();

		ShadowIntent shadowIntent = shadowOf(shadowOf(overview).getNextStartedActivity());
		assertThat(shadowIntent.getExtras(), is(notNullValue()));
		assertThat(shadowIntent.getExtras().getString(Extras.CategoryName.name()), is(NAME1));
	}

	private void assertThatTextAtPositionIs(int viewId, int position, String expected) {
		TextView name1 = (TextView) overview.getListView().getChildAt(position).findViewById(viewId);
		assertThat(name1, is(notNullValue()));
		assertThat(name1.getText().toString(), is(expected));
	}
}
