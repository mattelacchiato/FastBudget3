package de.splitstudio.fastbudget3;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.splitstudio.utils.view.Calculator;

@RunWith(RobolectricTestRunner.class)
public class CategoryActivityTest {

	private CategoryActivity activity;

	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		this.activity = new CategoryActivity();
		activity.onCreate(null);
	}

	@Test
	public void itHasAFieldToEnterTheCategoryName() throws Exception {
		View nameTextEdit = activity.findViewById(R.id.category_name);
		assertThat(nameTextEdit, is(notNullValue()));
		assertThat(nameTextEdit, is(EditText.class));
	}

	@Test
	public void categoryNameHasFocus() throws Exception {
		EditText nameEdit = (EditText) activity.findViewById(R.id.category_name);
		assertThat(nameEdit.hasFocus(), is(true));
	}

	@Test
	public void categoryNameHasAnHint() throws Exception {
		EditText nameEdit = (EditText) activity.findViewById(R.id.category_name);
		String expectedHint = activity.getString(R.string.hint_category_name);
		assertThat(nameEdit.getHint().toString(), is(expectedHint));
	}

	@Test
	public void titleIsSet() throws Exception {
		String expectedTitle = activity.getString(R.string.add_category);
		assertThat(activity.getTitle().toString(), is(expectedTitle));
	}

	@Test
	public void ithasACalculator() {
		View calculator = activity.findViewById(R.id.calculator);
		assertThat(calculator, is(notNullValue()));
		assertThat(calculator, is(Calculator.class));
	}

	@Test
	public void itHasASaveButton() {
		Button button = (Button) activity.findViewById(R.id.button_save);
		assertThat(button, is(notNullValue()));
	}

	@Test
	public void itHasACancelButton() {
		Button button = (Button) activity.findViewById(R.id.button_cancel);
		assertThat(button, is(notNullValue()));
	}
}
