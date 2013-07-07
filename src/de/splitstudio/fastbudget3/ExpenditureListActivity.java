package de.splitstudio.fastbudget3;

import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.db.ExpenditureListAdapter;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;

public class ExpenditureListActivity extends ListActivity {

	private ObjectContainer db;

	Calendar start;
	Calendar end;

	final Runnable update = new Runnable() {
		@Override
		public void run() {
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
		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		setTitle(categoryName);

		db = Database.getInstance(this);
		start = DateUtils.createFirstDayOfMonth();
		end = DateUtils.createLastDayOfMonth();
		category = Database.findCategory(categoryName);
		List<Expenditure> expenditures = category.findExpenditures(start.getTime(), end.getTime());
		adapter = new ExpenditureListAdapter(getLayoutInflater(), expenditures);
		setListAdapter(adapter);
		setContentView(R.layout.expenditure_list_activity);
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
