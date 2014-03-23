package de.splitstudio.fastbudget3;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.createChooser;
import static de.splitstudio.utils.NumberUtils.formatAsIntegerCurrency;
import static java.lang.String.format;

import java.io.File;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.fastbudget3.db.CategoryListAdapter;
import de.splitstudio.fastbudget3.db.FastBudgetMigration;
import de.splitstudio.fastbudget3.enums.Extras;
import de.splitstudio.fastbudget3.enums.RequestCode;
import de.splitstudio.utils.DateUtils;
import de.splitstudio.utils.activity.DialogHelper;
import de.splitstudio.utils.db.Database;

public class CategoryListActivity extends ListActivity {

	private static final String BACKUP_MIME_TYPE = "application/octet-stream";

	private CategoryListAdapter listAdapter;

	protected CategoryDao categoryDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list_activity);

		categoryDao = new CategoryDao(Database.getInstance(new FastBudgetMigration(this)));

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
		case (R.id.button_create_category):
			createCategory();
			return true;
		case (R.id.button_create_backup):
			createBackup();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		hideAllContextsRows();
	}

	public void addExpense(View view) {
		Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
		intent.putExtra(Extras.CategoryName.name(), (String) view.getTag());
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

	private void createCategory() {
		Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
		startActivityForResult(intent, RequestCode.CreateCategory.ordinal());
	}

	private void createBackup() {
		File externalFilesDir = getExternalFilesDir(null);
		if (externalFilesDir == null) {
			DialogHelper.createAlert(this, R.string.warning, R.string.warning_no_external_storage, R.string.ok);
		} else {
			String filename = getString(R.string.app_name) + ".backup";
			final File dest = new File(externalFilesDir, filename);
			Database.getInstance(this).ext().backupSync(dest.getAbsolutePath());
			DialogHelper.createQuestion(this, R.string.success, R.string.warning_backup_created, R.string.cancel,
				R.string.send_file, new Runnable() {
					@Override
					public void run() {
						String chooserTitle = getString(R.string.send_file);
						Intent intent = new Intent(ACTION_SEND);
						intent.setType(BACKUP_MIME_TYPE);
						intent.putExtra(EXTRA_STREAM, Uri.fromFile(dest));
						startActivity(createChooser(intent, chooserTitle));
					}
				}, dest.getAbsolutePath());
		}
	}

	public void updateView() {
		updateView(categoryDao.findAll(Category.class));
	}

	private void updateView(List<Category> categories) {
		listAdapter.update(categories);
		updateTitle(categories);
	}

	private void updateTitle(List<Category> categories) {
		int budgetCents = 0;
		int spentCents = 0;
		for (Category category : categories) {
			budgetCents += category.calculateBudget();
			spentCents += category.summarizeExpenses(DateUtils.createFirstDayOfMonth().getTime(), null);
		}

		String spent = formatAsIntegerCurrency(spentCents);
		String budget = formatAsIntegerCurrency(budgetCents);
		setTitle(format("%s  %s/%s", getString(R.string.app_name), spent, budget));
	}

	private void hideAllContextsRows() {
		((SlideExpandableListAdapter) getListAdapter()).collapseLastOpen();
	}

}
