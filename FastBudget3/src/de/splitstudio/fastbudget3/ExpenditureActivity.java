package de.splitstudio.fastbudget3;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

public class ExpenditureActivity extends Activity {

	Category category;

	private ObjectContainer db;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		String categoryName = getIntent().getExtras().getString(Extras.CategoryName.name());
		db = Database.getInstance(this);
		category = Database.findCategory(categoryName);
		setTitle(getString(R.string.title_expenditure, categoryName));
		setContentView(R.layout.expenditure_activity);
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
			int amount = NumberUtils.parseCent(calculator.getAmount());
			Date date = datePickerButtons.getDate().getTime();
			String description = descriptionEdit.getText().toString();
			category.expenditures.add(new Expenditure(amount, date, description));
			db.store(category.expenditures);
			db.commit();
			setResult(RESULT_OK);
			finish();
		} catch (ParseException e) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
		}
	}
}
