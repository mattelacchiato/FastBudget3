package de.splitstudio.fastbudget3.db;

import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;

public class ExpenseTest {

	@Test
	public void constructor_createsUniqueUuid() throws Exception {
		Expense expense1 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		Expense expense2 = new Expense(20, DateUtils.createFirstDayOfMonth().getTime(), "bla");
		assertThat(expense1.uuid, is(notNullValue()));
		assertThat(expense1.uuid, is(not(expense2.uuid)));
	}

	@Test
	public void compareTo_newestEntryShouldBeFirst() throws Exception {
		Calendar cal = DateUtils.createFirstDayOfMonth();
		Expense second = new Expense(20, cal.getTime(), "bla");
		cal.add(MILLISECOND, 1);
		Expense first = new Expense(10, cal.getTime(), "bla");
		assertThat(first.compareTo(second), is(lessThan(0)));
	}

	@Test
	public void compareTo_equalFields_notZero() throws Exception {
		//Background: Expenses will be added in a TreeSet, which looks for equalTo() to put them
		//in correct order. When equalTo() returns zero, it means, it's allready in the Set and
		//will not get added.
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
