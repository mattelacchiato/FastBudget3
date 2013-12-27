package de.splitstudio.fastbudget3.db;

import java.util.Date;

import de.splitstudio.utils.db.UniqueEntity;

public class Expense extends UniqueEntity {
	public int amount;
	public Date date;
	public String description;

	public Expense(int amount, Date date, String description) {
		super();
		this.amount = amount;
		this.date = date;
		this.description = description;
	}

	public Expense() {}

}
