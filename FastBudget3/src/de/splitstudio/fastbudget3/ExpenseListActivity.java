package de.splitstudio.fastbudget3;

import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_LONG;
import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static de.splitstudio.utils.view.ViewHelper.getViewsById;

import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.db4o.ObjectContainer;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.db.ExpenseDao;
import de.splitstudio.fastbudget3.db.ExpenseListAdapter;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.fastbudget3.enums.RequestCode;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;
import de.splitstudio.utils.db.Database;

public class ExpenseListActivity extends ListActivity {

	private static final String TAG = ListActivity.class.getSimpleName();

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

	Calendar start;
	Calendar end;

	private ExpenseListAdapter adapter;
	private Category category;
	private CategoryDao categoryDao;
	private ExpenseDao expenseDao;

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

		start = DateUtils.createFirstDayOfMonth();
		end = DateUtils.createLastDayOfMonth();
		ObjectContainer db = Database.getInstance(this);
		categoryDao = new CategoryDao(db);
		expenseDao = new ExpenseDao(db);
		category = categoryDao.findByName(categoryName);
		List<Expense> expenses = category.findExpenses(start.getTime(), end.getTime());
		adapter = new ExpenseListAdapter(LayoutInflater.from(this), expenses);
		setListAdapter(new SlideExpandableListAdapter(adapter, R.id.context_switcher, R.id.context_row));
		update.run();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			update.run();
		}
		hideAllContexts();
	}

	public void pickDate(View view) {
		if (view.getId() == R.id.date_start) {
			DialogHelper.pickDate(this, start, update);
		} else if (view.getId() == R.id.date_end) {
			DialogHelper.pickDate(this, end, update);
		}
	}

	public void editExpense(View view) {
		Intent intent = new Intent(this, ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), category.name);
		intent.putExtra(Extras.Id.name(), view.getTag().toString());
		startActivityForResult(intent, RequestCode.EditExpense.ordinal());
	}

	public void deleteExpense(View view) {
		String uuid = view.getTag().toString();
		Expense expense = expenseDao.findByUuid(uuid);
		if (expense == null) {
			throw new IllegalStateException("Could not find Expense for uuid " + uuid);
		}
		expenseDao.delete(expense);
		category.expenses.remove(expense);
		update.run();
	}

	private void hideAllContexts() {
		for (View view : getViewsById(getListView(), R.id.context_row)) {
			view.setVisibility(GONE);
		}
	}
}
