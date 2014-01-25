package de.splitstudio.fastbudget3;

import static de.splitstudio.utils.NumberUtils.formatAsDecimal;
import static de.splitstudio.utils.NumberUtils.parseCent;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.db.ExpenseDao;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

public class ExpenseActivity extends Activity {

	private static final String TAG = ExpenseActivity.class.getSimpleName();

	Category category;

	private Expense expense;

	private ExpenseDao expenseDao;
	private CategoryDao categoryDao;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (!getIntent().hasExtra(Extras.CategoryName.name())) {
			finish();
			return;
		}

		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		setContentView(R.layout.expense_activity);
		setTitle(getString(R.string.title_expense, categoryName));

		ObjectContainer db = Database.getInstance(this);
		expenseDao = new ExpenseDao(db);
		categoryDao = new CategoryDao(db);
		category = categoryDao.findByName(categoryName);
		expense = loadExpense();

		fillView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_save_cancel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.save):
			save();
			return true;
		case (R.id.cancel):
			cancel();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void cancel() {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void save() {
		Calculator calculator = (Calculator) findViewById(R.id.calculator);
		DatePickerButtons datePickerButtons = (DatePickerButtons) findViewById(R.id.date_picker);
		EditText descriptionEdit = (EditText) findViewById(R.id.description);

		try {
			expense.amount = parseCent(calculator.getAmount());
			expense.date = datePickerButtons.getDate().getTime();
			expense.description = descriptionEdit.getText().toString();

			category.add(expense);
			categoryDao.store(category);
			Log.d(TAG, "Persisted expense in db: " + expense);
			setResult(RESULT_OK);
			finish();
		} catch (ParseException e) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
		}
	}

	private Expense loadExpense() {
		if (isUpdating()) {
			String uuid = getIntent().getExtras().getString(Extras.Uuid.name());
			Log.d(TAG, "fill expense by loading its values from db with uuid " + uuid);
			return expenseDao.findByUuid(uuid);
		}
		Log.d(TAG, "no uuid given, will create a new expense");
		return new Expense(new Date());
	}

	private boolean isUpdating() {
		return getIntent().getExtras().containsKey(Extras.Uuid.name());
	}

	private void fillView() {
		AutoCompleteTextView descriptionTextView = (AutoCompleteTextView) findViewById(R.id.description);
		descriptionTextView.setText(expense.description);
		descriptionTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, expenseDao
				.findAllDescriptions()));

		((DatePickerButtons) findViewById(R.id.date_picker)).setAndUpdateDate(expense.date);
		if (isUpdating()) {
			((TextView) findViewById(R.id.calculator_amount)).setText(formatAsDecimal(expense.amount));
		}
	}

}
