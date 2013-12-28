package de.splitstudio.fastbudget3;

import static de.splitstudio.utils.DateUtils.formatAsShortDate;
import static de.splitstudio.utils.NumberUtils.formatAsDecimal;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.Expense;
import de.splitstudio.fastbudget3.db.ExpenseDao;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

public class ExpenseActivity extends Activity {

	Category category;

	private ObjectContainer db;

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
		setContentView(R.layout.expense_activity);
		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		db = Database.getInstance(this);
		expenseDao = new ExpenseDao(db);
		categoryDao = new CategoryDao(db);
		category = categoryDao.findCategory(categoryName);
		setTitle(getString(R.string.title_expense, categoryName));
		fillValues(getIntent().getExtras());
	}

	private void fillValues(Bundle extras) {
		if (extras.containsKey(Extras.Id.name())) {
			expense = expenseDao.findByUuid(extras.getString(Extras.Id.name()));
			((TextView) findViewById(R.id.description)).setText(expense.description);
			((TextView) findViewById(R.id.calculator_amount)).setText(formatAsDecimal(expense.amount));
			((TextView) findViewById(R.id.date_field)).setText(formatAsShortDate(expense.date));
		} else {
			expense = new Expense(new Date());
		}
	}

	//TODO (Dec 25, 2013): benchmark: findByExample vs. Predicate?

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
			expense.amount = NumberUtils.parseCent(calculator.getAmount());
			expense.date = datePickerButtons.getDate().getTime();
			expense.description = descriptionEdit.getText().toString();

			category.expenses.add(expense);

			categoryDao.store(category);
			setResult(RESULT_OK);
			finish();
		} catch (ParseException e) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
		}
	}
}
