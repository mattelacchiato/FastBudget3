package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static de.splitstudio.utils.NumberUtils.formatAsCurrency;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.db.ObjectListAdapter;

public class ExpenseListAdapter extends ObjectListAdapter<Expense> {

	private final Activity activity;

	public ExpenseListAdapter(Activity activity, List<Expense> objects) {
		super(LayoutInflater.from(activity), R.layout.expense_row, objects);
		this.activity = activity;
	}

	@Override
	public void bindView(View view, Expense expense) {
		activity.registerForContextMenu(view.findViewById(R.id.button_move));

		view.findViewById(R.id.button_edit).setTag(expense.uuid);
		view.findViewById(R.id.button_move).setTag(expense.uuid);
		view.findViewById(R.id.button_delete).setTag(expense.uuid);

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
