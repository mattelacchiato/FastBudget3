package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static de.splitstudio.utils.NumberUtils.formatAsCurrency;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.db.ObjectListAdapter;

public class ExpenseListAdapter extends ObjectListAdapter<Expense> {

	public ExpenseListAdapter(LayoutInflater layoutInflater, List<Expense> objects) {
		super(layoutInflater, R.layout.expense_row, objects);
	}

	@Override
	public void bindView(View view, Expense expense) {
		view.findViewById(R.id.button_edit).setTag(expense.uuid2);
		view.findViewById(R.id.button_delete).setTag(expense.uuid2);
		((TextView) view.findViewById(R.id.description)).setText(expense.description);
		((TextView) view.findViewById(R.id.amount)).setText(formatAsCurrency(expense.amount));
		((TextView) view.findViewById(R.id.date_field)).setText(formatAsShortDate(expense.date));
	}

	@Override
	public void update(List<Expense> expenses) {
		objects.clear();
		objects.addAll(expenses);
		notifyDataSetChanged();
	}

}
