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

	private Context anyContext;

	@Before
	public void setUp() {
		anyContext = new CategoryActivity();
	}

	@Test
	public void getInstance_createsAnInstanceOfCategoryStorage() {
		CategoryStorage instance = CategoryStorage.getInstance(anyContext);
		assertThat(instance, is(notNullValue()));
		assertThat(instance, is(CategoryStorage.class));
	}

	@Test
	public void getInstance_twoTimes_createsSameInstance() {
		CategoryStorage instance1 = CategoryStorage.getInstance(anyContext);
		CategoryStorage instance2 = CategoryStorage.getInstance(anyContext);
		assertThat(instance1, is(sameInstance(instance2)));
	}

}
