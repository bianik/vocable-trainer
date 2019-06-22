package myProjects.vocableTrainer.view.swing;

import static org.junit.Assert.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class) // takes screenshots in case of failure
public class SwingTrainerViewTest extends AssertJSwingJUnitTestCase {
	private FrameFixture window; // interact with GUI-components
	private SwingTrainerView swingTrainerView; // call and test methods on this

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			swingTrainerView = new SwingTrainerView();
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
	
	@Test @GUITest
	public void testWhenPhraseAndTranslationAreNotEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	@Test @GUITest
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
	
	@Test @GUITest
	public void testWhenAddButtonIsClickedClearPhraseAndTranslation() {
		// setup
		window.textBox("newPhraseTextBox").enterText("phrase 1");
		window.textBox("newTranslationTextBox").enterText("translation 1");
		// execute
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.textBox("newPhraseTextBox").requireEmpty();
		window.textBox("newTranslationTextBox").requireEmpty();
	}
}
