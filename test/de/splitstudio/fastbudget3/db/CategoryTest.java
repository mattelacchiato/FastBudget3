package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.splitstudio.utils.DateUtils;

public class CategoryTest {

	@Test
	public void summarizeExpenditures() {
		Date start = dayAndSecOfMonth(1, 0);
		Date end = dayAndSecOfMonth(32, 0);

		Category category = new Category("Foo", 0, new Date());
		//out of range
		category.expenditures.add(new Expenditure(2, dayAndSecOfMonth(-2, 0), null));
		//in range
		category.expenditures.add(new Expenditure(-10, dayAndSecOfMonth(1, 1), null));
		category.expenditures.add(new Expenditure(2, dayAndSecOfMonth(2, 0), null));
		category.expenditures.add(new Expenditure(40, dayAndSecOfMonth(31, 0), null));
		//out of range
		category.expenditures.add(new Expenditure(40, dayAndSecOfMonth(32, 1), null));

		assertThat(category.summarizeExpenditures(start, end), is(32));
	}

	@Test
	public void comparesTo_lessThanOther_greaterThanZero() {
		Category category = new Category("one");
		category.expenditures.add(new Expenditure(0, null, ""));

		Category other = new Category("one");
		other.expenditures.add(new Expenditure(0, null, ""));
		other.expenditures.add(new Expenditure(0, null, ""));

		assertThat(category.compareTo(other), is(greaterThan(0)));
	}

	@Test
	public void comparesTo_moreThanOther_lessThanZero() {
		Category category = new Category("one");
		category.expenditures.add(new Expenditure(0, null, ""));
		category.expenditures.add(new Expenditure(0, null, ""));

		Category other = new Category("one");
		other.expenditures.add(new Expenditure(0, null, ""));

		assertThat(category.compareTo(other), is(lessThan(0)));
	}

	@Test
	public void comparesTo_equal_zero() {
		Category category = new Category("one");
		category.expenditures.add(new Expenditure(0, null, ""));

		Category other = new Category("one");
		other.expenditures.add(new Expenditure(0, null, ""));

		assertThat(category.compareTo(other), is(0));
	}

	@Test
	public void calcGrossBudget_startetThisMonth_oneMonthBudget() {
		int budget = 10000;
		Category category = new Category("Foo", budget, DateUtils.createFirstDayOfMonth().getTime());
		assertThat(category.calcGrossBudget(), is(budget));
	}

	@Test
	public void calcGrossBudget_startetLastMonth_twoMonthBudget() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		assertThat(category.calcGrossBudget(), is(budget * 2));
	}

	@Test
	public void calcGrossBudget_startetTenMonthsAgo_elevenMonthBudget() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -10);
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		assertThat(category.calcGrossBudget(), is(budget * 11));
	}

	@Test
	public void calcBudget_thisMonth_nothingSpent() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		assertThat(category.calcBudget(), is(budget));
	}

	@Test
	public void calcBudget_thisMonth_spentThisMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		category.expenditures.add(new Expenditure(20, new Date(), null));
		assertThat(category.calcBudget(), is(budget));
	}

	@Test
	public void calcBudget_lastMonth_spentThisMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		category.expenditures.add(new Expenditure(20, new Date(), null));

		assertThat(category.calcBudget(), is(budget * 2));
	}

	@Test
	public void calcBudget_lastMonth_spentLastMonthSth() {
		int budget = 10000;

		Calendar cal = DateUtils.createFirstDayOfMonth();
		cal.add(Calendar.MONTH, -1);
		Date createdAt = cal.getTime();

		Category category = new Category("Foo", budget, createdAt);
		//spent sth. last month
		category.expenditures.add(new Expenditure(20, cal.getTime(), null));
		//spent sth. this month, don't count it
		category.expenditures.add(new Expenditure(20, new Date(), null));

		assertThat(category.calcBudget(), is(budget * 2 - 20));
	}

	@SuppressWarnings("deprecation")
	private Date dayAndSecOfMonth(int day, int sec) {
		return new Date(1985, 5, day, 0, 0, sec);
	}
}
