package myProjects.vocableTrainer.view.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;

public class ConsoleTrainerViewIT {
	private static final String PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String OTHER_PHRASE = "phrase 2";
	private static final String OTHER_TRANSLATION = "translation 2";
	private static final int INITIAL_CORR_TRIES = 5;
	private static final int INITIAL_FALSE_TRIES = 3;
	private static final String NL = System.getProperty("line.separator");
	private static final String DATABASE_ERROR = "Database error!";
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

	private ConsoleTrainerView consoleTrainerView;
	private VocableRepository vocableRepository;
	private TrainerController trainerController;

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
	public void setUp() {
		try {
			if (conn.isClosed())
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			vocableRepository = new H2VocableRepository(conn, TABLE_NAME);
			// always start with a new table using the repository
			vocableRepository.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNewVocableSuccess() {
		// setup
		String userInput = "new" + NL + PHRASE + NL + TRANSLATION + NL;
		createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		consoleTrainerView.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[5]).isEqualTo("Vocable added: " + PHRASE + " - " + TRANSLATION);
	}
	
	@Test
	public void testNewVocableError() throws SQLException {
		// setup
		vocableRepository.saveVocable(new Vocable(PHRASE, TRANSLATION));
		String userInput = "new" + NL + PHRASE + NL + TRANSLATION + NL;
		createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		consoleTrainerView.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[5]).isEqualTo("Vocable already exists: " + PHRASE + " - " + TRANSLATION);
	}

	//////////////// helping method ////////////////////
	private void createConsoleTrainerViewWithUserInput(String userInput) {
		Scanner scanner = new Scanner(userInput);
		outputBuffer = new ByteArrayOutputStream();
		PrintStream outPrinter = new PrintStream(outputBuffer);
		consoleTrainerView = new ConsoleTrainerView(scanner, outPrinter);
		trainerController = new TrainerController(vocableRepository, consoleTrainerView);
		consoleTrainerView.setTrainerContr(trainerController);
	}
}
