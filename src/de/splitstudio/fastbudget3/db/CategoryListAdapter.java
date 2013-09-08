package de.splitstudio.fastbudget3.db;

import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.ObjectListAdapter;

public class CategoryListAdapter extends ObjectListAdapter<Category> {

	private final Date start;

	public CategoryListAdapter(LayoutInflater layoutInflater, List<Category> categories) {
		super(layoutInflater, R.layout.category_row, categories);
		start = DateUtils.createFirstDayOfMonth().getTime();
	}

	@Override
	public void bindView(View view, Category category) {
		int expendituresCent = category.summarizeExpenditures(start, null);
		int budget = category.calcBudget();
		String budgetLiteral = NumberUtils.formatAsCurrency(budget);
		String expenditures = NumberUtils.formatAsCurrency(expendituresCent);

		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.category_fill);
		progressBar.setMax(budget);
		progressBar.setProgress(expendituresCent);

		view.findViewById(R.id.button_list).setTag(category.name);
		view.findViewById(R.id.button_delete).setTag(category.name);
		view.findViewById(R.id.button_edit).setTag(category.name);
		view.findViewById(R.id.context_row).setTag(category.name);
		((TextView) view.findViewById(R.id.name)).setText(category.name);
		((TextView) view.findViewById(R.id.category_budget)).setText(budgetLiteral);
		((TextView) view.findViewById(R.id.category_spent)).setText(expenditures);
	}

}
