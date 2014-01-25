package de.splitstudio.fastbudget3.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Calendar;
import java.util.Date;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.google.android.apps.common.testing.ui.espresso.DataInteraction;

import de.splitstudio.fastbudget3.CategoryListActivity;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.utils.db.Database;

public class ExpenseListActivityIntegrationTest extends FilledStateTestCase<CategoryListActivity> {

	public ExpenseListActivityIntegrationTest() {
		super(CategoryListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		openExpenseList();
	}

	private void openExpenseList() {
		onData(is(Category.class)).atPosition(0).perform(click());
		onData(is(Category.class)).atPosition(0).onChildView(withId(R.id.button_list)).perform(click());
	}

	@Override
	public Runnable updateAdapter() {
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
		expenseAt(0).perform(click());
		contextMenuAt(0).check(matches(isDisplayed()));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(not(isDisplayed())));

		expenseAt(2).perform(click());
		contextMenuAt(0).check(matches(not(isDisplayed())));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(isDisplayed()));
	}

	public void test_openExpenseActivity_goBack_allContextsAreGone() {
		expenseAt(0).perform(click());
		expenseAt(0).onChildView(withId(R.id.button_edit)).perform(click());
		onView(withId(R.id.cancel)).perform(click());

		contextMenuAt(0).check(matches(not(isDisplayed())));
	}

	public void test_deleteFirstItem_itemNotShown() {
		expenseAt(0).perform(click());
		expenseAt(0).onChildView(withId(R.id.button_delete)).perform(click());
		expenseAt(0).onChildView(withId(R.id.description)).check(matches(not(withText("first expenditure"))));
	}

	//This test is too whiteboxed for integration test. But Robolectric doesn't allow to test date buttons...
	public void test_editFirstItem_itemEditsVisible() throws Exception {
		final String newDescription = "changed description";
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstExpenditureDate);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date newDate = cal.getTime();

		expenseAt(0).onChildView(withId(R.id.description)).check(matches(withText("first expenditure")));
		expenseAt(0).perform(click());
		expenseAt(0).onChildView(withId(R.id.button_edit)).perform(click());

		onView(withId(R.id.date_minus)).perform(click());
		onView(withId(R.id.description)).perform(clearText()).perform(typeText(newDescription));
		onView(withId(R.id.button_delete)).perform(click());//0.1
		onView(withId(R.id.button_delete)).perform(click());//0.
		onView(withId(R.id.button_delete)).perform(click());//0
		onView(withId(R.id.button_delete)).perform(click());//
		onView(withId(R.id.button_9)).perform(click());//9
		onView(withId(R.id.button_9)).perform(click());//99

		onView(withId(R.id.save)).perform(click());

		expenseAt(0).onChildView(withId(R.id.description)).check(matches(withText(newDescription)));
		expenseAt(0).onChildView(withId(R.id.amount)).check(matches(withText("$99.00")));
		expenseAt(0).onChildView(withId(R.id.date_field)).check(matches(withText(formatAsShortDate(newDate))));

		assertExpensePersisted(newDescription, newDate);
	}

	public void test_clickMoveChooser_movesExpense() {
		expenseAt(0).onChildView(withId(R.id.description)).check(matches(withText("first expenditure")));
		expenseAt(0).perform(click());
		contextMenuAt(0).onChildView(withText(R.string.move)).perform(click());

		onView(withText(R.string.title_category_chooser)).check(matches(isDisplayed()));

		onView(withText("second category")).perform(click());
		expenseAt(0).onChildView(withId(R.id.description)).check(matches(not(withText("first expenditure"))));
	}

	@SuppressWarnings("serial")
	private void assertExpensePersisted(final String newDescription, Date newDate) {
		ObjectContainer db = Database.getInstance(getActivity());
		Expense expense = db.query(new Predicate<Expense>() {
			@Override
			public boolean match(Expense expense) {
				return expense.description.equals(newDescription);
			}
		}).next();

		assertThat(expense.amount, is(9900));
		assertThat(expense.description, is(newDescription));
		assertThat(expense.date, is(newDate));
	}

	private DataInteraction contextMenuAt(int position) {
		return expenseAt(position).onChildView(withId(R.id.context_row));
	}

	private DataInteraction expenseAt(int position) {
		return onData(is(Expense.class)).atPosition(position);
	}

}
