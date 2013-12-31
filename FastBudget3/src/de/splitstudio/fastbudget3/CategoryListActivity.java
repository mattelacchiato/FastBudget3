package de.splitstudio.fastbudget3;

import static android.view.View.GONE;
import static de.splitstudio.utils.NumberUtils.centToDouble;
import static de.splitstudio.utils.view.ViewHelper.getViewsById;
import static java.lang.String.format;

import java.text.NumberFormat;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.fastbudget3.enums.RequestCode;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;
import de.splitstudio.utils.db.Database;

public class CategoryListActivity extends ListActivity {

	private CategoryListAdapter listAdapter;

	protected CategoryDao categoryDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list_activity);
		categoryDao = new CategoryDao(Database.getInstance(this));

		List<Category> categories = categoryDao.findAll(Category.class);
		listAdapter = new CategoryListAdapter(getLayoutInflater(), categories);
		setListAdapter(new SlideExpandableListAdapter(listAdapter, R.id.context_switcher, R.id.context_row));
		updateView(categories);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_category_list, menu);
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
			case EditCategory:
			case CreateCategory:
			case CreateExpense:
				updateView();
				break;
			default:
				break;
			}
		}
		hideAllContexts();
	}

	private void hideAllContexts() {
		for (View view : getViewsById(getListView(), R.id.context_row)) {
			view.setVisibility(GONE);
		}
	}

	public void addExpense(View view) {
		View parent = (View) view.getParent();
		TextView nameTextView = (TextView) parent.findViewById(R.id.name);
		String categoryName = nameTextView.getText().toString();

		Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), categoryName);
		startActivityForResult(intent, RequestCode.CreateExpense.ordinal());
	}

	public void editCategory(View view) {
		Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
		intent.putExtra(Extras.CategoryName.name(), (String) view.getTag());
		startActivityForResult(intent, RequestCode.EditCategory.ordinal());
	}

	public void deleteCategory(final View view) {
		final String name = (String) view.getTag();
		DialogHelper.createQuestion(this, R.string.warning, R.string.warning_delete_category, R.string.cancel,
			R.string.ok, new Runnable() {
				@Override
				public void run() {
					categoryDao.delete(name);
					updateView();
				}
			});
	}

	public void openExpenseList(View view) {
		Intent intent = new Intent(getApplicationContext(), ExpenseListActivity.class);
		intent.putExtra(Extras.CategoryName.name(), (String) view.getTag());
		startActivity(intent);
	}

	public void updateView() {
		updateView(categoryDao.findAll(Category.class));
	}

	private void updateView(List<Category> categories) {
		listAdapter.update(categories);
		updateTitle(categories);
	}

	private void addCategory() {
		Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
		startActivityForResult(intent, RequestCode.CreateCategory.ordinal());
	}

	private void updateTitle(List<Category> categories) {
		NumberFormat currency = NumberFormat.getCurrencyInstance();
		currency.setMaximumFractionDigits(0);

		int budgetCents = 0;
		int spentCents = 0;
		for (Category category : categories) {
			budgetCents += category.calcBudget();
			spentCents += category.summarizeExpenses(DateUtils.createFirstDayOfMonth().getTime(), null);
		}

		String spent = currency.format(centToDouble(spentCents));
		String budget = currency.format(centToDouble(budgetCents));
		setTitle(format("%s  %s/%s", getString(R.string.app_name), spent, budget));
	}

}
