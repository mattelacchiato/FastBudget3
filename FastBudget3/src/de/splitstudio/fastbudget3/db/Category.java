package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.splitstudio.utils.DateUtils;

public class Category implements Comparable<Category> {

	public String name;
	public int budget;
	public List<Expenditure> expenditures = new ArrayList<Expenditure>();
	public Date date;

	public Category(String name, int budget, Date date) {
		this.name = name;
		this.budget = budget;
		this.date = date;
	}

	public Category(String name) {
		this(name, 0, null);
	}

	public Category() {}

	/**
	 * start or end may be null. in this case it means endless to this direction.
	 */
	public int summarizeExpenditures(Date start, Date end) {
		int sum = 0;
		for (Expenditure expenditure : expenditures) {
			if (DateUtils.isBetween(start, expenditure.date, end)) {
				sum += expenditure.amount;
			}
		}
		return sum;
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
		//TODO (Dec 22, 2013): put this in DateUtils!
		Calendar lastMonth = DateUtils.createFirstDayOfMonth();
		lastMonth.add(Calendar.MILLISECOND, -1);
		return calcGrossBudget() - summarizeExpenditures(null, lastMonth.getTime());
	}

	public List<Expenditure> findExpenditures(Date start, Date end) {
		List<Expenditure> list = new ArrayList<Expenditure>(expenditures.size());
		for (Expenditure expenditure : expenditures) {
			if (DateUtils.isBetween(start, expenditure.date, end)) {
				list.add(expenditure);
			}
		}
		return list;
	}

}
