package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.db4o.config.annotations.Indexed;

import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.UniqueEntity;

public class Category extends UniqueEntity implements Comparable<Category> {

	@Indexed
	public String name;

	public int budget;

	private final List<Expense> expenses = new ArrayList<Expense>();

	public Date date;

	public Category(String name, int budget, Date date) {
		this(name);
		this.budget = budget;
		this.date = date;
	}

	public Category(String name) {
		super();
		this.name = name;
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

	public void add(Expense expense) {
		if (!expenses.contains(expense)) {
			expenses.add(expense);
			Collections.sort(expenses);
		}
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void remove(Expense expense) {
		expenses.remove(expense);
	}

}
