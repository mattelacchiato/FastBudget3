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

	int calculateGrossBudget() {
		int budget = 0;
		Calendar startedMonth = Calendar.getInstance();
		startedMonth.setTime(date);

		Calendar currentMonth = Calendar.getInstance();
		while (!startedMonth.after(currentMonth)) {
			budget += this.budget;
			currentMonth.add(Calendar.MONTH, -1);
		}

		return budget;
	}

	public int calculateBudget() {
		Calendar lastMonth = DateUtils.createFirstDayOfMonth();
		lastMonth.add(Calendar.MILLISECOND, -1);
		return calculateGrossBudget() - summarizeExpenses(null, lastMonth.getTime());
	}

	public List<Expense> findExpenses(Date start, Date end) {
		List<Expense> expenses = new ArrayList<Expense>(this.expenses.size());
		for (Expense expense : this.expenses) {
			if (DateUtils.isBetween(start, expense.date, end)) {
				expenses.add(expense);
			}
		}
		return expenses;
	}

	public void add(Expense expense) {
		//TreeSet is buggy with db4o
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
