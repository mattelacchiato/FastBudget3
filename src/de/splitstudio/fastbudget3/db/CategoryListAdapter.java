package de.splitstudio.fastbudget3.db;

import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.ObjectListAdapter;

public class CategoryListAdapter extends ObjectListAdapter<Category> {

	private final Locale locale;

	public CategoryListAdapter(LayoutInflater layoutInflater, List<Category> categories) {
		super(layoutInflater, R.layout.category_row, categories);
		locale = layoutInflater.getContext().getResources().getConfiguration().locale;
	}

	@Override
	public void bindView(View view, Category category) {
		String budget = NumberUtils.formatAsCurrency(category.budget, locale);

		((TextView) view.findViewById(R.id.category_name)).setText(category.name);
		((TextView) view.findViewById(R.id.category_budget)).setText(budget);
	}
}
