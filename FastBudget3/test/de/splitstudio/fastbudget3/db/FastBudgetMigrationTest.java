package de.splitstudio.fastbudget3.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.Database;

@RunWith(RobolectricTestRunner.class)
public class FastBudgetMigrationTest {

	private static final int ANY_VERSION = 42;
	private FastBudgetMigration migration;
	private ObjectContainer db;

	@Before
	public void setUp() {
		Activity context = new Activity();
		migration = new FastBudgetMigration(context);
	}

	@Before
	public void openDb() {
		db = Db4oEmbedded.openFile(Database.createConfig(), CategoryDaoTest.DB_FILE);
	}

	private void closeAndOpenDb() {
		closeDb();
		openDb();
	}

	@After
	public void closeDb() {
		db.close();
	}

	@Test
	public void migrate_lastVersionIsZero_deletesNullExpenses() {
		CategoryDao categoryDao = new CategoryDao(db);
		Expense expense1 = new Expense(10, new Date(), null);
		Expense expense2 = new Expense(10, new Date(), null);
		Expense expense3 = new Expense(10, new Date(), null);

		String categoryName = "foo";
		Category category = new Category(categoryName);
		category.add(expense1);
		category.add(expense2);
		category.add(expense3);

		categoryDao.store(category);
		db.delete(expense1);
		//don't delete expense2
		db.delete(expense3);
		closeAndOpenDb();

		migration.migrate(0, ANY_VERSION, db);
		closeAndOpenDb();

		List<Expense> expenses = new CategoryDao(db).findByName(categoryName).getExpenses();
		assertThat(expenses).hasSize(1);
	}
}
