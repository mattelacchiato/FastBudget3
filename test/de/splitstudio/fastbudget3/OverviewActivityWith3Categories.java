package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.enums.Extras;

@RunWith(RobolectricTestRunner.class)
public class OverviewActivityWith3Categories {

	private OverviewActivity overview;

	private ObjectContainer db;

	private static final String NAME1 = "aFirst Category";
	private static final String NAME2 = "bSecond Category";
	private static final String NAME3 = "cThird Category";

	private Category category1;
	private Category category2;
	private Category category3;

	private TestMenu menu;

	@Before
	public void setUp() {
		overview = new OverviewActivity();
		db = Database.getInstance(overview);
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
		overview.onCreate(null);
		menu = new TestMenu();
		overview.onCreateOptionsMenu(menu);
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

	@Test
	public void setsSumOfAllExpenditures() {
		category1.expenditures.add(new Expenditure(20, new Date(), null));
		category1.expenditures.add(new Expenditure(40, new Date(), null));
		overview.requeryCategories();

		TextView spent = (TextView) overview.findViewById(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.60"));
	}

	@Test
	public void expenditureAdded_refreshUI() {
		overview.findViewById(R.id.button_add_expenditure).performClick();

		category1.expenditures.add(new Expenditure(20, new Date(), null));
		db.store(category1);
		Intent intent = new Intent(overview, ExpenditureActivity.class);
		intent.putExtra(Extras.CategoryName.name(), category1.name);
		shadowOf(overview).receiveResult(intent, Activity.RESULT_OK, null);

		TextView spent = (TextView) overview.findViewById(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.20"));
	}

	@Test
	public void setsProgressBar() {
		category1.expenditures.add(new Expenditure(20, new Date(), null));
		db.store(category1);
		overview.requeryCategories();

		ProgressBar progressBar = (ProgressBar) overview.findViewById(R.id.category_fill);
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

		assertThatTextAtPositionIs(R.id.name, 0, NAME2);
		assertThatTextAtPositionIs(R.id.name, 1, NAME1);
		assertThatTextAtPositionIs(R.id.name, 2, NAME3);
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
	@Ignore
	public void itShowsTheNetBudget() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		category1.expenditures.add(new Expenditure(20, cal.getTime(), ""));
		db.store(category1);

		overview.requeryCategories();

		assertThatTextAtPositionIs(R.id.category_budget, 0, "$0.91");
	}

	@Test
	public void deleteCategory_opensConfirmationBox() {
		overview.findViewById(R.id.delete_category).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		assertThat(dialog, is(notNullValue()));
	}

	@Test
	public void deleteCategory_cancelDoesNothing() {
		overview.findViewById(R.id.delete_category).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		assertThat(dialog.isShowing(), is(true));

		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();

		assertThat(dialog.isShowing(), is(false));
		assertThat(db.ext().isStored(category1), is(true));
		assertThatTextAtPositionIs(R.id.name, 0, NAME1);
	}

	@Test
	public void deleteCategory_okDeletesCategoryFromDb() {
		overview.findViewById(R.id.delete_category).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThat(db.ext().isStored(category1), is(false));
	}

	@Test
	public void deleteCategory_okDeletesCategorysExpendituresFromDb() {
		Expenditure expenditure = new Expenditure(20, new Date(), null);
		category1.expenditures.add(expenditure);
		db.store(category1.expenditures);
		overview.findViewById(R.id.delete_category).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThat(db.ext().isStored(expenditure), is(false));
	}

	@Test
	public void deleteCategory_requeriesList() {
		overview.findViewById(R.id.delete_category).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThatTextAtPositionIs(R.id.name, 0, NAME2);
	}

	//TODO requery

	private void assertThatTextAtPositionIs(int viewId, int position, String expected) {
		TextView name1 = (TextView) overview.getListView().getChildAt(position).findViewById(viewId);
		assertThat(name1, is(notNullValue()));
		assertThat("At Position " + position, name1.getText().toString(), is(expected));
	}
}
