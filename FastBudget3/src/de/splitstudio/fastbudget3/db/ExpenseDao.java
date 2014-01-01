package de.splitstudio.fastbudget3.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.GenericBaseDao;

public class ExpenseDao extends GenericBaseDao<Expense> {

	public ExpenseDao(ObjectContainer db) {
		super(db);
	}

	public List<String> findAllDescriptions() {
		return sort(countDescriptions());
	}

	private List<String> sort(final Map<String, Integer> count) {
		List<String> sorted = new ArrayList<String>();
		sorted.addAll(count.keySet());
		Collections.sort(sorted, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				int compareByCount = count.get(rhs).compareTo(count.get(lhs));
				if (compareByCount != 0) {
					return compareByCount;
				}
				return lhs.compareTo(rhs);
			}
		});
		return sorted;
	}

	Map<String, Integer> countDescriptions() {
		Map<String, Integer> count = new HashMap<String, Integer>();
		for (Expense expense : findAll(Expense.class)) {
			String description = expense.description;
			if (count.containsKey(description)) {
				count.put(description, count.get(description) + 1);
			} else {
				count.put(description, 1);
			}
		}
		return count;
	}

}
