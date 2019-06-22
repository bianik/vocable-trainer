package myProjects.vocableTrainer.view.swing;

import static org.junit.Assert.*;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)	// takes screenshots in case of failure
public class SwingTrainerViewTest extends AssertJSwingJUnitTestCase {
	private FrameFixture window;					// interact with GUI-components
	private SwingTrainerView swingTrainerView;		// call and test methods on this

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
	public void test() {
		fail("Not yet implemented");
	}
}
