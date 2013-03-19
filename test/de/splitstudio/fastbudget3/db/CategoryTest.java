package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

public class CategoryTest {

	@Test
	public void summarizeExpenditures() {
		Date start = dayAndSecOfMonth(1, 0);
		Date end = dayAndSecOfMonth(32, 0);

		Category category = new Category("Foo", 0);
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

	@SuppressWarnings("deprecation")
	private Date dayAndSecOfMonth(int day, int sec) {
		return new Date(1985, 5, day, 0, 0, sec);
	}
}
