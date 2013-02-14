package de.splitstudio.fastbudget3.db;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.ObjectListAdapter;

public class CategoryListAdapter extends ObjectListAdapter<Category> {

	public CategoryListAdapter(LayoutInflater layoutInflater, List<Category> objects) {
		super(layoutInflater, R.layout.category_row, objects);
	}

	@Override
	public void bindView(View view, Category object) {

	}

}
