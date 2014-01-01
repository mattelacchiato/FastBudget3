package de.splitstudio.fastbudget3.db;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

import de.splitstudio.utils.db.Database;

public class CategoryDaoTest {

	private static final int ANY_AMOUNT = 0;
	private static final Date ANY_DATE = new Date();
	private static final String ANY_NAME = "foo";

	private CategoryDao categoryDao;
	private ObjectContainer db;

	@Before
	public void setUp() throws Exception {
		db = Db4oEmbedded.openFile(Database.createConfig(), File.createTempFile(UUID.randomUUID().toString(), "")
				.getAbsolutePath());
		categoryDao = new CategoryDao(db);
	}

}
