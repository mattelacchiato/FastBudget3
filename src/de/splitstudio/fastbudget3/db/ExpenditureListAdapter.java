package de.splitstudio.fastbudget3.db;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.ObjectListAdapter;

public class ExpenditureListAdapter extends ObjectListAdapter<Expenditure> {

	public ExpenditureListAdapter(LayoutInflater layoutInflater, List<Expenditure> objects) {
		super(layoutInflater, R.layout.expenditure_list_row, objects);
	}

	@Override
	public void bindView(View view, Expenditure expenditure) {
		view.findViewById(R.id.button_list).setTag(expenditure.hashCode());
		view.findViewById(R.id.button_delete).setTag(expenditure.hashCode());
		view.findViewById(R.id.button_edit).setTag(expenditure.hashCode());
		view.findViewById(R.id.context_row).setTag(expenditure.hashCode());

		((TextView) view.findViewById(R.id.description)).setText(expenditure.description);
		((TextView) view.findViewById(R.id.amount)).setText(NumberUtils.formatAsCurrency(expenditure.amount));
		((TextView) view.findViewById(R.id.date_field)).setText(DateUtils.formatAsShortDate(expenditure.date));
	}

	@Override
	public void update(List<Expenditure> expenditures) {
		objects.clear();
		objects.addAll(expenditures);
		notifyDataSetChanged();
	}

}
