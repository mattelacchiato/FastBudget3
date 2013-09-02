package de.splitstudio.fastbudget3.db;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.ObjectExpandableListAdapter;

public class CategoryExpandableListAdapter extends ObjectExpandableListAdapter<Category> {

	private final Locale locale;
	private final Date start;

	public CategoryExpandableListAdapter(LayoutInflater layoutInflater, List<Category> categories) {
		super(layoutInflater, categories, R.layout.category_row, 0);
		locale = layoutInflater.getContext().getResources().getConfiguration().locale;
		start = DateUtils.createFirstDayOfMonth().getTime();
	}

	@Override
	public void bindGroupView(View view, Category category) {
		int expendituresCent = category.summarizeExpenditures(start, null);
		int budget = category.calcBudget();
		String budgetLiteral = NumberUtils.formatAsCurrency(budget, locale);
		String expenditures = NumberUtils.formatAsCurrency(expendituresCent, locale);

		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.category_fill);
		progressBar.setMax(budget);
		progressBar.setProgress(expendituresCent);

		((TextView) view.findViewById(R.id.name)).setText(category.name);
		((TextView) view.findViewById(R.id.category_budget)).setText(budgetLiteral);
		((TextView) view.findViewById(R.id.category_spent)).setText(expenditures);
	}

	@Override
	public void bindChildView(View view, Category category) {
		view.findViewById(R.id.delete_category).setTag(category.name);
	}

}