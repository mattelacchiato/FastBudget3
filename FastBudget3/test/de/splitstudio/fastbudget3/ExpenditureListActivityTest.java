package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDatePickerDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;

@RunWith(RobolectricTestRunner.class)
public class ExpenditureListActivityTest {

	private static final String CATEGORY_NAME = "foobar";
	private static final String ANY_DESCRIPTION = "description";

	private Category category;

	private ActivityController<ExpenditureListActivity> activityController;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(ExpenditureListActivity.class).withIntent(createIntent());
		initDb();
	}

	private void initDb() {
		Context context = activityController.get().getApplicationContext();
		ObjectContainer db = Database.getInstance(context);
		Database.clear();

		category = new Category(CATEGORY_NAME);
		db.store(category);
		db.commit();
	}

	private Intent createIntent() {
		Intent intent = new Intent(new OverviewActivity(), ExpenditureListActivity.class);
		return intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
	}

	private ExpenditureListActivity createActivity() {
		return activityController.create().get();
	}

	@Test
	public void itsTitleContainsCategoryName() throws Exception {
		assertThat(createActivity().getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void noExpendituresGiven_itShowsAHint() throws Exception {
		Activity activity = createActivity();
		TextView hint = (TextView) activity.findViewById(android.R.id.empty);
		assertThat(hint, is(notNullValue()));
		assertThat(hint.getText(), is(notNullValue()));
		assertThat(hint.getText().toString(), is(activity.getString(R.string.hint_empty_expenditures)));
	}

	@Test
	public void itShowsTheDescription() throws Exception {
		String description = "jo!";
		category.expenditures.add(new Expenditure(20, new Date(), description));

		ExpenditureListActivity activity = createActivity();

		ListAdapter listAdapter = activity.getListAdapter();
		assertThat(listAdapter.getCount(), is(greaterThan(0)));
		View row = listAdapter.getView(0, null, null);
		assertThat(row, is(notNullValue()));
		TextView descriptionTextView = (TextView) row.findViewById(R.id.description);
		assertThat(descriptionTextView, is(notNullValue()));
		assertThat(descriptionTextView.getText().toString(), is(description));
	}

	@Test
	public void itShowsTheAmount() throws Exception {
		category.expenditures.add(new Expenditure(20, new Date(), "bla"));

		View row = createActivity().getListAdapter().getView(0, null, null);
		TextView amountTextView = (TextView) row.findViewById(R.id.amount);
		assertThat(amountTextView, is(notNullValue()));
		assertThat(amountTextView.getText().toString(), is("$0.20"));
	}

	@Test
	public void itShowsTheDate() throws Exception {
		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date date = cal.getTime();
		category.expenditures.add(new Expenditure(20, date, "bla"));

		View row = createActivity().getListAdapter().getView(0, null, null);
		TextView dateTextView = (TextView) row.findViewById(R.id.date_field);
		assertThat(dateTextView, is(notNullValue()));
		assertThat(dateTextView.getText().toString(), is(DateUtils.formatAsShortDate(date)));
	}

	@Test
	public void itShowsAButtonToSelectStartDate() throws Exception {
		ExpenditureListActivity activity = createActivity();
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void itShowsAButtonToSelectEndDate() throws Exception {
		ExpenditureListActivity activity = createActivity();
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void startButtonIsInitializedWithFirstDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createFirstDayOfMonth().getTime());
		ExpenditureListActivity activity = createActivity();
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	public void endButtonIsInitializedWithLastDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createLastDayOfMonth().getTime());
		ExpenditureListActivity activity = createActivity();
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	public void clickOnStartDateButton_opensDatePicker() throws Exception {
		ExpenditureListActivity activity = createActivity();
		activity.findViewById(R.id.date_start).performClick();
		DatePickerDialog dialog = (DatePickerDialog) ShadowDatePickerDialog.getLatestDialog();
		assertThat(dialog.isShowing(), is(true));
	}

	@Test
	public void update_startIsAfterEnd_buttonsUpdatedAnyway() {
		ExpenditureListActivity activity = createActivity();
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
		ExpenditureListActivity activity = createActivity();
		activity.start.set(1985, 4, 14);
		activity.end.set(1985, 4, 13);

		activity.update.run();

		assertThat(ShadowToast.getTextOfLatestToast(), is(notNullValue()));
		assertThat(ShadowToast.getTextOfLatestToast(), is(activity.getString(R.string.error_end_before_start)));
	}

	@Test
	public void itShowsOnlyExpendituresForCurrentPeriod() throws Exception {
		Calendar cal = Calendar.getInstance();
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));
		cal.add(Calendar.MONTH, -1);
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));

		ExpenditureListActivity activity = createActivity();

		assertThat(activity.getListAdapter().getCount(), is(2));
	}

	@Test
	public void update_startButtonUpdated() throws Exception {
		ExpenditureListActivity activity = createActivity();
		activity.start.set(1985, 4, 14);
		activity.update.run();
		Button startButton = (Button) activity.findViewById(R.id.date_start);
		assertThat(startButton.getText().toString(), is("5/14/85"));
	}

	@Test
	public void update_endButtonUpdated() throws Exception {
		ExpenditureListActivity activity = createActivity();
		activity.end.set(1985, 4, 14);
		activity.update.run();
		Button startButton = (Button) activity.findViewById(R.id.date_end);
		assertThat(startButton.getText().toString(), is("5/14/85"));
	}

	@Test
	public void update_listViewUpdated() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		category.expenditures.add(new Expenditure(30, cal.getTime(), null));
		ExpenditureListActivity activity = createActivity();
		assertThat(activity.getListAdapter().getCount(), is(0));

		activity.start.add(Calendar.MONTH, -1);
		activity.update.run();

		assertThat(activity.getListAdapter().getCount(), is(1));
	}

	@Test
	public void onCreate_noIntentGiven_isFinishing() {
		ExpenditureListActivity activityWithoutIntent = buildActivity(ExpenditureListActivity.class).create().get();
		assertThat(activityWithoutIntent.isFinishing(), is(true));
	}

}
