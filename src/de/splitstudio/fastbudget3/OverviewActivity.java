package de.splitstudio.fastbudget3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.fastbudget3.enums.RequestCode;

public class OverviewActivity extends ListActivity {

	//TODO: add ActionBar

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
		requeryCategories();
	}

	@Override
	protected void onActivityResult(int requestCodeOrdinal, int resultCode, Intent data) {
		RequestCode requestCode = RequestCode.values()[requestCodeOrdinal];
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CreateCategory:
			case CreateExpenditure:
				requeryCategories();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.overview_activity, menu);
		return true;
	}

	/*
	 * START Listeners
	 */

	public void addExpenditure(View view) {
		View parent = (View) view.getParent();
		TextView nameTextView = (TextView) parent.findViewById(R.id.name);
		String categoryName = nameTextView.getText().toString();

		Intent intent = new Intent(getContext(), ExpenditureActivity.class);
		intent.putExtra(Extras.CategoryName.name(), categoryName);
		startActivityForResult(intent, RequestCode.CreateExpenditure.ordinal());
	}

	public void addCategory(View view) {
		Intent intent = new Intent(getContext(), CategoryActivity.class);
		startActivityForResult(intent, RequestCode.CreateCategory.ordinal());
	}

	/*
	 * END Listeners
	 */

	void requeryCategories() {
		categories.clear();
		categories.addAll(db.query(Category.class));
		Collections.sort(categories);
		listAdapter.notifyDataSetChanged();
	}

	private Context getContext() {
		return this;
	}

}
