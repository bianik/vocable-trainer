package myProjects.vocableTrainer.view.swing;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;

@RunWith(GUITestRunner.class) // takes screenshots in case of failure
public class SwingTrainerViewIT extends AssertJSwingJUnitTestCase {
	private static final String CORRECT_PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String GIVEN_INCORRECT_PHRASE = "wrong phrase";
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

	@Test @GUITest
	public void test() {
		// will the test case run?
	}

}
