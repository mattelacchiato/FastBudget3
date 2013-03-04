package de.splitstudio.fastbudget3;

import java.util.ArrayList;
import java.util.List;

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
import de.splitstudio.fastbudget3.db.CategoryStorage;

public class OverviewActivity extends ListActivity {

	List<Category> categories = new ArrayList<Category>();
	CategoryStorage storage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		storage = CategoryStorage.getInstance(getContext());
		setContentView(R.layout.overview_activity);
		appendFooterToList();

		CategoryListAdapter categoryListAdapter = new CategoryListAdapter(LayoutInflater.from(this), categories);
		setListAdapter(categoryListAdapter);
	}

	private void appendFooterToList() {
		View listFooter = LayoutInflater.from(getContext()).inflate(R.layout.category_add, null);
		listFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getContext(), CategoryActivity.class));
			}
		});
		((ListView) findViewById(android.R.id.list)).addFooterView(listFooter);
	}

	private Context getContext() {
		return this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.overview_activity, menu);
		return true;
	}

}
