package de.splitstudio.fastbudget3;

import java.text.ParseException;

import com.db4o.ObjectContainer;

import de.splitstudio.fastbudget3.db.Category;
import de.splitstudio.utils.NumberUtils;

public class CategoryValidator {

	public enum CategoryValidationResult {
		//@formatter:off
		Ok           (android.R.string.ok), 
		Duplicate    (R.string.error_name_duplicated), 
		Empty        (R.string.error_name_empty),
		InvalidNumber(R.string.error_invalid_number),
		;
		//@formatter:on

		public final int stringId;

		private CategoryValidationResult(int stringId) {
			this.stringId = stringId;

		}
	}

	private final ObjectContainer db;

	private final String name;
	private final String amount;

	private int amountInCent;

	private final CategoryValidationResult result;

	public CategoryValidator(ObjectContainer db, String name, String amount) {
		this.db = db;
		this.name = name;
		this.amount = amount;
		this.result = validate();
	}

	private CategoryValidationResult validate() {
		if (isNameEmpty()) {
			return CategoryValidationResult.Empty;
		}
		if (isCategoryNameDuplicated()) {
			return CategoryValidationResult.Duplicate;
		}
		if (isAmountNotANumber()) {
			return CategoryValidationResult.InvalidNumber;
		}
		return CategoryValidationResult.Ok;
	}

	private boolean isAmountNotANumber() {
		try {
			amountInCent = NumberUtils.parseCent(amount);
			return false;
		} catch (ParseException e) {
			return true;
		}
	}

	public String getName() {
		return name;
	}

	public CategoryValidationResult getResult() {
		return result;
	}

	private boolean isNameEmpty() {
		return name.trim().isEmpty();
	}

	private boolean isCategoryNameDuplicated() {
		return !db.queryByExample(new Category(name)).isEmpty();
	}

	public int getAmountInCent() {
		return amountInCent;
	}

}