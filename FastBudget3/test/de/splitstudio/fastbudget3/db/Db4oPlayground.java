package de.splitstudio.fastbudget3.db;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;

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
