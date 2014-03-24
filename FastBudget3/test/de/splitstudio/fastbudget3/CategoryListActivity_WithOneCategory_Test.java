package de.splitstudio.fastbudget3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.buildActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.view.TestMenu;
import org.robolectric.util.ActivityController;

import android.view.View;
import android.widget.ProgressBar;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.utils.db.Database;

@RunWith(RobolectricTestRunner.class)
public class CategoryListActivity_WithOneCategory_Test {

	private CategoryListActivity activity;

	private ObjectContainer db;

	private static final String NAME1 = "aFirst Category";
	private static final String NAME2 = "bSecond Category";
	private static final String NAME3 = "cThird Category";

	private Category category;
	private Category category2;
	private Category category3;

	private TestMenu menu;

	private ActivityController<CategoryListActivity> activityController;

	private CategoryDao categoryDao;

	private File externalFilePathForDb;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		activityController = buildActivity(CategoryListActivity.class);
		activity = activityController.get();
		externalFilePathForDb = new File(activity.getExternalFilesDir(null), "FastBudget.backup");
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
		category = new Category(NAME1, 100, now);
		categoryDao.store(category);
	}

	@Test
	public void negativeBudget_progressBarOnMax() {
		category.add(new Expense(9999, lastMonth(), null));
		assertThat(category.calculateBudget()).isNegative();
		activityController.resume();

		ProgressBar progressBar = (ProgressBar) findListView(R.id.category_fill);

		assertThat(progressBar.getProgress()).isGreaterThan(0);
		assertThat(progressBar.getProgress()).isEqualTo(progressBar.getMax());
	}

	@Test
	public void negativeBudget_nothingSpentThisMonth_progressBarOnMax() {
		category.add(new Expense(1, new Date(), null));
		category.add(new Expense(9999, lastMonth(), null));
		activityController.resume();

		ProgressBar progressBar = (ProgressBar) findListView(R.id.category_fill);

		assertThat(progressBar.getProgress()).isGreaterThan(0);
		assertThat(progressBar.getProgress()).isEqualTo(progressBar.getMax());
	}

	private Date lastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	private View findListView(int viewId) {
		return activity.getListAdapter().getView(0, null, activity.getListView()).findViewById(viewId);
	}
}
