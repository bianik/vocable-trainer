package myProjects.vocableTrainer.repository.h2;

import static org.assertj.core.api.Assertions.*;

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

import myProjects.vocableTrainer.model.Vocable;

public class H2VocableRepositoryTest {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";

	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "vocables";

	private static Connection conn;
	private static H2VocableRepository vocableRepo;

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
		vocableRepo = new H2VocableRepository(conn, TABLE_NAME);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindByPhraseNotFound() {
		try {
			assertThat(vocableRepo.findByPhrase("phrase")).isNull();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testFindByPhraseFound() {
		// setup
		addTestVocable("an other phrase", "translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		// execution
		Vocable retreivedVocable = null;
		try {
			retreivedVocable = vocableRepo.findByPhrase("phrase 1");
		} catch (SQLException e) {
		}
		// verify
		assertThat(retreivedVocable).isEqualTo(dbVocable);
	}

	@Test
	public void testFindByPhraseDbErrorShouldThrow() {
		// setup
		try {
			conn.close();
		} catch (SQLException e) {
		}
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.findByPhrase("phrase")).isInstanceOf(SQLException.class);
	}

	@Test
	public void testFindByTranslationNotFound() {
		assertThat(vocableRepo.findByTranslation("translation")).isNull();
	}

	@Test
	public void testFindByTranslationFound() {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		// execution
		Vocable retreivedVocable = null;
		retreivedVocable = vocableRepo.findByTranslation("translation 1");
		// verify
		assertThat(retreivedVocable).isEqualTo(dbVocable);
	}

	@Test
	public void testSaveVocable() {
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		// execution
		vocableRepo.saveVocable(vocable);
		// verify
		assertThat(readAllVocablesFromRepository()).containsExactly(vocable);
	}

	@Test
	public void testUpdateVocable() {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		dbVocable.setCorrTries(6);
		dbVocable.setFalseTries(3);
		// execution
		vocableRepo.updateVocable(dbVocable);
		// verify
		assertThat(readAllVocablesFromRepository()).contains(dbVocable);
	}

	@Test
	public void testNextVocableWhenNoCurrentVocable() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		// execution
		Vocable nextVocable = vocableRepo.nextVocable(null);
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
	}

	@Test
	public void testNextVocableWhenCurrentVocable() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		Vocable secondVocable = addTestVocable("phrase 2", "translation 2", 0, 0);
		// execution
		Vocable nextVocable = vocableRepo.nextVocable(firstVocable);
		// verify
		assertThat(nextVocable).isEqualTo(secondVocable);
	}

	@Test
	public void testNextVocableWhenCurrentVocableLastOne() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		Vocable lastVocable = addTestVocable("phrase 3", "translation 3", 0, 0);
		// execution
		Vocable nextVocable = vocableRepo.nextVocable(lastVocable);
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
