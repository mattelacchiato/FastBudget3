package de.splitstudio.fastbudget3;

import static de.splitstudio.utils.NumberUtils.centToDouble;
import static java.lang.String.format;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.Database;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.fastbudget3.enums.RequestCode;
import de.splitstudio.utils.DateUtils;

public class OverviewActivity extends ListActivity {

	List<Category> categories;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.add_category):
			addCategory();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	public void addExpenditure(View view) {
		View parent = (View) view.getParent();
		TextView nameTextView = (TextView) parent.findViewById(R.id.name);
		String categoryName = nameTextView.getText().toString();

		Intent intent = new Intent(getContext(), ExpenditureActivity.class);
		intent.putExtra(Extras.CategoryName.name(), categoryName);
		startActivityForResult(intent, RequestCode.CreateExpenditure.ordinal());
	}

	void requeryCategories() {
		categories.clear();
		categories.addAll(db.query(Category.class));
		Collections.sort(categories);
		listAdapter.notifyDataSetChanged();
		updateTitle();
	}

	private void addCategory() {
		Intent intent = new Intent(getContext(), CategoryActivity.class);
		startActivityForResult(intent, RequestCode.CreateCategory.ordinal());
	}

	private void updateTitle() {
		NumberFormat currency = NumberFormat.getCurrencyInstance();
		currency.setMaximumFractionDigits(0);

		int budgetCents = 0;
		int spentCents = 0;
		for (Category category : categories) {
			budgetCents += category.calcBudget();
			spentCents += category.summarizeExpenditures(DateUtils.createFirstDayOfMonth().getTime(), null);
		}

		String spent = currency.format(centToDouble(spentCents));
		String budget = currency.format(centToDouble(budgetCents));
		setTitle(format("%s  %s/%s", getString(R.string.app_name), spent, budget));
	}

	private Context getContext() {
		return this;
	}

}
