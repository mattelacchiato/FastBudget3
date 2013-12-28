package de.splitstudio.fastbudget3;

import static de.splitstudio.utils.DateUtils.createFirstDayOfMonth;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDatePickerDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;

@RunWith(RobolectricTestRunner.class)
public class ExpenseListActivityTest {

	private static final String CATEGORY_NAME = "foobar";

	private static final String DESCRIPTION = "desc";

	private Expense expense;

	private Category category;

	private ExpenseListActivity activity;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		ActivityController<ExpenseListActivity> activityController = buildActivity(ExpenseListActivity.class)
				.withIntent(createIntent());
		activity = activityController.get();
		initDb();
		activityController.create();
	}

	private void initDb() {
		ObjectContainer db = Database.getClearedInstance(activity);
		CategoryDao categoryDao = new CategoryDao(db);

		category = new Category(CATEGORY_NAME);
		expense = new Expense(10, createFirstDayOfMonth().getTime(), DESCRIPTION);
		category.expenses.add(expense);
		categoryDao.store(category);
	}

	private Intent createIntent() {
		Intent intent = new Intent(new OverviewActivity(), ExpenseListActivity.class);
		return intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
	}

	@Test
	public void itsTitleContainsCategoryName() throws Exception {
		assertThat(activity.getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void noExpensesGiven_itShowsAHint() throws Exception {
		TextView hint = (TextView) activity.findViewById(android.R.id.empty);
		assertThat(hint, is(notNullValue()));
		assertThat(hint.getText(), is(notNullValue()));
		assertThat(hint.getText().toString(), is(activity.getString(R.string.hint_empty_expenses)));
	}

	@Test
	public void itShowsAButtonToSelectStartDate() throws Exception {
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void itShowsAButtonToSelectEndDate() throws Exception {
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void startButtonIsInitializedWithFirstDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createFirstDayOfMonth().getTime());
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	public void endButtonIsInitializedWithLastDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createLastDayOfMonth().getTime());
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	public void clickOnStartDateButton_opensDatePicker() throws Exception {
		activity.findViewById(R.id.date_start).performClick();
		DatePickerDialog dialog = (DatePickerDialog) ShadowDatePickerDialog.getLatestDialog();
		assertThat(dialog.isShowing(), is(true));
	}

	@Test
	public void update_startIsAfterEnd_buttonsUpdatedAnyway() {
		activity.start.set(1985, 4, 14);
		activity.end.set(1985, 4, 13);

		activity.update.run();

		Button startButton = (Button) activity.findViewById(R.id.date_start);
		assertThat(startButton.getText().toString(), is("5/14/85"));
		Button endButton = (Button) activity.findViewById(R.id.date_end);
		assertThat(endButton.getText().toString(), is("5/13/85"));
	}

	@Test
	public void update_startIsAfterEnd_showToast() {
		activity.start.set(1985, 4, 14);
		activity.end.set(1985, 4, 13);

		activity.update.run();

		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(activity.getString(R.string.error_end_before_start)));
	}

	@Test
	public void update_startButtonUpdated() throws Exception {
		activity.start.set(1985, 4, 14);
		activity.update.run();
		Button startButton = (Button) activity.findViewById(R.id.date_start);
		assertThat(startButton.getText().toString(), is("5/14/85"));
	}

	@Test
	public void update_endButtonUpdated() throws Exception {
		activity.end.set(1985, 4, 14);
		activity.update.run();
		Button startButton = (Button) activity.findViewById(R.id.date_end);
		assertThat(startButton.getText().toString(), is("5/14/85"));
	}

	@Test
	public void onCreate_noIntentGiven_isFinishing() {
		ExpenseListActivity activityWithoutIntent = buildActivity(ExpenseListActivity.class).create().get();
		assertThat(activityWithoutIntent.isFinishing(), is(true));
	}

	@Test
	public void hasEditButton() {
		Button button = (Button) findListView(R.id.button_edit);
		assertThat(button.getText().toString(), is(activity.getString(R.string.edit)));
	}

	@Test
	public void clickEditButton_opensExpenditureActivity() {
		findListView(R.id.button_edit).performClick();

		Intent nextStartedActivity = shadowOf(activity).getNextStartedActivity();
		assertThat("Activity was not started", nextStartedActivity, is(notNullValue()));

		Bundle extras = shadowOf(nextStartedActivity).getExtras();
		assertThat(extras, is(notNullValue()));
		assertThat(extras.getString(Extras.Id.name()), is(expense.uuid));
		assertThat(extras.getString(Extras.CategoryName.name()), is(category.name));
	}

	private View findListView(int viewId) {
		return findListView(0, viewId);
	}

	private View findListView(int position, int viewId) {
		return activity.getListAdapter().getView(position, null, activity.getListView()).findViewById(viewId);
	}

}
