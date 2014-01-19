package de.splitstudio.fastbudget3;

import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.Duplicate;
import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.Ok;
import static de.splitstudio.fastbudget3.enums.Extras.CategoryName;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.CategoryValidator;
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

	private boolean updateCategory;

	private CategoryDao categoryDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity);

		categoryDao = new CategoryDao(Database.getInstance(this));

		datePicker = (DatePickerButtons) findViewById(R.id.date_picker);
		nameEdit = (EditText) findViewById(R.id.name);
		calculator = (Calculator) findViewById(R.id.calculator);

		initFields();
	}

	private void initFields() {
		//TODO (Jan 1, 2014): use uuid
		updateCategory = getIntent().hasExtra(Extras.CategoryName.name());
		if (updateCategory) {
			setTitle(R.string.edit);
			String name = getIntent().getExtras().getString(CategoryName.name());
			category = categoryDao.findByName(name);
			nameEdit.setText(category.name);
			calculator.setAmount(NumberUtils.formatAsDecimal(category.budget));
			datePicker.setAndUpdateDate(category.date);
		} else {
			setTitle(R.string.create_category);
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
		boolean duplicateNameButIsUpdating = validator.getResult() == Duplicate && updateCategory;
		return valid || duplicateNameButIsUpdating;
	}

	public void cancel() {
		setResultAndFinish(RESULT_CANCELED);
	}

	private void setResultAndFinish(int resultCode) {
		setResult(resultCode);
		finish();
	}

}
