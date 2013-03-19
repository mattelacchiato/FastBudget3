package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Date;
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

	public int summarizeExpenditures(Date start, Date end) {
		int sum = 0;
		for (Expenditure expenditure : expenditures) {
			boolean startValid = start == null || expenditure.date.after(start);
			boolean endValid = end == null || expenditure.date.before(end);
			if (startValid && endValid) {
				sum += expenditure.amount;
			}
		}
		return sum;
	}

	public int summarizeExpendituresForMonth(int i) {
		return 0;
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

}
