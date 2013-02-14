package de.splitstudio.fastbudget3;

import java.util.Collections;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;

//TODO 13.02.2013: rename to activity
public class Overview extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview_activity);
		View listFooter = LayoutInflater.from(getContext()).inflate(R.layout.category_add, null);
		((ListView) findViewById(android.R.id.list)).addFooterView(listFooter);

		CategoryListAdapter categoryListAdapter = new CategoryListAdapter(LayoutInflater.from(this),
				Collections.<Category> emptyList());
		setListAdapter(categoryListAdapter);

		listFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getContext(), CategoryActivity.class));
			}
		});
	}

	private Context getContext() {
		return this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overview_activity, menu);
		return true;
	}

}
