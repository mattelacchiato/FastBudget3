package de.splitstudio.fastbudget3.db;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.UniqueEntity;

public class ExpenseTest {

	@Test
	public void hashCode_equalValues_notEqualHashCode() throws Exception {
		//TODO (Dec 25, 2013): use uuid instead of hashCode!
		UniqueEntity expense1 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		UniqueEntity expense2 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		assertThat(expense1.hashCode(), is(not(expense2.hashCode())));
	}

	@Test
	public void constructor_createsUniqueUuid() throws Exception {
		UniqueEntity expense1 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		UniqueEntity expense2 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		assertThat(expense1.uuid, is(notNullValue()));
		assertThat(expense1.uuid, is(not(expense2.uuid)));
	}
}
