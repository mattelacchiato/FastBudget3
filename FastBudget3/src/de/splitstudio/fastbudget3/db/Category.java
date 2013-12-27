package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.splitstudio.utils.DateUtils;

//TODO (Dec 27, 2013): extend UniqueEntity
public class Category implements Comparable<Category> {

	public String name;
	public int budget;
	public List<Expense> expenses = new ArrayList<Expense>();
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
	public int summarizeExpenses(Date start, Date end) {
		int sum = 0;
		for (Expense expense : expenses) {
			if (DateUtils.isBetween(start, expense.date, end)) {
				sum += expense.amount;
			}
		}
		return sum;
	}

	/**
	 * Sorts descending by expenses size.
	 */
	@Override
	public int compareTo(Category other) {
		int compareBySize = other.expenses.size() - expenses.size();
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
		return calcGrossBudget() - summarizeExpenses(null, lastMonth.getTime());
	}

	public List<Expense> findExpenses(Date start, Date end) {
		List<Expense> list = new ArrayList<Expense>(expenses.size());
		for (Expense expense : expenses) {
			if (DateUtils.isBetween(start, expense.date, end)) {
				list.add(expense);
			}
		}
		return list;
	}

}
