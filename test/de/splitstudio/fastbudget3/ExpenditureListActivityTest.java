package de.splitstudio.fastbudget3;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
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
	private ExpenditureListActivity activity;
	private Intent intent;
	private ObjectContainer db;
	private Category category;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activity = Robolectric.buildActivity(ExpenditureListActivity.class).get();
		db = Database.getInstance(activity);
		Database.clear();
		intent = new Intent(new OverviewActivity(), ExpenditureListActivity.class);
		intent.putExtra(Extras.CategoryName.name(), CATEGORY_NAME);
		activity.setIntent(intent);

		category = new Category(CATEGORY_NAME);
		db.store(category);
		db.commit();
	}

	@Test
	public void itsTitleContainsCategoryName() throws Exception {
		activity.onCreate(null);
		assertThat(activity.getTitle().toString(), containsString(CATEGORY_NAME));
	}

	@Test
	public void noExpendituresGiven_itShowsAHint() throws Exception {
		activity.onCreate(null);
		TextView hint = (TextView) activity.findViewById(android.R.id.empty);
		assertThat(hint, is(notNullValue()));
		assertThat(hint.getText(), is(notNullValue()));
		assertThat(hint.getText().toString(), is(activity.getString(R.string.hint_empty_expenditures)));
	}

	@Test
	public void itShowsTheDescription() throws Exception {
		String description = "jo!";
		category.expenditures.add(new Expenditure(20, new Date(), description));

		activity.onCreate(null);

		View row = activity.getListView().getChildAt(0);
		assertThat(row, is(notNullValue()));
		TextView descriptionTextView = (TextView) row.findViewById(R.id.description);
		assertThat(descriptionTextView, is(notNullValue()));
		assertThat(descriptionTextView.getText().toString(), is(description));
	}

	@Test
	public void itShowsTheAmount() throws Exception {
		category.expenditures.add(new Expenditure(20, new Date(), "bla"));

		activity.onCreate(null);

		View row = activity.getListView().getChildAt(0);
		TextView amountTextView = (TextView) row.findViewById(R.id.amount);
		assertThat(amountTextView, is(notNullValue()));
		assertThat(amountTextView.getText().toString(), is("$0.20"));
	}

	@Test
	public void itShowsTheDate() throws Exception {
		Calendar calendar = new GregorianCalendar(2013, 2, 23);
		Date date = calendar.getTime();
		category.expenditures.add(new Expenditure(20, date, "bla"));

		activity.onCreate(null);
		updateDateBoundariesToYesterdayAndTomorrow(calendar);

		View row = activity.getListView().getChildAt(0);
		TextView amountTextView = (TextView) row.findViewById(R.id.date_field);
		assertThat(amountTextView, is(notNullValue()));
		assertThat(amountTextView.getText().toString(), is("3/23/13"));
	}

	@Test
	public void itShowsAButtonToSelectStartDate() throws Exception {
		activity.onCreate(null);
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void itShowsAButtonToSelectEndDate() throws Exception {
		activity.onCreate(null);
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void startButtonIsInitializedWithFirstDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createFirstDayOfMonth().getTime());
		activity.onCreate(null);
		Button button = (Button) activity.findViewById(R.id.date_start);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	public void endButtonIsInitializedWithLastDayOfMonth() throws Exception {
		String startDate = DateUtils.formatAsShortDate(DateUtils.createLastDayOfMonth().getTime());
		activity.onCreate(null);
		Button button = (Button) activity.findViewById(R.id.date_end);
		assertThat(button.getText().toString(), is(startDate));
	}

	@Test
	@Ignore
	public void clickOnStartDateButton_opensDatePicker() throws Exception {
		activity.onCreate(null);
		activity.findViewById(R.id.date_start).performClick();
		//test that datepicker is shown
	}

	//TODO complains abeout mixed up dates

	@Test
	public void itShowsOnlyExpendituresForCurrentPeriod() throws Exception {
		String description = "Foo";
		Calendar cal = Calendar.getInstance();
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));
		cal.add(Calendar.MONTH, -1);
		category.expenditures.add(new Expenditure(30, cal.getTime(), ANY_DESCRIPTION));

		activity.onCreate(null);

		assertThat(activity.getListAdapter().getCount(), is(2));
	}

	@Test
	public void update_startButtonUpdated() throws Exception {
		activity.onCreate(null);
		activity.start.set(1985, 4, 14);
		activity.update.run();
		Button startButton = (Button) activity.findViewById(R.id.date_start);
		assertThat(startButton.getText().toString(), is("5/14/85"));
	}

	@Test
	public void update_endButtonUpdated() throws Exception {
		activity.onCreate(null);
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
		activity.onCreate(null);
		assertThat(activity.getListView().getChildCount(), is(0));

		activity.start.add(Calendar.MONTH, -1);
		activity.update.run();

		assertThat(activity.getListView().getChildCount(), is(1));
	}

	private void updateDateBoundariesToYesterdayAndTomorrow(Calendar calendar) {
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		activity.start = (Calendar) calendar.clone();
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		activity.end = (Calendar) calendar.clone();
		activity.update.run();
	}

}
