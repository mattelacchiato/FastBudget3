package de.splitstudio.fastbudget3;

import java.text.ParseException;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.utils.NumberUtils;

public class CategoryActivity extends Activity {

	ObjectContainer db;

	Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = Database.getInstance(this);
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
		if (!db.queryByExample(new Category(name)).isEmpty()) {
			Toast.makeText(this, R.string.error_name_duplicated, Toast.LENGTH_LONG).show();
			return;
		}
		if (amount == 0) {
			Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_LONG).show();
			return;
		}

		db.store(new Category(name, amount));
		db.commit();
		goToOverview();
	}

	public void cancel(View view) {
		goToOverview();
	}

	private void goToOverview() {
		finish();
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
