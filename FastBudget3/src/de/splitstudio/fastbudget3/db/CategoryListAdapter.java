package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.NumberUtils.formatAsCurrency;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.db.ObjectListAdapter;

public class CategoryListAdapter extends ObjectListAdapter<Category> {

	private static final String TAG = CategoryListAdapter.class.getSimpleName();
	private final Date start;

	public CategoryListAdapter(LayoutInflater layoutInflater, List<Category> categories) {
		super(layoutInflater, R.layout.category_row, categories);
		start = DateUtils.createFirstDayOfMonth().getTime();
	}

	@Override
	public void bindView(View view, Category category) {
		int expensesCent = category.summarizeExpenses(start, null);
		int budgetCent = category.calculateBudget();
		String budgetString = formatAsCurrency(budgetCent);
		String expensesString = formatAsCurrency(expensesCent);

		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.category_fill);
		if (budgetCent < 0) {
			progressBar.setMax(1);
			progressBar.setProgress(1);
		} else {
			progressBar.setMax(budgetCent);
			progressBar.setProgress(expensesCent);
		}

		view.findViewById(R.id.button_add_expense).setTag(category.name);
		view.findViewById(R.id.button_list).setTag(category.name);
		view.findViewById(R.id.button_edit).setTag(category.name);
		view.findViewById(R.id.button_delete).setTag(category.name);

		((TextView) view.findViewById(R.id.name)).setText(category.name);
		((TextView) view.findViewById(R.id.category_budget)).setText(budgetString);
		((TextView) view.findViewById(R.id.category_spent)).setText(expensesString);
	}

	@Override
	public void update(List<Category> categories) {
		objects.clear();
		objects.addAll(categories);
		Collections.sort(objects);
		notifyDataSetChanged();
	}

}
