package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.StoredClass;

public class Db4oPlayground {

	private File file;

	@Before
	public void setUp() throws Exception {
		file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".db4o");
	}

	@After
	public void tearDown() {
		file.delete();
	}

	@Test
	public void simpleObjectAdded_increasesDbSize() {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file.getAbsolutePath());
		assertThat(db.query().execute().size(), is(0));
		db.store(new SimpleObject());
		assertThat(db.query().execute().size(), is(1));
		db.store(new SimpleObject());
		assertThat(db.query().execute().size(), is(2));
	}

	@Test
	public void advancedObjectAdded_increasesDbSizeTwice() throws Throwable {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file.getAbsolutePath());
		assertThat(db.query().execute().size(), is(0));
		AdvancedObject advancedObject = new AdvancedObject();
		db.store(advancedObject);
		assertThat(db.ext().isStored(advancedObject), is(true));
		assertThat(db.ext().isStored(advancedObject.simple), is(true));
	}

	@Test
	public void advancedadvancedObject() throws Throwable {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file.getAbsolutePath());
		assertThat(db.query().execute().size(), is(0));
		AdvancedAdvancedObject advancedObject = new AdvancedAdvancedObject();

		db.store(advancedObject);

		assertThat(db.ext().isStored(advancedObject), is(true));
		assertThat(db.ext().isStored(advancedObject.advancedObject), is(true));
		assertThat(db.ext().isStored(advancedObject.advancedObject.simple), is(true));
	}

	@Test
	public void objectWithList() {
		SimpleObject secondItem = new SimpleObject();
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file.getAbsolutePath());
		CollectionObject collectionObject = new CollectionObject();
		db.store(collectionObject);
		collectionObject.list.add(secondItem);
		assertThat(db.ext().isStored(collectionObject), is(true));
		assertThat(db.ext().isStored(collectionObject.list), is(true));
		assertThat(db.ext().isStored(collectionObject.list.get(0)), is(true));
		assertThat(db.ext().isStored(secondItem), is(false));

		db.store(collectionObject);
		db.store(collectionObject.list);
		assertThat(db.ext().isStored(secondItem), is(true));
	}

	@Test
	@Ignore
	public void treeset() throws Exception {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().activationDepth(Integer.MAX_VALUE);
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, "/home/mb/downloads/db");

		ObjectSet<TreeMap> treeSets = db.query(TreeMap.class);
		ObjectSet<Category> categories = db.query(Category.class);
		for (TreeMap map : treeSets) {
			System.out.println(map);
		}
		for (Category category : categories) {
			System.out.println(category);
		}
	}

	@Test
	@Ignore
	public void renameCategor2ToCategory() throws Exception {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile("/home/mb/downloads/db2");

		ObjectSet<Category> categories = db.query(Category.class);
		for (StoredClass storedClass : db.ext().storedClasses()) {
			if (storedClass.getName().endsWith("Category2")) {
				storedClass.rename("de.splitstudio.fastbudget3.db.Category");
			}
		}
		db.close();

		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//		config.common().objectClass("de.splitstudio.fastbudget3.db.Category2")
//				.rename("de.splitstudio.fastbudget3.db.Category");
		db = Db4oEmbedded.openFile(config, "/home/mb/downloads/db2");
		categories = db.query(Category.class);
		System.out.println("found " + categories);
		db.close();
	}

//	@Test
//	public void loadDbBackup() throws Exception {
//		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//		config.common().activationDepth(1);
//		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, "/home/mb/downloads/db");
//
//		List<Category> categories = new ArrayList<Category>(db.query(new Predicate<Category>() {
//			@Override
//			public boolean match(Category category) {
//				return !category.name.equals("Haushalt");
//			}
//		}));
//		Category haushaltCategory = db.query(new Predicate<Category>() {
//			@Override
//			public boolean match(Category category) {
//				return category.name.equals("Haushalt");
//			}
//		}).next();
//
//		Set<Expense> haushaltExpenses = getHaushaltExpenses(db, categories);
//		TreeSet<Expense> allExpenses = getAllExpenses(db);
//
//		haushaltCategory.expenses = haushaltExpenses;
//		categories.add(haushaltCategory);
//		db.close();
//		db = Db4oEmbedded.openFile(Database.createConfig(), "/home/mb/downloads/db2");
//		for (Object object : db.query().execute()) {
//			db.delete(object);
//		}
//		for (Category category : categories) {
//			Category2 category2 = new Category2(category.name, category.budget, category.date);
//			category2.uuid = category.uuid;
//			category2.expenses = new ArrayList<Expense>(category.expenses);
//			db.store(category2);
//		}
//		for (Expense expense : allExpenses) {
//			assertThat(db.ext().isStored(expense), is(true));
//		}
//	}

	@SuppressWarnings("unused")
	private Set<Expense> getHaushaltExpenses(EmbeddedObjectContainer db, List<Category> categories) {
		Set<Expense> haushaltExpenses = getAllExpenses(db);
		List<Expense> expensesInCategories = new ArrayList<Expense>();
		for (Category category : categories) {
			db.activate(category, Integer.MAX_VALUE);
			expensesInCategories.addAll(category.expenses);
		}
		haushaltExpenses.removeAll(expensesInCategories);
		return haushaltExpenses;
	}

	private TreeSet<Expense> getAllExpenses(EmbeddedObjectContainer db) {
		return new TreeSet<Expense>(db.query(Expense.class));
	}

	private static class SimpleObject {
		@SuppressWarnings("unused")
		int bla = 1;
	}

	private static class AdvancedObject {
		SimpleObject simple = new SimpleObject();
	}

	private static class AdvancedAdvancedObject {
		AdvancedObject advancedObject = new AdvancedObject();
	}

	private static class CollectionObject {
		List<SimpleObject> list = new ArrayList<SimpleObject>();

		public CollectionObject() {
			list.add(new SimpleObject());
		}
	}

}
