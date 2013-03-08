package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.List;

public class Category {

	public String name;
	public int budget;
	public List<Expenditure> expenditures;

	public Category(String name, int budget) {
		this.name = name;
		this.budget = budget;
		this.expenditures = new ArrayList<Expenditure>();
	}

	public Category(String name) {
		this(name, 0);
	}

}
