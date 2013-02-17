package de.splitstudio.fastbudget3;

import android.app.Activity;
import android.os.Bundle;

public class CategoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity);
		setTitle(R.string.add_category);
	}

}
