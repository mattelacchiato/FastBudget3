package de.splitstudio.fastbudget3;

import static android.view.View.GONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenu;
import org.robolectric.util.ActivityController;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.enums.Extras;

@RunWith(RobolectricTestRunner.class)
public class OverviewActivityWith3CategoriesTest {

	private OverviewActivity overview;

	private ObjectContainer db;

	private static final String NAME1 = "aFirst Category";
	private static final String NAME2 = "bSecond Category";
	private static final String NAME3 = "cThird Category";

	private Category category1;
	private Category category2;
	private Category category3;

	private TestMenu menu;

	private ActivityController<OverviewActivity> activityController;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(OverviewActivity.class);
		overview = activityController.get();
		db = Database.getInstance(overview.getApplicationContext());
		Database.clear();

		Calendar started = Calendar.getInstance();
		started.add(Calendar.MONTH, -1);
		Date now = new Date();
		category1 = new Category(NAME1, 111, now);
		category2 = new Category(NAME2, 222, now);
		category3 = new Category(NAME3, 333, now);
		db.store(category1);
		db.store(category2);
		db.store(category3);
		db.commit();
		activityController.create();
		menu = new TestMenu();
		overview.onCreateOptionsMenu(menu);
		assertThat(overview.getListAdapter().getCount(), is(greaterThan(0)));
	}

	@Test
	public void hasAnAddView() throws Exception {
		assertThat(menu.findItem(R.id.add_category), is(notNullValue()));
	}

	@Test
	public void containsAListWith3Items() throws Exception {
		assertThat(overview.getListView().getAdapter().getCount(), is(3));
	}

	@Test
	public void showsTheNameOfEachCategory() throws Exception {
		assertThatTextAtPositionIs(0, R.id.name, NAME1);
		assertThatTextAtPositionIs(1, R.id.name, NAME2);
		assertThatTextAtPositionIs(2, R.id.name, NAME3);
	}

	@Test
	public void showsTheAmountOfEachBudget() throws Exception {
		assertThatTextAtPositionIs(0, R.id.category_budget, "$1.11");
		assertThatTextAtPositionIs(1, R.id.category_budget, "$2.22");
		assertThatTextAtPositionIs(2, R.id.category_budget, "$3.33");
	}

	@Test
	public void addExpenditure_sendsCategoryNameToExpenditureActivity() {
		findListView(R.id.button_add_expenditure).performClick();

		Intent nextStartedActivity = shadowOf(overview).getNextStartedActivity();
		assertThat("Activity was not started", nextStartedActivity, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(nextStartedActivity);
		assertThat(shadowIntent.getExtras(), is(notNullValue()));
		assertThat(shadowIntent.getExtras().getString(Extras.CategoryName.name()), is(NAME1));
	}

	@Test
	public void editCategory_sendsCategoryNameToCategoryActivity() {
		findListView(R.id.button_edit).performClick();

		Intent nextStartedActivity = shadowOf(overview).getNextStartedActivity();
		assertThat("Activity was not started", nextStartedActivity, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(nextStartedActivity);
		assertThat(shadowIntent.getExtras(), is(notNullValue()));
		assertThat(shadowIntent.getExtras().getString(Extras.CategoryName.name()), is(NAME1));
	}

	@Test
	public void setsSumOfAllExpenditures() {
		category1.expenditures.add(new Expenditure(20, new Date(), null));
		category1.expenditures.add(new Expenditure(40, new Date(), null));
		overview.requeryCategories();

		TextView spent = (TextView) findListView(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.60"));
	}

	@Test
	public void expenditureAdded_refreshUI() {
		findListView(R.id.button_add_expenditure).performClick();

		category1.expenditures.add(new Expenditure(20, new Date(), null));
		db.store(category1);
		Intent intent = new Intent(overview, ExpenditureActivity.class);
		intent.putExtra(Extras.CategoryName.name(), category1.name);
		shadowOf(overview).receiveResult(intent, Activity.RESULT_OK, null);

		TextView spent = (TextView) findListView(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.20"));
	}

	@Test
	public void setsProgressBar() {
		category1.expenditures.add(new Expenditure(20, new Date(), null));
		db.store(category1);
		overview.requeryCategories();

		ProgressBar progressBar = (ProgressBar) findListView(R.id.category_fill);
		assertThat(progressBar.getMax(), is(category1.budget));
		assertThat(progressBar.getProgress(), is(20));
	}

	@Test
	public void itSortsCategoriesByTheirExpenditureCount() {
		category1.expenditures.add(new Expenditure(0, new Date(), ""));
		category2.expenditures.add(new Expenditure(0, new Date(), ""));
		category2.expenditures.add(new Expenditure(0, new Date(), ""));
		db.store(category1);
		db.store(category2);

		overview.requeryCategories();

		assertThatTextAtPositionIs(0, R.id.name, NAME2);
		assertThatTextAtPositionIs(1, R.id.name, NAME1);
		assertThatTextAtPositionIs(2, R.id.name, NAME3);
	}

	@Test
	public void itShowsTotalBudgetForThisMonthInTitle() {
		assertThat(overview.getTitle().toString(), containsString("$7"));
	}

	@Test
	public void itShowsTotalSpentsForThisMonthInTitle() {
		category1.expenditures.add(new Expenditure(20, new Date(), ""));
		category2.expenditures.add(new Expenditure(200, new Date(), ""));
		overview.requeryCategories();
		assertThat(overview.getTitle().toString(), containsString("$2"));
	}

	@Test
	public void itShowsTheNetBudget() {
		assertThatTextAtPositionIs(0, R.id.category_budget, "$1.11");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		category1.expenditures.add(new Expenditure(20, cal.getTime(), ""));
		db.store(category1);

		overview.requeryCategories();

		assertThatTextAtPositionIs(0, R.id.category_budget, "$0.91");
	}

	@Test
	public void deleteCategory_opensConfirmationBox() {
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		assertThat(dialog, is(notNullValue()));
	}

	@Test
	public void deleteCategory_cancelDoesNothing() {
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		assertThat(dialog.isShowing(), is(true));

		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();

		assertThat(dialog.isShowing(), is(false));
		assertThat(db.ext().isStored(category1), is(true));
		assertThatTextAtPositionIs(0, R.id.name, NAME1);
	}

	@Test
	public void deleteCategory_okDeletesCategoryFromDb() {
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThat(db.ext().isStored(category1), is(false));
	}

	@Test
	public void deleteCategory_okDeletesCategorysExpendituresFromDb() {
		Expenditure expenditure = new Expenditure(20, new Date(), null);
		category1.expenditures.add(expenditure);
		db.store(category1.expenditures);
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThat(db.ext().isStored(expenditure), is(false));
	}

	@Test
	public void deleteCategory_requeriesList() {
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThatTextAtPositionIs(0, R.id.name, NAME2);
		//TODO (07.09.2013): assert that all contexts are collapsed
	}

	@Test
	public void hidesContextRow() {
		activityController.start();
		assertThat(findListView(R.id.context_row).getVisibility(), is(GONE));
	}

	@Test
	public void clickOnListIcon_opensExpenditureListActivity() {
		activityController.start();
		findListView(R.id.button_list).performClick();

		Intent startedIntent = shadowOf(overview).getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		assertThat(startedIntent.getExtras().isEmpty(), is(false));
		assertThat((String) startedIntent.getExtras().get(Extras.CategoryName.name()), is(category1.name));

		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(ExpenditureListActivity.class.getName()));
	}

	private void assertThatTextAtPositionIs(int position, int viewId, String expected) {
		TextView name1 = (TextView) overview.getListAdapter().getView(position, null, null).findViewById(viewId);
		assertThat(name1, is(notNullValue()));
		assertThat("At Position " + position, name1.getText().toString(), is(expected));
	}

	private View findListView(int viewId) {
		return findListView(0, viewId);
	}

	private View findListView(int position, int viewId) {
		return overview.getListAdapter().getView(position, null, overview.getListView()).findViewById(viewId);
	}
}
