package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import de.splitstudio.fastbudget3.CategoryActivity;

@RunWith(RobolectricTestRunner.class)
public class CategoryStorageTest {

	private static final int ANY_BUDGET = 123;
	private Context anyContext;
	private CategoryStorage categoryStorage;

	@Before
	public void setUp() {
		anyContext = new CategoryActivity();
		categoryStorage = CategoryStorage.getInstance(anyContext);
		categoryStorage.init(10000000);
	}

	@Test
	public void getInstance_createsAnInstanceOfCategoryStorage() {
		assertThat(categoryStorage, is(notNullValue()));
		assertThat(categoryStorage, is(CategoryStorage.class));
	}

	@Test
	public void getInstance_twoTimes_createsSameInstance() {
		CategoryStorage instance1 = CategoryStorage.getInstance(anyContext);
		CategoryStorage instance2 = CategoryStorage.getInstance(anyContext);
		assertThat(instance1, is(sameInstance(instance2)));
	}

	@Test
	public void savesItem() {
		categoryStorage.push(new Category("fdasfasd", ANY_BUDGET));
		assertThat(categoryStorage.currentSize(), is(1));
	}

}
