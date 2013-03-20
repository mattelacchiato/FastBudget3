package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.splitstudio.utils.DateUtils;

public class Category implements Comparable<Category> {

	public String name;
	public int budget;
	public List<Expenditure> expenditures;
	public Date date;

	public Category(String name, int budget, Date date) {
		this.name = name;
		this.budget = budget;
		this.expenditures = new ArrayList<Expenditure>();
		this.date = date;
	}

	public Category(String name) {
		this(name, 0, null);
	}

	public int summarizeExpenditures(Date start, Date end) {
		int sum = 0;
		for (Expenditure expenditure : expenditures) {
			if (DateUtils.isBetween(start, expenditure.date, end)) {
				sum += expenditure.amount;
			}
		}
		return sum;
	}

	public int summarizeExpendituresForMonth(int i) {
		return 0;
	}

	/**
	 * Sorts descending by expenditures size.
	 */
	@Override
	public int compareTo(Category other) {
		int compareBySize = other.expenditures.size() - expenditures.size();
		if (compareBySize != 0) {
			return compareBySize;
		}
		return name.compareTo(other.name);
	}

	int calcGrossBudget() {
		int budget = 0;
		Calendar started = Calendar.getInstance();
		started.setTime(date);

		Calendar currentMonth = Calendar.getInstance();
		while (started.before(currentMonth) || started.equals(currentMonth)) {
			budget += this.budget;
			currentMonth.add(Calendar.MONTH, -1);
		}

		return budget;
	}

	public int calcBudget() {
		Calendar lastMonth = DateUtils.createFirstDayOfMonth();
		lastMonth.add(Calendar.MILLISECOND, -1);
		return calcGrossBudget() - summarizeExpenditures(date, lastMonth.getTime());
	}
}
