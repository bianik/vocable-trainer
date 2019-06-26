package myProjects.vocableTrainer.app;

import static org.junit.Assert.*;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import myProjects.vocableTrainer.model.Vocable;

@RunWith(GUITestRunner.class)
public class VocableTrainerAppSwingE2E extends AssertJSwingJUnitTestCase {
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

	private FrameFixture window;

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
		// always start with new table
		executeDbCommand("DROP TABLE IF EXISTS " + TABLE_NAME);
		executeDbCommand("CREATE TABLE " + TABLE_NAME
				+ "(phrase VARCHAR(30), translation VARCHAR(30), corrTries INTEGER, falseTries INTEGER)");
		// add two vocables to the test fixture
		addTestVocable(PHRASE, TRANSLATION, INITIAL_FALSE_TRIES, INITIAL_CORR_TRIES);
		addTestVocable(OTHER_PHRASE, OTHER_TRANSLATION, 0, 0);
		// start the Swing application
		application("myProjects.vocableTrainer.app.VocableTrainerApp").start();
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Vocable Trainer".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Test
	public void test() {
		// will this test case run?
	}

	////////////////// helping functions ////////////////////////////////

	public Vocable addTestVocable(String phrase, String translation, int falseTries, int corrTries) {
		executeDbCommand("INSERT INTO " + TABLE_NAME + " VALUES ('" + phrase + "', '" + translation + "', " + corrTries
				+ ", " + falseTries + ")");
		Vocable v = new Vocable(phrase, translation);
		v.setCorrTries(corrTries);
		v.setFalseTries(falseTries);
		return v;
	}

	public void executeDbCommand(String command) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(command);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
