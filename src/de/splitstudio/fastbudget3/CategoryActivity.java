package de.splitstudio.fastbudget3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity);
		setTitle(R.string.add_category);
	}

	public void save(View view) {
		startActivity(new Intent(this, OverviewActivity.class));
	}

}
