package de.splitstudio.fastbudget3.db;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.Database;

public class ExpenseDaoTest {

	private static final int ANY_AMOUNT = 0;
	private static final Date ANY_DATE = new Date();

	private ExpenseDao expenseDao;
	private ObjectContainer db;

	@Before
	public void setUp() throws Exception {
		String databaseFileName = File.createTempFile(UUID.randomUUID().toString(), "").getAbsolutePath();
		db = Db4oEmbedded.openFile(Database.createConfig(), databaseFileName);
		expenseDao = new ExpenseDao(db);
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "d"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "a"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "a"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "b"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "b"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "b"));
		expenseDao.store(new Expense(ANY_AMOUNT, ANY_DATE, "c"));
	}

	@Test
	public void findAllDescriptions_allDescriptions() {
		List<String> descriptions = expenseDao.findAllDescriptions();
		assertThat(descriptions, is(notNullValue()));
		assertThat(descriptions, is(not(empty())));
		assertThat(descriptions, containsInAnyOrder("a", "b", "c", "d"));
	}

	@Test
	public void findAllDescriptions_eachItemOnlyOnce() {
		List<String> descriptions = expenseDao.findAllDescriptions();
		assertThat(descriptions, hasSize(4));
	}

	@Test
	public void findAllDescriptions_mostUsedFirst() {
		List<String> descriptions = expenseDao.findAllDescriptions();
		assertThat(descriptions.get(0), is("b"));
		assertThat(descriptions.get(1), is("a"));
	}

	@Test
	public void findAllDescriptions_equalCount_orderByAlphabet() {
		List<String> descriptions = expenseDao.findAllDescriptions();
		assertThat(descriptions.get(2), is("c"));
		assertThat(descriptions.get(3), is("d"));
	}

	@Test
	public void countDescriptions_returnsMapWithFourItems() throws Exception {
		assertThat(expenseDao.countDescriptions().keySet(), hasSize(4));
	}

	@Test
	public void countDescriptions_hasCountedDescriptionsCorrect() throws Exception {
		Map<String, Integer> countDescriptions = expenseDao.countDescriptions();
		assertThat(countDescriptions.get("a"), is(2));
		assertThat(countDescriptions.get("b"), is(3));
		assertThat(countDescriptions.get("c"), is(1));
		assertThat(countDescriptions.get("d"), is(1));
	}

}
