package de.splitstudio.fastbudget3;

import static android.widget.Toast.LENGTH_LONG;

import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.db.ExpenditureListAdapter;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;

public class ExpenditureListActivity extends ListActivity {

	Calendar start;
	Calendar end;

	final Runnable update = new Runnable() {
		@Override
		public void run() {
			if (end.before(start)) {
				Toast.makeText(getApplicationContext(), R.string.error_end_before_start, LENGTH_LONG).show();
			}
			((Button) findViewById(R.id.date_start)).setText(DateUtils.formatAsShortDate(start.getTime()));
			((Button) findViewById(R.id.date_end)).setText(DateUtils.formatAsShortDate(end.getTime()));
			adapter.update(category.findExpenditures(start.getTime(), end.getTime()));
		}
	};

	private ExpenditureListAdapter adapter;

	private Category category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO (31.08.2013): exit when intent hasn't any extras or this string
		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		setTitle(categoryName);
		setContentView(R.layout.expenditure_list_activity);

		Database.getInstance(this);
		start = DateUtils.createFirstDayOfMonth();
		end = DateUtils.createLastDayOfMonth();
		category = Database.findCategory(categoryName);
		List<Expenditure> expenditures = category.findExpenditures(start.getTime(), end.getTime());
		adapter = new ExpenditureListAdapter(LayoutInflater.from(this), expenditures);
		setListAdapter(adapter);
		update.run();
	}

	public void pickDate(View view) {
		if (view.getId() == R.id.date_start) {
			DialogHelper.pickDate(this, start, update);
		} else if (view.getId() == R.id.date_end) {
			DialogHelper.pickDate(this, end, update);
		}
	}

}
