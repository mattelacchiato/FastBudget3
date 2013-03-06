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
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.CategoryStorage;

public class OverviewActivity extends ListActivity {

	List<Category> categories;
	CategoryStorage storage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		categories = new ArrayList<Category>();
		storage = CategoryStorage.getInstance(getContext());

		categories.addAll(storage.getAll());
		setContentView(R.layout.overview_activity);
		setListAdapter(new CategoryListAdapter(LayoutInflater.from(this), categories));
		setAddListener();
	}

	private void setAddListener() {
		findViewById(R.id.button_list_add).setOnClickListener(new OnClickListener() {
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
		getMenuInflater().inflate(R.menu.overview_activity, menu);
		return true;
	}

}
