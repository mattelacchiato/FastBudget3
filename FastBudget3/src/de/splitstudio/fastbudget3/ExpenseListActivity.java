package de.splitstudio.fastbudget3;

import static android.widget.Toast.LENGTH_LONG;
import static de.splitstudio.utils.DateUtils.formatAsShortDate;

import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.db.ExpenseListAdapter;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;

public class ExpenseListActivity extends ListActivity {

	private static final String TAG = ListActivity.class.getSimpleName();
	Calendar start;
	Calendar end;

	public final Runnable update = new Runnable() {
		@Override
		public void run() {
			if (end.before(start)) {
				Toast.makeText(getApplicationContext(), R.string.error_end_before_start, LENGTH_LONG).show();
			}
			((Button) findViewById(R.id.date_start)).setText(formatAsShortDate(start.getTime()));
			((Button) findViewById(R.id.date_end)).setText(formatAsShortDate(end.getTime()));
			adapter.update(category.findExpenses(start.getTime(), end.getTime()));
		}
	};

	private ExpenseListAdapter adapter;

	private Category category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getIntent().hasExtra(Extras.CategoryName.name())) {
			Log.e(TAG, "No category name given!");
			finish();
			return;
		}
		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		setTitle(categoryName);
		setContentView(R.layout.expense_list_activity);

		Database.getInstance(this);
		start = DateUtils.createFirstDayOfMonth();
		end = DateUtils.createLastDayOfMonth();
		category = Database.findCategory(categoryName);
		List<Expense> expenses = category.findExpenses(start.getTime(), end.getTime());
		adapter = new ExpenseListAdapter(LayoutInflater.from(this), expenses);
		setListAdapter(new SlideExpandableListAdapter(adapter, R.id.context_switcher, R.id.context_row));
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
