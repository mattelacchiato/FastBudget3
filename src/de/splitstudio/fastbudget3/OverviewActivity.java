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

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.Database;

public class OverviewActivity extends ListActivity {

	private List<Category> categories;

	private ObjectContainer db;

	private CategoryListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = Database.getInstance(getContext());
		categories = new ArrayList<Category>();
		categories.addAll(db.query(Category.class));
		setContentView(R.layout.overview_activity);
		listAdapter = new CategoryListAdapter(LayoutInflater.from(this), categories);
		setListAdapter(listAdapter);
		addCategoryListener();
		requeryCategories();
	}

	private void requeryCategories() {
		categories.clear();
		categories.addAll(db.query(Category.class));
		listAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCode.CreateCategory.ordinal() && resultCode == RESULT_OK) {
			requeryCategories();
		}
	}

	private void addCategoryListener() {
		findViewById(R.id.button_list_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), CategoryActivity.class);
				startActivityForResult(intent, RequestCode.CreateCategory.ordinal());
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
