package de.splitstudio.fastbudget3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.splitstudio.fastbudget3.db.CategoryStorage;

public class CategoryActivity extends Activity {

	CategoryStorage storage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		storage = CategoryStorage.getInstance(this);
		setContentView(R.layout.category_activity);
		setTitle(R.string.add_category);
	}

	public void save(View view) {
		String name = ((EditText) findViewById(R.id.category_name)).getText().toString();
		int amount = parseAmount();
		if (name.trim().isEmpty()) {
			Toast.makeText(this, R.string.error_name_empty, Toast.LENGTH_LONG).show();
		} else if (storage.contains(name)) {
			Toast.makeText(this, R.string.error_name_duplicated, Toast.LENGTH_LONG).show();
		} else {
			startActivity(new Intent(this, OverviewActivity.class));
		}
	}

	private int parseAmount() {
		String amountString = ((EditText) findViewById(R.id.calculator_amount)).getText().toString();
		try {
			float amountFloat = Float.parseFloat(amountString);
		} catch (NumberFormatException e) {

		}
		return 0;
	}
}
