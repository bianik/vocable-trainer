package myProjects.vocableTrainer.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;

@RunWith(GUITestRunner.class) // takes screenshots in case of failure
public class SwingTrainerViewIT extends AssertJSwingJUnitTestCase {
	private static final String PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String OTHER_PHRASE = "phrase 2";
	private static final String OTHER_TRANSLATION = "translation 2";
	private static final int INITIAL_CORR_TRIES = 5;
	private static final int INITIAL_FALSE_TRIES = 3;

	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "VOCABLES";
	private static final String TCP_PORT = System.getProperty("tcpPort"); // get tcpPort from pom
	// JDBC driver name and database URL, use database server running in Docker
	// container
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:tcp://localhost:" + TCP_PORT + "/" + TABLE_NAME;
	private static Connection conn;

	private FrameFixture window; // interact with GUI-components
	private SwingTrainerView swingTrainerView; // call and test methods on this
	private VocableRepository vocableRepository;
	private TrainerController trainerController;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Override
	protected void onSetUp() {
		try {
			if (conn.isClosed())
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			vocableRepository = new H2VocableRepository(conn, TABLE_NAME);
			// always start with a new table using the repository
			vocableRepository.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		GuiActionRunner.execute(() -> {
			swingTrainerView = new SwingTrainerView();
			trainerController = new TrainerController(vocableRepository, swingTrainerView);
			swingTrainerView.setTrainerController(trainerController);
			return swingTrainerView;
		});
		window = new FrameFixture(robot(), swingTrainerView);
		window.show();
	}

	@Test
	@GUITest
	public void testAddButtonSuccess() {
		// setup
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.label("newVocableMessageLabel").requireText("Vocable added: " + PHRASE + " - " + TRANSLATION);
		window.label("newVocableMessageLabel").foreground().requireEqualTo(Color.BLACK);
	}
	
	@Test
	@GUITest
	public void testAddButtonError() throws SQLException {
		// setup
		vocableRepository.saveVocable(new Vocable(PHRASE, TRANSLATION));
		window.textBox("newPhraseTextBox").enterText(PHRASE);
		window.textBox("newTranslationTextBox").enterText(TRANSLATION);
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		window.label("newVocableMessageLabel").requireText("Vocable already exists: " + PHRASE + " - " + TRANSLATION);
		window.label("newVocableMessageLabel").foreground().requireEqualTo(Color.BLACK);
	}
	
	@Test
	@GUITest
	public void testNextButton() throws SQLException {
		// setup - execute on EDT
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		Vocable nextVocable = new Vocable(OTHER_PHRASE, OTHER_TRANSLATION);
		vocableRepository.saveVocable(currentVocable);
		vocableRepository.saveVocable(nextVocable);
		GuiActionRunner.execute(() -> {
			swingTrainerView.setCurrentVocable(currentVocable);
			swingTrainerView.btnNext.setEnabled(true);
		});
		// exercise
		window.button(JButtonMatcher.withText("Next")).click();
		// verify
		window.label("checkShowLabel").requireText(OTHER_TRANSLATION);
		window.textBox("checkEnterTextBox").requireEmpty();
		window.label("checkVocableMessageLabel").requireText(" ");
	}

}
