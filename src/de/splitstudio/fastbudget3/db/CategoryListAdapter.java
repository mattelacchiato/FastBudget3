package de.splitstudio.fastbudget3.db;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.ObjectListAdapter;

public class CategoryListAdapter extends ObjectListAdapter<Category> {

	private final NumberFormat numberFormat;

	public CategoryListAdapter(LayoutInflater layoutInflater, List<Category> categories) {
		super(layoutInflater, R.layout.category_row, categories);
		Locale locale = layoutInflater.getContext().getResources().getConfiguration().locale;
		numberFormat = NumberFormat.getCurrencyInstance(locale);
	}

	@Override
	public void bindView(View view, Category category) {
		String budget = numberFormat.format(category.budget / 100.0);

		((TextView) view.findViewById(R.id.category_name)).setText(category.name);
		((TextView) view.findViewById(R.id.category_budget)).setText(budget);
	}
}
