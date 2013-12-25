package de.splitstudio.fastbudget3.db;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;

public class ExpenseTest {

	@Test
	public void hashCode_equalValues_notEqualHashCode() throws Exception {
		Expense expense1 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		Expense expense2 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		assertThat(expense1.hashCode(), is(not(expense2.hashCode())));
	}
}
