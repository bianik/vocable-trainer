package myProjects.vocableTrainer.repository.h2;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class H2VocableRepositoryTest {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";

	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "vocables";

	private static Connection conn;
	private static H2VocableRepository VocableRepo;

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
		VocableRepo = new H2VocableRepository(conn, TABLE_NAME);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindByPhraseNotFound() {
		assertThat(VocableRepo.findByPhrase("phrase")).isNull();
	}

	public void addTestVocable(String phrase, String translation, int falseTries, int corrTries) {
		executeDbCommand("INSERT INTO " + TABLE_NAME + " VALUES ('" + phrase + "', '" + translation + "', " + corrTries
				+ ", " + falseTries + ")");
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
