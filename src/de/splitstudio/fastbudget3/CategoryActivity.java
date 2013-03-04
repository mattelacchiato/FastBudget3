package de.splitstudio.fastbudget3;

import java.text.ParseException;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryStorage;
import de.splitstudio.utils.NumberUtils;

public class CategoryActivity extends Activity {

	CategoryStorage storage;

	Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		storage = CategoryStorage.getInstance(this);
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.category_activity);
		setTitle(R.string.add_category);
	}

	public void save(View view) {
		String name = ((EditText) findViewById(R.id.category_name)).getText().toString();
		int amount = parseAmount();

		if (name.trim().isEmpty()) {
			Toast.makeText(this, R.string.error_name_empty, Toast.LENGTH_LONG).show();
			return;
		}
		if (storage.contains(name)) {
			Toast.makeText(this, R.string.error_name_duplicated, Toast.LENGTH_LONG).show();
			return;
		}
		if (amount == 0) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
			return;
		}

		storage.push(new Category(name, amount));
		goToOverview();
	}

	public void cancel(View view) {
		goToOverview();
	}

	private void goToOverview() {
		startActivity(new Intent(this, OverviewActivity.class));
	}

	private int parseAmount() {
		String amountString = ((EditText) findViewById(R.id.calculator_amount)).getText().toString();
		try {
			return NumberUtils.parseCent(amountString, locale);
		} catch (ParseException e) {
			return 0;
		}
	}
}
