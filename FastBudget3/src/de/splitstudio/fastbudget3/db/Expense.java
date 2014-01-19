package de.splitstudio.fastbudget3.db;

import java.util.Date;

import de.splitstudio.utils.db.UniqueEntity;

public class Expense extends UniqueEntity implements Comparable<Expense> {

	public int amount;
	public Date date;
	public String description;

	public Expense(int amount, Date date, String description) {
		this(date);
		this.amount = amount;
		this.description = description;
	}

	public Expense(Date date) {
		super();
		this.date = date;
	}

	@Override
	public int compareTo(Expense other) {
		int dateComparison = other.date.compareTo(date);
		if (dateComparison != 0) {
			return dateComparison;
		}
		return equals(other) ? 0 : 1;
	}

}
