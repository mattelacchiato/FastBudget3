package de.splitstudio.fastbudget3.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
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

public class CategoryListActivityIntegrationTest extends FilledStateTestCase<CategoryListActivity> {

	private static final String CATEGORY_NAME = "My Category";

	public CategoryListActivityIntegrationTest() {
		super(CategoryListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		loadListView();
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

	private void loadListView() {
		onData(is(Object.class));
	}

	public void testCreateACategory() {
		onView(withText(CATEGORY_NAME)).check(doesNotExist());

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.add_category)).perform(click());
		//TODO (Dec 20, 2013): find a way to check the title
		onView(withId(R.id.category_date_hint)).check(matches(isDisplayed()));

		onView(withId(R.id.name)).perform(typeText(CATEGORY_NAME));
		onView(withId(R.id.button_2)).perform(click());
		onView(withId(R.id.save)).perform(click());

		onView(withText(CATEGORY_NAME)).check(matches(isDisplayed()));
	}

	public void testAllContextMenuAreHidden() {
		for (int position = 0; position < 3; ++position) {
			contextMenuAt(position).check(matches(not(isDisplayed())));
		}
	}

	public void testClickOnItemShowsContextMenuAndHidesOldOne() {
		onData(is(Category.class)).atPosition(0).perform(click());
		contextMenuAt(0).check(matches(isDisplayed()));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(not(isDisplayed())));

		onData(is(Category.class)).atPosition(2).perform(click());
		contextMenuAt(0).check(matches(not(isDisplayed())));
		contextMenuAt(1).check(matches(not(isDisplayed())));
		contextMenuAt(2).check(matches(isDisplayed()));
	}

	public void test_openCategoryActivity_goBack_allContextsAreGone() {
		onData(is(Category.class)).atPosition(0).perform(click());
		onData(is(Category.class)).atPosition(0).onChildView(withId(R.id.button_edit)).perform(click());
		onView(withId(R.id.cancel)).perform(click());

		contextMenuAt(0).check(matches(not(isDisplayed())));
	}

	private DataInteraction contextMenuAt(int position) {
		return onData(is(Category.class)).atPosition(position).onChildView(withId(R.id.context_row));
	}

}
