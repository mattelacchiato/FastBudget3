package de.splitstudio.fastbudget3.db;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.Database;

public class CategoryDaoTest {

	private static final int ANY_AMOUNT = 0;
	private static final Date ANY_DATE = new Date();
	private static final String ANY_NAME = "foo";

	private static final String DB_FILENAME;
	static {
		try {
			DB_FILENAME = File.createTempFile(UUID.randomUUID().toString(), "").getAbsolutePath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private CategoryDao categoryDao;
	private ObjectContainer db;

	@Before
	public void setUp() throws Exception {
		initDao();
	}

	private void initDao() throws IOException {
		if (db != null) {
			db.close();
		}
		db = Db4oEmbedded.openFile(Database.createConfig(), DB_FILENAME);
		categoryDao = new CategoryDao(db);
	}

	@Test
	public void store_persistsExpenditures() throws Exception {
		Category category = new Category(ANY_NAME, ANY_AMOUNT, ANY_DATE);
		category.expenses.add(new Expense(ANY_AMOUNT, ANY_DATE, ANY_NAME));
		categoryDao.store(category);

		initDao();

		assertThat(categoryDao.findByUuid(category.uuid).expenses, is(not(empty())));
	}

}
