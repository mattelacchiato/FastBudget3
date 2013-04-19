package de.splitstudio.fastbudget3;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.CategoryValidator.CategoryValidationResult;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.view.Calculator;
import de.splitstudio.utils.view.DatePickerButtons;

public class CategoryActivity extends Activity {

	ObjectContainer db;

	Locale locale;

	private DatePickerButtons datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = Database.getInstance(this);
		locale = getResources().getConfiguration().locale;

		setContentView(R.layout.category_activity);
		setTitle(R.string.add_category);

		datePicker = (DatePickerButtons) findViewById(R.id.date_picker);
		datePicker.setAndUpdateDate(DateUtils.createFirstDayOfYear());
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

	//TODO cleanup
	public void save() {
		String name = ((EditText) findViewById(R.id.name)).getText().toString();
		String amount = ((Calculator) findViewById(R.id.calculator)).getAmount();

		CategoryValidator validator = new CategoryValidator(db, name, amount);
		if (validator.getResult() != CategoryValidationResult.Ok) {
			Toast.makeText(this, validator.getResult().stringId, Toast.LENGTH_LONG).show();
			return;
		}

		db.store(new Category(name, validator.getAmountInCent(), datePicker.getDate().getTime()));
		db.commit();

		setResultAndFinish(RESULT_OK);
	}

	public void cancel() {
		setResultAndFinish(RESULT_CANCELED);
	}

	private void setResultAndFinish(int resultCode) {
		setResult(resultCode);
		finish();
	}

}
