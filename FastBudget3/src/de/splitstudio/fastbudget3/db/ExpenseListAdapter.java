package de.splitstudio.fastbudget3.db;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.ObjectListAdapter;

public class ExpenseListAdapter extends ObjectListAdapter<Expense> {

	public ExpenseListAdapter(LayoutInflater layoutInflater, List<Expense> objects) {
		super(layoutInflater, R.layout.expense_row, objects);
	}

	@Override
	public void bindView(View view, Expense expense) {
		view.findViewById(R.id.button_list).setTag(expense.hashCode());
		view.findViewById(R.id.button_delete).setTag(expense.hashCode());
		view.findViewById(R.id.button_edit).setTag(expense.hashCode());
		view.findViewById(R.id.context_row).setTag(expense.hashCode());

		((TextView) view.findViewById(R.id.description)).setText(expense.description);
		((TextView) view.findViewById(R.id.amount)).setText(NumberUtils.formatAsCurrency(expense.amount));
		((TextView) view.findViewById(R.id.date_field)).setText(DateUtils.formatAsShortDate(expense.date));
	}

	@Override
	public void update(List<Expense> expenses) {
		objects.clear();
		objects.addAll(expenses);
		notifyDataSetChanged();
	}

}
