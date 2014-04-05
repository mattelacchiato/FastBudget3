package de.splitstudio.fastbudget3.db;

import static de.splitstudio.utils.NumberUtils.formatAsCurrency;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CsvExport {

	private static final char ESCAPE = '"';

	private static final String SEPERATOR = "\t";

	final List<Category> categories;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private static final String END_OF_LINE = "\r\n";

	public CsvExport(List<Category> categories) {
		this.categories = categories;
	}

	public String getContent() {
		StringBuilder content = new StringBuilder();
		for (Category category : categories) {
			for (Expense expense : category.getExpenses()) {
				content.append(DATE_FORMAT.format(expense.date));
				content.append(SEPERATOR);
				content.append(category.name);
				content.append(SEPERATOR);
				content.append(formatAsCurrency(expense.amount));
				content.append(SEPERATOR);
				escape(content, expense.description);
				content.append(END_OF_LINE);
			}
		}
		return content.toString();
	}

	private void escape(StringBuilder stringBuilder, String value) {
		if (value == null) {
			return;
		}
		boolean mustEscape = value.contains(SEPERATOR);
		if (mustEscape) {
			stringBuilder.append(ESCAPE);
		}
		stringBuilder.append(value);
		if (mustEscape) {
			stringBuilder.append(ESCAPE);
		}
	}
}
