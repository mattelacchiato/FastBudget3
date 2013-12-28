package de.splitstudio.fastbudget3;

import static de.splitstudio.fastbudget3.CategoryValidator.CategoryValidationResult.Duplicate;
import static de.splitstudio.fastbudget3.CategoryValidator.CategoryValidationResult.Ok;
import static de.splitstudio.fastbudget3.enums.Extras.CategoryName;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.NumberUtils;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

public class CategoryActivity extends Activity {

	private DatePickerButtons datePicker;

	private EditText nameEdit;

	private Calculator calculator;

	private Category category;

	private boolean updateCategory = true;

	private CategoryDao categoryDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		categoryDao = new CategoryDao(Database.getInstance(this));

		setContentView(R.layout.category_activity);

		datePicker = (DatePickerButtons) findViewById(R.id.date_picker);
		nameEdit = (EditText) findViewById(R.id.name);
		calculator = (Calculator) findViewById(R.id.calculator);

		initFields();
	}

	private void initFields() {
		updateCategory = getIntent().hasExtra(Extras.CategoryName.name());
		if (updateCategory) {
			setTitle(R.string.edit);
			String name = getIntent().getExtras().getString(CategoryName.name());
			category = categoryDao.findByName(name);
			nameEdit.setText(category.name);
			calculator.setAmount(NumberUtils.formatAsDecimal(category.budget));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(category.date);
			datePicker.setAndUpdateDate(calendar);
		} else {
			setTitle(R.string.add_category);
			category = new Category();
			datePicker.setAndUpdateDate(DateUtils.createFirstDayOfYear());
		}
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

	public void save() {
		String amountString = calculator.getAmount();
		category.name = nameEdit.getText().toString();

		CategoryValidator validator = new CategoryValidator(categoryDao, category.name, amountString);
		if (!isValid(validator)) {
			Toast.makeText(this, validator.getResult().stringId, Toast.LENGTH_LONG).show();
			return;
		}
		category.budget = validator.getAmountInCent();
		category.date = datePicker.getDate().getTime();

		categoryDao.store(category);
		setResultAndFinish(RESULT_OK);
	}

	private boolean isValid(CategoryValidator validator) {
		boolean valid = validator.getResult() == Ok;
		boolean duplicateNameAndUpdating = validator.getResult() == Duplicate && updateCategory;
		return valid || duplicateNameAndUpdating;
	}

	public void cancel() {
		setResultAndFinish(RESULT_CANCELED);
	}

	private void setResultAndFinish(int resultCode) {
		setResult(resultCode);
		finish();
	}

}
