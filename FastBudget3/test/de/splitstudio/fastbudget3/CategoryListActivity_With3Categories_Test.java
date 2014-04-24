package de.splitstudio.fastbudget3;

import static android.view.View.GONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;
import static org.robolectric.shadows.ShadowAlertDialog.getLatestAlertDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.db.Database;

@RunWith(RobolectricTestRunner.class)
public class CategoryListActivity_With3Categories_Test {

	private CategoryListActivity activity;

	private ObjectContainer db;

	private static final String NAME1 = "aFirst Category";
	private static final String NAME2 = "bSecond Category";
	private static final String NAME3 = "cThird Category";

	private Category category1;
	private Category category2;
	private Category category3;

	private TestMenu menu;

	private ActivityController<CategoryListActivity> activityController;

	private CategoryDao categoryDao;

	private File externalFilePathForDb;
	private File externalFilePathForCsv;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(CategoryListActivity.class);
		activity = activityController.get();
		externalFilePathForDb = new File(activity.getExternalFilesDir(null), "FastBudget.backup");
		externalFilePathForCsv = new File(activity.getExternalFilesDir(null), "FastBudget.csv");
		initDb();

		activityController.create();
		menu = new TestMenu();
		activity.onCreateOptionsMenu(menu);
		assertThat(activity.getListAdapter().getCount(), is(greaterThan(0)));
	}

	@After
	public void cleanUp() {
		externalFilePathForDb.getParentFile().delete();
	}

	private void initDb() {
		db = Database.getClearedInstance(activity.getApplicationContext());
		categoryDao = new CategoryDao(db);

		Calendar started = Calendar.getInstance();
		started.add(Calendar.MONTH, -1);
		Date now = new Date();
		category1 = new Category(NAME1, 111, now);
		category2 = new Category(NAME2, 222, now);
		category3 = new Category(NAME3, 333, now);
		categoryDao.store(category1);
		categoryDao.store(category2);
		categoryDao.store(category3);
	}

	@Test
	public void hasAnAddView() throws Exception {
		assertThat(menu.findItem(R.id.button_create_category), is(notNullValue()));
	}

	@Test
	public void containsAListWith3Items() throws Exception {
		assertThat(activity.getListView().getAdapter().getCount(), is(3));
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
	public void addExpense_sendsCategoryNameToExpenseActivity() {
		findListView(R.id.button_add_expense).performClick();

		Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
		assertThat("Activity was not started", nextStartedActivity, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(nextStartedActivity);
		assertThat(shadowIntent.getExtras(), is(notNullValue()));
		assertThat(shadowIntent.getExtras().getString(Extras.CategoryName.name()), is(NAME1));
	}

	@Test
	public void editCategory_sendsCategoryNameToCategoryActivity() {
		findListView(R.id.button_edit).performClick();

		Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
		assertThat("Activity was not started", nextStartedActivity, is(notNullValue()));
		ShadowIntent shadowIntent = shadowOf(nextStartedActivity);
		assertThat(shadowIntent.getExtras(), is(notNullValue()));
		assertThat(shadowIntent.getExtras().getString(Extras.CategoryName.name()), is(NAME1));
	}

	@Test
	public void setsSumOfAllExpenses() {
		category1.add(new Expense(20, new Date(), null));
		category1.add(new Expense(40, new Date(), null));
		activity.updateView();

		TextView spent = (TextView) findListView(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.60"));
	}

	@Test
	public void expenseAdded_refreshUI() {
		findListView(R.id.button_add_expense).performClick();

		category1.add(new Expense(20, new Date(), null));
		categoryDao.store(category1);
		Intent intent = new Intent(activity, ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), category1.name);
		shadowOf(activity).receiveResult(intent, Activity.RESULT_OK, null);

		TextView spent = (TextView) findListView(R.id.category_spent);
		assertThat(spent.getText().toString(), is("$0.20"));
	}

	@Test
	public void setsProgressBar() {
		category1.add(new Expense(20, new Date(), null));
		categoryDao.store(category1);
		activity.updateView();

		ProgressBar progressBar = (ProgressBar) findListView(R.id.category_fill);
		assertThat(progressBar.getMax(), is(category1.budget));
		assertThat(progressBar.getProgress(), is(20));
	}

	@Test
	public void itSortsCategoriesByTheirExpenseCount() {
		category1.add(new Expense(0, new Date(), ""));
		category2.add(new Expense(0, new Date(), ""));
		category2.add(new Expense(0, new Date(), ""));
		categoryDao.store(category1);
		categoryDao.store(category2);

		activity.updateView();

		assertThatTextAtPositionIs(0, R.id.name, NAME2);
		assertThatTextAtPositionIs(1, R.id.name, NAME1);
		assertThatTextAtPositionIs(2, R.id.name, NAME3);
	}

	@Test
	public void itShowsTotalBudgetForThisMonthInTitle() {
		assertThat(activity.getTitle().toString(), containsString("$7"));
	}

	@Test
	public void itShowsTotalSpentsForThisMonthInTitle() {
		category1.add(new Expense(20, new Date(), ""));
		category2.add(new Expense(200, new Date(), ""));
		activity.updateView();
		assertThat(activity.getTitle().toString(), containsString("$2"));
	}

	@Test
	public void itShowsTheNetBudget() {
		assertThatTextAtPositionIs(0, R.id.category_budget, "$1.11");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		category1.add(new Expense(20, cal.getTime(), ""));
		categoryDao.store(category1);

		activity.updateView();

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
	public void deleteCategory_okDeletesCategorysExpensesFromDb() {
		Expense expense = new Expense(20, new Date(), null);
		category1.add(expense);
		categoryDao.store(category1);
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThat(db.ext().isStored(expense), is(false));
	}

	@Test
	public void deleteCategory_requeriesList() {
		findListView(R.id.button_delete).performClick();
		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertThatTextAtPositionIs(0, R.id.name, NAME2);
	}

	@Test
	public void hidesContextRow() {
		activityController.start();
		assertThat(findListView(R.id.context_row).getVisibility(), is(GONE));
	}

	@Test
	public void clickOnListIcon_opensExpenseListActivity() {
		activityController.start();
		findListView(R.id.button_list).performClick();

		Intent startedIntent = shadowOf(activity).getNextStartedActivity();
		assertThat("No intend was started!", startedIntent, is(notNullValue()));
		assertThat(startedIntent.getExtras().isEmpty(), is(false));
		assertThat((String) startedIntent.getExtras().get(Extras.CategoryName.name()), is(category1.name));

		ShadowIntent shadowIntent = shadowOf(startedIntent);
		assertThat(shadowIntent.getComponent().getClassName(), equalTo(ExpenseListActivity.class.getName()));
	}

	@Test
	public void hasABackupMenu() throws Exception {
		menu.findItem(R.id.button_create_backup);
	}

	@Test
	public void clickOnBackup_createsAFileOnSdCard() throws Exception {
		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_backup));

		assertThat(externalFilePathForDb.exists(), is(true));
	}

	@Test
	public void clickOnCsv_createsAFileOnSdCard() throws Exception {
		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_csv));

		assertThat(externalFilePathForCsv.exists(), is(true));
	}

	@Test
	public void clickOnBackup_notifiesUserAboutItsLocation() throws Exception {
		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_backup));

		assertThat(getLatestAlertDialog(), is(not(nullValue())));
		String message = shadowOf(getLatestAlertDialog()).getMessage().toString();
		String expectedMessage = activity.getString(R.string.backup_created, externalFilePathForDb.getAbsolutePath());
		assertThat(message, is(expectedMessage));
	}

	@Test
	public void clickOnBackup_noExternalStorageAvailable_noFileCreated() throws Exception {
		CategoryListActivity activity = Mockito.spy(this.activity);
		Mockito.when(activity.getExternalFilesDir(null)).thenReturn(null);

		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_backup));

		assertThat(externalFilePathForDb.exists(), is(false));
	}

	@Test
	public void clickOnBackup_noExternalStorageAvailable_userNotified() throws Exception {
		CategoryListActivity activity = Mockito.spy(this.activity);
		Mockito.when(activity.getExternalFilesDir(null)).thenReturn(null);

		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_backup));

		assertDialogTextIs(R.string.error_no_external_storage, activity);
	}

	@Test
	public void clickOnCsv_noExternalStorageAvailable_userNotified() throws Exception {
		CategoryListActivity activity = Mockito.spy(this.activity);
		Mockito.when(activity.getExternalFilesDir(null)).thenReturn(null);

		activity.onOptionsItemSelected(menu.findItem(R.id.button_create_csv));

		assertDialogTextIs(R.string.error_no_external_storage, activity);
	}

	private void assertDialogTextIs(int textId, CategoryListActivity activity) {
		AlertDialog latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
		assertThat(latestAlertDialog, is(not(nullValue())));
		String message = shadowOf(latestAlertDialog).getMessage().toString();
		String expectedMessage = activity.getString(textId);
		assertThat(message, is(expectedMessage));
	}

	private void assertThatTextAtPositionIs(int position, int viewId, String expected) {
		TextView name1 = (TextView) activity.getListAdapter().getView(position, null, null).findViewById(viewId);
		assertThat(name1, is(notNullValue()));
		assertThat("At Position " + position, name1.getText().toString(), is(expected));
	}

	private View findListView(int viewId) {
		return findListView(0, viewId);
	}

	private View findListView(int position, int viewId) {
		return activity.getListAdapter().getView(position, null, activity.getListView()).findViewById(viewId);
	}
}
