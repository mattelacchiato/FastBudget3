package de.splitstudio.fastbudget3.db;

import java.util.Date;

public class Expense {
	public int amount;
	public Date date;
	public String description;

	public Expense(int amount, Date date, String description) {
		this.amount = amount;
		this.date = date;
		this.description = description;
	}

}
