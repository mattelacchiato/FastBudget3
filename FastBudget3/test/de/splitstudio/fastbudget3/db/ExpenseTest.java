package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.DateUtils.createFirstDayOfMonth;
import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;

public class ExpenseTest {

	@Test
	public void constructor_createsUniqueUuid() throws Exception {
		Expense expense1 = new Expense(20, createFirstDayOfMonth().getTime(), "bla");
		Expense expense2 = new Expense(20, createFirstDayOfMonth().getTime(), "bla");
		assertThat(expense1.uuid, is(notNullValue()));
		assertThat(expense1.uuid, is(not(expense2.uuid)));
	}

	@Test
	public void compareTo_newestEntryShouldBeFirst() throws Exception {
		Calendar cal = createFirstDayOfMonth();
		Expense oldExpense = new Expense(10, cal.getTime(), "first");
		cal.add(MILLISECOND, 1);
		Expense newExpense = new Expense(10, cal.getTime(), "second");
		List<Expense> expenses = new ArrayList<Expense>();
		expenses.add(oldExpense);
		expenses.add(newExpense);
		Collections.sort(expenses);
		assertThat(expenses.get(0), is(newExpense));
	}

	@Test
	public void compareTo_equalFields_notZero() throws Exception {
		Date date = DateUtils.createFirstDayOfMonth().getTime();
		Expense second = new Expense(20, date, "bla");
		Expense first = new Expense(20, date, "bla");
		assertThat(first.compareTo(second), is(not(0)));
	}

	@Test
	public void compareTo_sameInstances_zero() throws Exception {
		Expense expense = new Expense(20, new Date(), "bla");
		assertThat(expense.compareTo(expense), is(0));
	}
}
