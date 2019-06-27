package myProjects.vocableTrainer.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import myProjects.vocableTrainer.model.Vocable;

public class VocableTrainerAppConsoleE2E {
	private static final String PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String OTHER_PHRASE = "phrase 2";
	private static final String OTHER_TRANSLATION = "translation 2";
	private static final int INITIAL_CORR_TRIES = 5;
	private static final int INITIAL_FALSE_TRIES = 3;
	private static final String NL = System.getProperty("line.separator");
	// ANSI escape codes for colors
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";

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

	private VocableTrainerApp app;
	private Thread consoleThread;
	private ByteArrayOutputStream outputBuffer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Before
	public void setUp() throws Exception {
		// always start with new table
		executeDbCommand("DROP TABLE IF EXISTS " + TABLE_NAME);
		executeDbCommand("CREATE TABLE " + TABLE_NAME
				+ "(phrase VARCHAR(30), translation VARCHAR(30), corrTries INTEGER, falseTries INTEGER)");
		// add a vocable to the test fixture
		addTestVocable(PHRASE, TRANSLATION, INITIAL_FALSE_TRIES, INITIAL_CORR_TRIES);
	}

	@After
	public void tearDown() throws Exception {
		app.stop();
		outputBuffer.close();
	}

	@Test
	public void testStartUp() {
		// setup & exercise
		createConsoleAppWithUserInput("");
		// verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(outputBuffer.toString().contains("##### Vocable Trainer #####")));
	}
	
	@Test
	public void testNewVocableSuccess() {
		// setup & exercise
		String userInput = "new" + NL + OTHER_PHRASE + NL + OTHER_TRANSLATION + NL;
		createConsoleAppWithUserInput(userInput);
		// verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(outputBuffer.toString().contains("Vocable added: " + PHRASE + " - " + TRANSLATION)));
	}
	
	@Test
	public void testNewVocableError() {
		// setup & exercise
		String userInput = "new" + NL + PHRASE + NL + TRANSLATION + NL;
		createConsoleAppWithUserInput(userInput);
		// verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(outputBuffer.toString().contains("Vocable already exists: " + PHRASE + " - " + TRANSLATION)));
	}

	////////////////// helping functions ////////////////////////////////

	private Vocable addTestVocable(String phrase, String translation, int falseTries, int corrTries) {
		executeDbCommand("INSERT INTO " + TABLE_NAME + " VALUES ('" + phrase + "', '" + translation + "', " + corrTries
				+ ", " + falseTries + ")");
		Vocable v = new Vocable(phrase, translation);
		v.setCorrTries(corrTries);
		v.setFalseTries(falseTries);
		return v;
	}

	private void executeDbCommand(String command) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(command);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createConsoleAppWithUserInput(String userInput) {
		Scanner scanner = new Scanner(userInput);
		app = new VocableTrainerApp();
		outputBuffer = app.setIOArgs(scanner, "", TCP_PORT, "", false, true);
		consoleThread = new Thread(app);
		consoleThread.start();
	}
}
