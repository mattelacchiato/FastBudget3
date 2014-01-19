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

	/**
	 * sorted first by count and then alphabetically.
	 */
	private List<String> sort(final Map<String, Integer> descriptionsCount) {
		List<String> sortedDescriptions = new ArrayList<String>();
		sortedDescriptions.addAll(descriptionsCount.keySet());
		Collections.sort(sortedDescriptions, new Comparator<String>() {
			@Override
			public int compare(String left, String right) {
				int compareByCount = descriptionsCount.get(right).compareTo(descriptionsCount.get(left));
				if (compareByCount != 0) {
					return compareByCount;
				}
				return left.compareTo(right);
			}
		});
		return sortedDescriptions;
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
