package de.splitstudio.fastbudget3.db;

import java.util.Date;
import java.util.UUID;

public class Expense {
	public int amount;
	public Date date;
	public String description;
	public String uuid;

	private Expense() {
		this.uuid = UUID.randomUUID().toString();
	}

	public Expense(int amount, Date date, String description) {
		this();
		this.amount = amount;
		this.date = date;
		this.description = description;
	}

}
