package de.splitstudio.fastbudget3;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.db.Expenditure;
import de.splitstudio.fastbudget3.enums.Extras;
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
		category = (Category) db.queryByExample(new Category(categoryName)).get(0);
		setTitle(getString(R.string.title_expenditure, categoryName));
		setContentView(R.layout.expenditure_activity);
	}

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void save(View view) {
		Calculator calculator = (Calculator) findViewById(R.id.calculator);
		DatePickerButtons datePickerButtons = (DatePickerButtons) findViewById(R.id.date_picker);
		EditText descriptionEdit = (EditText) findViewById(R.id.description);

		try {
			int amount = calculator.parseAmountInCent();
			Date date = datePickerButtons.getDate().getTime();
			String description = descriptionEdit.getText().toString();
			category.expenditures.add(new Expenditure(amount, date, description));
			setResult(RESULT_OK);
			finish();
		} catch (ParseException e) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
		}
	}
}
