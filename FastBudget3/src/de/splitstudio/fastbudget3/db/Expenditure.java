package de.splitstudio.fastbudget3.db;

import java.util.Date;

//TODO (Dec 23, 2013): Frauke sacht, das könnte falsch sein...
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
