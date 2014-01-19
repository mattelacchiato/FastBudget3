package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.Database;

public class CategoryDaoTest {

	private static final String DB_FILE;
	static {
		try {
			DB_FILE = File.createTempFile(UUID.randomUUID().toString(), "").getAbsolutePath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	private static final int ANY_AMOUNT = 0;
	private static final Date ANY_DATE = new Date();
	private static final String ANY_NAME = "foo";

	private CategoryDao categoryDao;
	private ObjectContainer db;

	@Before
	public void setUp() throws Exception {
		db = Db4oEmbedded.openFile(Database.createConfig(), DB_FILE);
		categoryDao = new CategoryDao(db);
	}

	@After
	public void closeDb() {
		db.close();
	}

	@Test
	public void store_persistsAllExpenses() throws Exception {
		Expense expense = new Expense(ANY_DATE);
		Category category = new Category(ANY_NAME, ANY_AMOUNT, ANY_DATE);
		category.add(expense);
		categoryDao.store(category);
		assertThat(db.ext().isStored(expense), is(true));
	}

	@Test
	public void store_update_persistsAllExpenses() throws Exception {
		Expense expense = new Expense(ANY_DATE);
		Category category = new Category(ANY_NAME, ANY_AMOUNT, ANY_DATE);
		categoryDao.store(category);
		category.add(expense);
		categoryDao.store(category);

		assertThat(db.ext().isStored(expense), is(true));
	}

	@Test
	public void delete_deletesAllExpenses() throws Exception {
		Expense expense = new Expense(ANY_DATE);
		Category category = new Category(ANY_NAME, ANY_AMOUNT, ANY_DATE);
		category.add(expense);
		categoryDao.store(category);

		categoryDao.delete(category);
		assertThat(db.ext().isStored(expense), is(false));
	}

	@Test
	public void findByName_loadExpenditures() throws Exception {
		Expense expense = new Expense(ANY_DATE);
		Category category = new Category(ANY_NAME, ANY_AMOUNT, ANY_DATE);
		category.add(expense);
		categoryDao.store(category);

		closeDb();
		setUp();

		Expense loadedExpense = categoryDao.findByName(ANY_NAME).getExpenses().iterator().next();
		assertThat(db.ext().isActive(loadedExpense), is(true));
	}
}
