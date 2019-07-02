package myProjects.vocableTrainer.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.awt.Color;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.model.Vocable;

@RunWith(GUITestRunner.class) // takes screenshots in case of failure
public class SwingTrainerViewTest extends AssertJSwingJUnitTestCase {
	// constants for testing
	private static final String SOME_TEXT = "some text";
	private static final String INCORRECT = "incorrect!";
	private static final String CORRECT = "correct!";
	private static final String DATABASE_ERROR = "Database error!";
	private static final String WRONG_PHRASE = "wrong phrase";
	private static final String TRANSLATION = "translation 1";
	private static final String PHRASE = "phrase 1";

	private FrameFixture window; // interact with GUI-components
	private SwingTrainerView swingTrainerView; // call and test methods on this
	@Mock
	private TrainerController trainerController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			swingTrainerView = new SwingTrainerView();
			swingTrainerView.setTrainerController(trainerController);
			return swingTrainerView;
		});
		window = new FrameFixture(robot(), swingTrainerView);
		window.show();
	}

////////////////// unit tests for GUI- controls and GUI's logic /////////////
	@Test
	@GUITest
	public void testControlsInitialState() {
		window.requireTitle("Vocable Trainer");
		// components in upper panel 'new vocable'
		window.panel("newVocable");
		window.label("newPhraseLabel");
		window.label("newTranslationLabel");
		window.textBox("newPhraseTextBox").requireEditable().requireEmpty();
		window.textBox("newTranslationTextBox").requireEditable().requireEmpty();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.label("newVocableMessageLabel").requireText(" ");
		// components in upper panel 'check vocable'
		window.panel("checkVocable");
		window.label("checkShowTitleLabel");
		window.label("checkShowLabel");
		window.label("checkEnterLabel");
		window.textBox("checkEnterTextBox").requireEditable().requireEmpty();
		window.button(JButtonMatcher.withText("Check")).requireDisabled();
		window.button(JButtonMatcher.withText("Next")).requireDisabled();
		window.label("checkVocableMessageLabel").requireText(" ");
	}

	@Test
	@GUITest
	public void testWhenPhraseAndTranslationAreNotEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEitherPhraseOrTranslationAreBlankThenAddButtonShouldBeDisabled() {
		window.textBox("newPhraseTextBox").enterText(" ");
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		// erase text in TextFields
		window.textBox("newPhraseTextBox").setText("");
		window.textBox("newTranslationTextBox").setText("");

		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenAddButtonIsClickedClearPhraseAndTranslationAndDisableAddButton() {
		// setup
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.textBox("newPhraseTextBox").requireEmpty();
		window.textBox("newTranslationTextBox").requireEmpty();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	@GUITest
	public void testAddButtonShouldDelegateToTrainerControllerNewVocable() {
		// setup
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	@GUITest
	public void testAddButtonShouldDelegateToTrainerControllerNewVocableIgnoreLeadingOrTrailingWhiteSpace() {
		// setup
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		window.textBox("newPhraseTextBox").enterText(" " + PHRASE + "  ");
		window.textBox("newTranslationTextBox").enterText("   " + TRANSLATION + "   ");
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	@GUITest
	public void testAddButtonWhenClickedShouldEnableNextButton() {
		// setup
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.button(JButtonMatcher.withText("Next")).requireEnabled();
	}

	@Test
	@GUITest
	public void testNextButtonShouldDelegateToTrainerControllerNextVocableWhenNoCurrentVocable() {
		// setup - execute on EDT
		GuiActionRunner.execute(() -> swingTrainerView.btnNext.setEnabled(true));
		// exercise
		window.button(JButtonMatcher.withText("Next")).click();
		// verify
		verify(trainerController).nextVocable(null);
	}

	@Test
	@GUITest
	public void testNextButtonShouldDelegateToTrainerControllerNextVocableWhenCurrentVocable() {
		// setup - execute on EDT
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		GuiActionRunner.execute(() -> {
			swingTrainerView.setCurrentVocable(currentVocable);
			swingTrainerView.btnNext.setEnabled(true);
		});
		// exercise
		window.button(JButtonMatcher.withText("Next")).click();
		// verify
		verify(trainerController).nextVocable(currentVocable);
	}

	@Test
	@GUITest
	public void testWhenEnterTextFieldIsNotEmptyAndNoCurrentVocableThenCheckButtonShouldBeDisabled() {
		window.textBox("checkEnterTextBox").enterText(PHRASE);
		window.button(JButtonMatcher.withText("Check")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenEnterTextFieldIsNotEmptyAndCurrentVocableThenCheckButtonShouldBeEnabled() {
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		GuiActionRunner.execute(() -> swingTrainerView.setCurrentVocable(currentVocable));
		window.textBox("checkEnterTextBox").enterText(PHRASE);
		window.button(JButtonMatcher.withText("Check")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEnterTextFieldBlankThenCheckButtonShouldBeDisabled() {
		window.textBox("checkEnterTextBox").enterText("  ");
		window.button(JButtonMatcher.withText("Check")).requireDisabled();
	}

	@Test
	@GUITest
	public void testCheckButtonShouldDelegateToTrainerControllerCheckOnGivenPhrase() {
		// setup - execute on EDT
		Vocable vocableToCheck = new Vocable(WRONG_PHRASE, TRANSLATION);
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		GuiActionRunner.execute(() -> swingTrainerView.setCurrentVocable(currentVocable));
		window.textBox("checkEnterTextBox").enterText(WRONG_PHRASE);
		// exercise
		window.button(JButtonMatcher.withText("Check")).click();
		// verify
		verify(trainerController).checkVocableOnGivenPhrase(vocableToCheck);
	}

	@Test
	@GUITest
	public void testCheckButtonShouldDelegateToTrainerControllerCheckOnGivenPhraseIgnoreLeadingOrTrailingWhiteSpace() {
		// setup - execute on EDT
		Vocable vocableToCheck = new Vocable(WRONG_PHRASE, TRANSLATION);
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		GuiActionRunner.execute(() -> swingTrainerView.setCurrentVocable(currentVocable));
		window.textBox("checkEnterTextBox").enterText("   " + WRONG_PHRASE + "  ");
		// exercise
		window.button(JButtonMatcher.withText("Check")).click();
		// verify
		verify(trainerController).checkVocableOnGivenPhrase(vocableToCheck);
	}

////////////////// unit tests for TrainerView-interface implementation/////////////
	@Test
	@GUITest
	public void testShowMessageVocableAdded() {
		// setup
		GuiActionRunner.execute(() -> swingTrainerView.lblAddMessage.setForeground(Color.RED));
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		// exercise
		GuiActionRunner.execute(() -> swingTrainerView.showMessageVocableAdded("Vocable added: ", vocableToAdd));
		// verify
		window.label("newVocableMessageLabel").requireText("Vocable added: " + PHRASE + " - " + TRANSLATION);
		window.label("newVocableMessageLabel").foreground().requireEqualTo(Color.BLACK);
	}

	@Test
	@GUITest
	public void testShowMessageVocableAddedDbError() {
		// exercise
		GuiActionRunner.execute(() -> swingTrainerView.showMessageVocableAdded(DATABASE_ERROR, null));
		// verify
		window.label("newVocableMessageLabel").requireText(DATABASE_ERROR);
		window.label("newVocableMessageLabel").foreground().requireEqualTo(Color.RED);
	}

	@Test
	@GUITest
	public void testShowCheckResultWhenCorrect() {
		GuiActionRunner.execute(() -> swingTrainerView.showCheckResult(CORRECT, true));
		// verify
		window.label("checkVocableMessageLabel").requireText(CORRECT);
		window.label("checkVocableMessageLabel").foreground().requireEqualTo(Color.GREEN);
		window.textBox("checkEnterTextBox").background().requireEqualTo(Color.GREEN);
	}

	@Test
	@GUITest
	public void testShowCheckResultWhenIncorrect() {
		GuiActionRunner.execute(() -> swingTrainerView.showCheckResult(INCORRECT, false));
		// verify
		window.label("checkVocableMessageLabel").requireText(INCORRECT);
		window.label("checkVocableMessageLabel").foreground().requireEqualTo(Color.RED);
		window.textBox("checkEnterTextBox").background().requireEqualTo(Color.RED);
	}

	@Test
	@GUITest
	public void testShowNextVocableWhenNextVocable() {
		// setup
		GuiActionRunner.execute(() -> {
			swingTrainerView.lblCheckMessage.setText(SOME_TEXT);
			swingTrainerView.enterTextField.setText(SOME_TEXT);
			swingTrainerView.enterTextField.setBackground(Color.GREEN);
		});
		Vocable nextVocable = new Vocable(PHRASE, TRANSLATION);
		// exercise
		GuiActionRunner.execute(() -> swingTrainerView.showNextVocable("", nextVocable));
		// verify
		window.label("checkShowLabel").requireText(TRANSLATION);
		window.textBox("checkEnterTextBox").requireEmpty();
		window.label("checkVocableMessageLabel").requireText(" ");
		assertThat(swingTrainerView.getCurrentVocable()).isEqualTo(nextVocable);
		window.textBox("checkEnterTextBox").background().requireEqualTo(Color.WHITE);
	}

	@Test
	@GUITest
	public void testShowNextVocableWheNonNextVocable() {
		// setup
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		GuiActionRunner.execute(() -> {
			swingTrainerView.lblShow.setText(SOME_TEXT);
			swingTrainerView.enterTextField.setText(SOME_TEXT);
			swingTrainerView.setCurrentVocable(currentVocable);
			swingTrainerView.enterTextField.setBackground(Color.GREEN);
		});
		// exercise
		GuiActionRunner.execute(() -> swingTrainerView.showNextVocable(DATABASE_ERROR, null));
		// verify
		window.label("checkShowLabel").requireText(SOME_TEXT);
		window.textBox("checkEnterTextBox").requireText(SOME_TEXT);
		window.label("checkVocableMessageLabel").requireText(DATABASE_ERROR);
		assertThat(swingTrainerView.getCurrentVocable()).isEqualTo(currentVocable);
		window.textBox("checkEnterTextBox").background().requireEqualTo(Color.GREEN);
	}
}
