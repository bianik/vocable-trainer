package myProjects.vocableTrainer.controller;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;
import myProjects.vocableTrainer.view.TrainerView;

public class TrainerControllerIT {
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

	@Mock
	private TrainerView trainerView;

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

	@Before
	public void setUp() throws Exception {
		if (conn.isClosed())
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		// always start with new table
		executeDbCommand("DROP TABLE IF EXISTS " + TABLE_NAME);
		executeDbCommand("CREATE TABLE " + TABLE_NAME
				+ "(phrase VARCHAR(30), translation VARCHAR(30), corrTries INTEGER, falseTries INTEGER)");
		vocableRepository = new H2VocableRepository(conn, TABLE_NAME);
		trainerController = new TrainerController(vocableRepository, trainerView);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		// does test case work? 
	}

	////////////////// helping functions ////////////////////////////////

	private List<Vocable> readAllVocablesFromRepository() {
		String command = "SELECT * FROM " + TABLE_NAME;
		Statement stmt = null;
		ResultSet rs = null;
		List<Vocable> allVocables = new ArrayList<Vocable>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(command);
			// extract data from result set
			while (rs.next()) {
				Vocable v = new Vocable();
				v = new Vocable();
				// Retrieve by column name
				v.setPhrase(rs.getString("phrase"));
				v.setTranslation(rs.getString("translation"));
				v.setCorrTries(rs.getInt("corrTries"));
				v.setFalseTries(rs.getInt("falseTries"));
				allVocables.add(v);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
			}
		}
		return allVocables;
	}

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
			e.printStackTrace();
		}
	}
}
