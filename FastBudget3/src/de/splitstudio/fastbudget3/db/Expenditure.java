package de.splitstudio.fastbudget3.db;

import java.util.Date;

public class Expenditure {
	public int amount;
	public Date date;
	public String description;

	public Expenditure(int amount, Date date, String description) {
		this.amount = amount;
		this.date = date;
		this.description = description;
	}

}
