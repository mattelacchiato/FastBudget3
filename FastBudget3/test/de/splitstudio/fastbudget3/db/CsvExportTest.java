package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.DateUtils.onGermanDate;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

public class CsvExportTest {

	private Category category;
	private Expense firstExpense;
	private CsvExport csvExport;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		firstExpense = new Expense(123, onGermanDate("27.2.2014"), "description");
		category = new Category("category name");
		category.add(firstExpense);
		csvExport = new CsvExport(Lists.newArrayList(category));
	}

	@Test
	public void getContent_emptyList_emptyString() {
		csvExport.categories.clear();
		String content = csvExport.getContent();
		assertThat(content).isEqualTo("");
	}

	@Test
	public void getContent_oneExpense_containsDate() {
		String content = csvExport.getContent();
		assertThat(content).contains("2014-02-27");
	}

	@Test
	public void getContent_oneExpense_containsDescription() {
		String content = csvExport.getContent();
		assertThat(content).contains("description");
	}

	@Test
	public void getContent_oneExpense_containsCategoryName() {
		String content = csvExport.getContent();
		assertThat(content).contains("category name");
	}

	@Test
	public void getContent_oneExpense_containsAmount() {
		String content = csvExport.getContent();
		assertThat(content).contains("$1.23");
	}

	@Test
	public void getContent_oneExpense_seperatedByTab() {
		String content = csvExport.getContent();
		assertThat(content.split("\\t")).hasSize(4);
	}

	@Test
	public void getContent_oneExpense_hasCRLFatTheAnd() {
		String content = csvExport.getContent();
		assertThat(content).endsWith("\r\n");
	}

	@Test
	public void getContent_oneExpense_correctOrder() {
		String content = csvExport.getContent();
		assertThat(content.split("\\t")).containsExactly("2014-02-27", "category name", "$1.23", "description\r\n");
	}

	@Test
	public void getContent_nullDescription_emptyString() {
		firstExpense.description = null;
		String content = csvExport.getContent();
		assertThat(content.split("\\t")[3]).isEqualTo("\r\n");
	}

	@Test
	public void getContent_nullDescription_sameColumnSize() {
		firstExpense.description = null;
		String content = csvExport.getContent();
		assertThat(content.split("\\t")).hasSize(4);
	}

	@Test
	public void getContent_tabInDescription_escaped() {
		firstExpense.description = "\t";
		String content = csvExport.getContent();
		assertThat(content).contains("\"\t\"");
	}

}
