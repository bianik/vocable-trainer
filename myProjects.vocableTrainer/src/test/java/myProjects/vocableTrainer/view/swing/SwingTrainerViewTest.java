package myProjects.vocableTrainer.view.swing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

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
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEitherPhraseOrTranslationAreBlankThenAddButtonShouldBeDisabled() {
		window.textBox("newPhraseTextBox").enterText(" ");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		// erase text in TextFields
		window.textBox("newPhraseTextBox").setText("");
		window.textBox("newTranslationTextBox").setText("");

		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenAddButtonIsClickedClearPhraseAndTranslation() {
		// setup
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.textBox("newPhraseTextBox").requireEmpty();
		window.textBox("newTranslationTextBox").requireEmpty();
	}

	@Test
	@GUITest
	public void testAddButtonShouldDelegateToTrainerControllerNewVocable() {
		// setup
		Vocable vocableToAdd = new Vocable("phrase 1", "translation 1");
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	@GUITest
	public void testAddButtonShouldDelegateToTrainerControllerNewVocableIgnoreLeadingOrTrailingWhiteSpace() {
		// setup
		Vocable vocableToAdd = new Vocable("phrase 1", "translation 1");
		window.textBox("newPhraseTextBox").enterText(" phrase 1  ");
		window.textBox("newTranslationTextBox").enterText("   translation 1   ");
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	@GUITest
	public void testAddButtonWhenClickedShouldEnableNextButton() {
		// setup
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
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
		Vocable currentVocable = new Vocable("phrase 1", "translation 1");
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
		window.textBox("checkEnterTextBox").enterText("phrase 1");
		window.button(JButtonMatcher.withText("Check")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testWhenEnterTextFieldIsNotEmptyAndCurrentVocableThenCheckButtonShouldBeEnabled() {
		Vocable currentVocable = new Vocable("phrase 1", "translation 1");
		GuiActionRunner.execute(() -> swingTrainerView.setCurrentVocable(currentVocable));
		window.textBox("checkEnterTextBox").enterText("phrase 1");
		window.button(JButtonMatcher.withText("Check")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEnterTextFieldBlankThenCheckButtonShouldDisabled() {
		window.textBox("checkEnterTextBox").enterText("  ");
		window.button(JButtonMatcher.withText("Check")).requireDisabled();
	}

	@Test
	@GUITest
	public void testCheckButtonShouldDelegateToTrainerControllerCheckOnGivenPhrase() {
		// setup - execute on EDT
		Vocable vocableToCheck = new Vocable("wrong phrase","translation 1");
		Vocable currentVocable = new Vocable("phrase 1", "translation 1");
		GuiActionRunner.execute(() -> swingTrainerView.setCurrentVocable(currentVocable));
		window.textBox("checkEnterTextBox").enterText("wrong phrase");
		// exercise
		window.button(JButtonMatcher.withText("Check")).click();
		// verify
		verify(trainerController).checkVocableOnGivenPhrase(vocableToCheck);
	}
}
