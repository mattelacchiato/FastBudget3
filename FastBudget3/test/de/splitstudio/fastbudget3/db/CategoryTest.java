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

	private static final Date ANY_DATE = DateUtils.createFirstDayOfMonth().getTime();

	@Test
	public void summarizeExpenses() {
		Date start = dayAndSecOfMonth(1, 0);
		Date end = dayAndSecOfMonth(32, 0);

		Category category = new Category(ANY_NAME, 0, new Date());
		//out of range
		category.add(new Expense(2, dayAndSecOfMonth(-2, 0), null));
		//in range
		category.add(new Expense(-10, dayAndSecOfMonth(1, 1), null));
		category.add(new Expense(2, dayAndSecOfMonth(2, 0), null));
		category.add(new Expense(40, dayAndSecOfMonth(31, 0), null));
		//out of range
		category.add(new Expense(40, dayAndSecOfMonth(32, 1), null));

		assertThat(category.summarizeExpenses(start, end), is(32));
	}

	@Test
	public void comparesTo_lessThanOther_greaterThanZero() {
		Category category = new Category(ANY_NAME);
		category.add(new Expense(0, ANY_DATE, ""));

		Category other = new Category("one");
		other.add(new Expense(0, ANY_DATE, ""));
		other.add(new Expense(0, ANY_DATE, ""));

		assertThat(category.compareTo(other), is(greaterThan(0)));
	}

	@Test
	public void comparesTo_moreThanOther_lessThanZero() {
		Category category = new Category(ANY_NAME);
		category.add(new Expense(0, ANY_DATE, ""));
		category.add(new Expense(0, ANY_DATE, ""));

		Category other = new Category(ANY_NAME);
		other.add(new Expense(0, ANY_DATE, ""));

		assertThat(category.compareTo(other), is(lessThan(0)));
	}

	@Test
	public void comparesTo_equal_zero() {
		Category category = new Category(ANY_NAME);
		category.add(new Expense(0, ANY_DATE, ""));

		Category other = new Category(ANY_NAME);
		other.add(new Expense(0, ANY_DATE, ""));

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
		category.add(new Expense(20, new Date(), null));
		assertThat(category.calcBudget(), is(budget));
	}

	@Test
	public void calcBudget_lastMonth_spentThisMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category(ANY_NAME, budget, createdAt);
		category.add(new Expense(20, new Date(), null));

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
		category.add(new Expense(20, cal.getTime(), null));
		//spent sth. this month, don't count it
		category.add(new Expense(20, new Date(), null));

		assertThat(category.calcBudget(), is(budget * 2 - 20));
	}

	@Test
	public void findExpenses_expenseBeforeStart_emptyList() throws Exception {
		Category category = new Category(ANY_NAME);
		Calendar cal = Calendar.getInstance();
		category.add(new Expense(20, cal.getTime(), null));
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date start = cal.getTime();

		assertThat(category.findExpenses(start, null), is(empty()));
	}

	@Test
	public void findExpenses_roundTrip() throws Exception {
		Category category = new Category(ANY_NAME);
		Calendar cal = Calendar.getInstance();

		//out of range
		category.add(new Expense(20, cal.getTime(), null));

		//in range
		cal.add(Calendar.MONTH, 1);
		Date start = cal.getTime();
		category.add(new Expense(20, cal.getTime(), null));

		cal.add(Calendar.MONTH, 1);
		category.add(new Expense(20, cal.getTime(), null));

		cal.add(Calendar.MONTH, 1);
		Date end = cal.getTime();
		category.add(new Expense(20, cal.getTime(), null));

		//out of range
		cal.add(Calendar.MONTH, 1);
		category.add(new Expense(20, cal.getTime(), null));

		assertThat(category.findExpenses(start, end).size(), is(3));
	}

	@Test
	public void addSameExpenseInstanceTwice_onlyOneAdded() throws Exception {
		Category category = new Category();
		Expense expense = new Expense(ANY_DATE);
		category.add(expense);
		category.add(expense);
		assertThat(category.getExpenses().size(), is(1));
	}

	@Test
	public void addExpense_resortList() throws Exception {
		Category category = new Category();
		Expense older = new Expense(ANY_DATE);
		category.add(older);
		Calendar cal = Calendar.getInstance();
		cal.setTime(ANY_DATE);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Expense newer = new Expense(cal.getTime());
		category.add(newer);
		assertThat(category.getExpenses().get(0), is(newer));
		assertThat(category.getExpenses().get(1), is(older));
	}

	@SuppressWarnings("deprecation")
	private Date dayAndSecOfMonth(int day, int sec) {
		return new Date(1985, 5, day, 0, 0, sec);
	}
}
