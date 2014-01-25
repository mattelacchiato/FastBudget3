package de.splitstudio.fastbudget3;

import static de.splitstudio.fastbudget3.enums.Extras.CategoryName;
import static de.splitstudio.fastbudget3.enums.Extras.Uuid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.fastbudget3.db.CategoryDao;
import de.splitstudio.utils.db.Database;
import de.splitstudio.utils.db.GenericBaseDao;

public class CategoryListDialog extends DialogFragment {

	interface CategoryListDialogListener {
		public void onDone();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.title_category_chooser);
		final CategoryDao categoryDao = new CategoryDao(Database.getInstance(getActivity()));
		final CharSequence[] categoryNames = getCategoryNames(categoryDao);
		builder.setItems(categoryNames, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newCategoryName = categoryNames[which].toString();
				String oldCategoryName = getArguments().getString(CategoryName.name());
				String expenseUuid = getArguments().getString(Uuid.name());
				categoryDao.moveExpense(expenseUuid, oldCategoryName, newCategoryName);
				getListener().onDone();
			}
		});
		return builder.create();
	}

	protected CategoryListDialogListener getListener() {
		return (CategoryListDialogListener) getActivity();
	}

	//TODO (Jan 25, 2014): move to dao
	private CharSequence[] getCategoryNames(GenericBaseDao<Category> categoryDao) {
		List<Category> categories = categoryDao.findAll(Category.class);
		List<String> names = new ArrayList<String>(categories.size());
		for (Category category : categories) {
			names.add(category.name);
		}
		Collections.sort(names);
		return names.toArray(new CharSequence[categories.size()]);
	}
}
