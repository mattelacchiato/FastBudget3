package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;

public class CategoryTest {

	private static final String ANY_NAME = "Category name";

	@Test
	public void summarizeExpenses() {
		Date start = dayAndSecOfMonth(1, 0);
		Date end = dayAndSecOfMonth(32, 0);

		Category category = new Category(ANY_NAME, 0, new Date());
		//out of range
		category.expenses.add(new Expense(2, dayAndSecOfMonth(-2, 0), null));
		//in range
		category.expenses.add(new Expense(-10, dayAndSecOfMonth(1, 1), null));
		category.expenses.add(new Expense(2, dayAndSecOfMonth(2, 0), null));
		category.expenses.add(new Expense(40, dayAndSecOfMonth(31, 0), null));
		//out of range
		category.expenses.add(new Expense(40, dayAndSecOfMonth(32, 1), null));

		assertThat(category.summarizeExpenses(start, end), is(32));
	}

	@Test
	public void comparesTo_lessThanOther_greaterThanZero() {
		Category category = new Category(ANY_NAME);
		category.expenses.add(new Expense(0, null, ""));

		Category other = new Category("one");
		other.expenses.add(new Expense(0, null, ""));
		other.expenses.add(new Expense(0, null, ""));

		assertThat(category.compareTo(other), is(greaterThan(0)));
	}

	@Test
	public void comparesTo_moreThanOther_lessThanZero() {
		Category category = new Category(ANY_NAME);
		category.expenses.add(new Expense(0, null, ""));
		category.expenses.add(new Expense(0, null, ""));

		Category other = new Category(ANY_NAME);
		other.expenses.add(new Expense(0, null, ""));

		assertThat(category.compareTo(other), is(lessThan(0)));
	}

	@Test
	public void comparesTo_equal_zero() {
		Category category = new Category(ANY_NAME);
		category.expenses.add(new Expense(0, null, ""));

		Category other = new Category(ANY_NAME);
		other.expenses.add(new Expense(0, null, ""));

		assertThat(category.compareTo(other), is(0));
	}

	@Test
	public void calcGrossBudget_startetThisMonth_oneMonthBudget() {
		int budget = 10000;
		Category category = new Category(ANY_NAME, budget, DateUtils.createFirstDayOfMonth().getTime());
		assertThat(category.calcGrossBudget(), is(budget));
	}

	@Test
	public void calcGrossBudget_startetLastMonth_twoMonthBudget() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		assertThat(category.calcGrossBudget(), is(budget * 2));
	}

	@Test
	public void calcGrossBudget_startetTenMonthsAgo_elevenMonthBudget() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -10);
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		assertThat(category.calcGrossBudget(), is(budget * 11));
	}

	@Test
	public void calcBudget_thisMonth_nothingSpent() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		assertThat(category.calcBudget(), is(budget));
	}

	@Test
	public void calcBudget_thisMonth_spentThisMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		category.expenses.add(new Expense(20, new Date(), null));
		assertThat(category.calcBudget(), is(budget));
	}

	@Test
	public void calcBudget_lastMonth_spentThisMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		category.expenses.add(new Expense(20, new Date(), null));

		assertThat(category.calcBudget(), is(budget * 2));
	}

	@Test
	public void calcBudget_lastMonth_spentLastMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		//spent sth. last month
		category.expenses.add(new Expense(20, cal.getTime(), null));
		//spent sth. this month, don't count it
		category.expenses.add(new Expense(20, new Date(), null));

		assertThat(category.calcBudget(), is(budget * 2 - 20));
	}

	@Test
	public void findExpenses_expenseBeforeStart_emptyList() throws Exception {
		Category category = new Category(ANY_NAME);
		Calendar cal = Calendar.getInstance();
		category.expenses.add(new Expense(20, cal.getTime(), null));
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date start = cal.getTime();

		assertThat(category.findExpenses(start, null), is(empty()));
	}

	@Test
	public void findExpenses_roundTrip() throws Exception {
		Category category = new Category(ANY_NAME);
		Calendar cal = Calendar.getInstance();

		//out of range
		category.expenses.add(new Expense(20, cal.getTime(), null));

		//in range
		cal.add(Calendar.MONTH, 1);
		Date start = cal.getTime();
		category.expenses.add(new Expense(20, cal.getTime(), null));

		cal.add(Calendar.MONTH, 1);
		category.expenses.add(new Expense(20, cal.getTime(), null));

		cal.add(Calendar.MONTH, 1);
		Date end = cal.getTime();
		category.expenses.add(new Expense(20, cal.getTime(), null));

		//out of range
		cal.add(Calendar.MONTH, 1);
		category.expenses.add(new Expense(20, cal.getTime(), null));

		assertThat(category.findExpenses(start, end).size(), is(3));
	}

	@SuppressWarnings("deprecation")
	private Date dayAndSecOfMonth(int day, int sec) {
		return new Date(1985, 5, day, 0, 0, sec);
	}
}
