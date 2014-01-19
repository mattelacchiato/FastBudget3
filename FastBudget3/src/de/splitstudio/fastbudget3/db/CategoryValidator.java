package de.splitstudio.fastbudget3.db;

import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.Duplicate;
import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.Empty;
import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.InvalidNumber;
import static de.splitstudio.fastbudget3.db.CategoryValidator.CategoryValidationResult.Ok;

import java.text.ParseException;

import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.NumberUtils;

//TODO (Jan 18, 2014): find a more concise way to validate user inputs
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

	private final CategoryDao categoryDao;

	private final String name;
	private final String amount;

	private int amountInCent;

	private final CategoryValidationResult result;

	public CategoryValidator(CategoryDao categoryDao, String name, String amount) {
		this.categoryDao = categoryDao;
		this.name = name;
		this.amount = amount;
		this.result = validate();
	}

	private CategoryValidationResult validate() {
		if (isNameEmpty()) {
			return Empty;
		}
		if (isAmountNotANumber()) {
			return InvalidNumber;
		}
		if (isCategoryNameDuplicated()) {
			return Duplicate;
		}
		return Ok;
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
		return categoryDao.findByName(name) != null;
	}

	public int getAmountInCent() {
		return amountInCent;
	}

}
