package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;
import android.content.Context;

import com.db4o.ObjectContainer;

@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {

	private static final Context ANY_CONTEXT = new Activity();
	private ObjectContainer db;

	@Before
	public void setUp() {
		db = Database.getInstance(ANY_CONTEXT);
		Database.clear();
	}

	@Test
	public void isASingleton() {
		assertThat(db, is(notNullValue()));
		assertThat(db, is(sameInstance(Database.getInstance(ANY_CONTEXT))));
	}

	@Test
	public void clear_hasNoObjects() {
		Object object = new Category("foo");
		db.store(object);
		Database.clear();
		assertThat(db.query().execute(), is(empty()));
	}

	@Test
	public void changingAnObject_store_stillOneObjectInDb() throws Throwable {
		db.store(new Category("foo"));
		Category category = db.query(Category.class).get(0);

		category.name = "bar";
//		db.store(category);
		category.finalize();

		assertThat(db.query(Category.class), hasSize(1));
		assertThat(db.query(Category.class).get(0).name, is("bar"));
	}

}
