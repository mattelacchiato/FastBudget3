package de.splitstudio.fastbudget3.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.android.apps.common.testing.ui.espresso.DataInteraction;

import de.splitstudio.fastbudget3.CategoryListActivity;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Expense;

public class ExpenseListActivityIntegrationTest extends FilledStateTestCase<CategoryListActivity> {

	public ExpenseListActivityIntegrationTest() {
		super(CategoryListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		onData(is(Category.class)).atPosition(0).perform(click());
		onData(is(Category.class)).atPosition(0).onChildView(withId(R.id.button_list)).perform(click());
	}

	@Override
	public Runnable syncAdapter() {
		return new Runnable() {
			@Override
			public void run() {
				initialActivity.updateView();
			}
		};
	}

	public void testAllContextMenuAreHidden() {
		for (int position = 0; position < 3; ++position) {
			contextMenuAt(position).check(matches(not(isDisplayed())));
		}
	}

	public void testClickOnItemShowsContextMenuAndHidesOldOne() {
		expenditureAt(0).perform(click());
		contextMenuAt(0).check(matches(isDisplayed()));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(not(isDisplayed())));

		expenditureAt(2).perform(click());
		contextMenuAt(0).check(matches(not(isDisplayed())));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(isDisplayed()));
	}

	public void test_deleteFirstItem_itemNotShown() {
		expenditureAt(0).onChildView(withId(R.id.description)).check(matches(withText("first expenditure")));
		expenditureAt(0).perform(click());
		expenditureAt(0).onChildView(withId(R.id.button_delete)).perform(click());
		expenditureAt(0).onChildView(withId(R.id.description)).check(matches(not(withText("first expenditure"))));
	}

	private DataInteraction contextMenuAt(int position) {
		return expenditureAt(position).onChildView(withId(R.id.context_row));
	}

	private DataInteraction expenditureAt(int position) {
		return onData(is(Expense.class)).atPosition(position);
	}

}
