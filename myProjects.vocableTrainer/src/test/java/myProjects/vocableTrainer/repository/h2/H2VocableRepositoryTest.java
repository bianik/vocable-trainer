package myProjects.vocableTrainer.repository.h2;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
	static final String DB_URL = "jdbc:h2:mem:"; // use volatile in-memory database

	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "VOCABLES";

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
	public void testFindByPhraseNotFound() throws SQLException {
		assertThat(vocableRepo.findByPhrase("phrase")).isNull();
	}

	@Test
	public void testFindByPhraseFound() throws SQLException {
		// setup
		addTestVocable("an other phrase", "translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 5, 7);
		// execution
		Vocable retreivedVocable = null;
		retreivedVocable = vocableRepo.findByPhrase("phrase 1");
		// verify
		assertThat(retreivedVocable).isEqualTo(dbVocable);
	}

	@Test
	public void testFindByPhraseDbErrorShouldThrow() throws SQLException {
		// setup
		conn.close();
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.findByPhrase("phrase")).isInstanceOf(SQLException.class);
	}

	@Test
	public void testFindByTranslationNotFound() throws SQLException {
		assertThat(vocableRepo.findByTranslation("translation")).isNull();
	}

	@Test
	public void testFindByTranslationFound() throws SQLException {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 5, 7);
		// execution
		Vocable retreivedVocable = null;
		retreivedVocable = vocableRepo.findByTranslation("translation 1");
		// verify
		assertThat(retreivedVocable).isEqualTo(dbVocable);
	}

	@Test
	public void testFindByTranslationDbErrorShouldThrow() throws SQLException {
		// setup
		conn.close();
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.findByTranslation("translation")).isInstanceOf(SQLException.class);
	}

	@Test
	public void testSaveVocable() throws SQLException {
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		vocable.setCorrTries(5);
		vocable.setFalseTries(7);
		// execution
		vocableRepo.saveVocable(vocable);
		// verify
		assertThat(readAllVocablesFromRepository()).containsExactly(vocable);
	}

	@Test
	public void testSaveVocableDbErrorShouldThrow() throws SQLException {
		// setup
		conn.close();
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.saveVocable(vocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testUpdateVocable() throws SQLException {
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
	public void testUpdateVocableDbErrorShouldThrow() throws SQLException {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		dbVocable.setCorrTries(6);
		dbVocable.setFalseTries(3);
		conn.close();
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.updateVocable(dbVocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testNextVocableWhenNoCurrentVocable() throws SQLException {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		// execution
		Vocable nextVocable = null;
		nextVocable = vocableRepo.nextVocable(null);
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
	}

	@Test
	public void testNextVocableWhenCurrentVocable() throws SQLException {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		Vocable secondVocable = addTestVocable("phrase 2", "translation 2", 5, 7);
		// execution
		Vocable nextVocable = null;
		nextVocable = vocableRepo.nextVocable(firstVocable);
		// verify
		assertThat(nextVocable).isEqualTo(secondVocable);
	}

	@Test
	public void testNextVocableWhenCurrentVocableLastOne() throws SQLException {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 5, 7);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		Vocable lastVocable = addTestVocable("phrase 3", "translation 3", 0, 0);
		// execution
		Vocable nextVocable = null;
		nextVocable = vocableRepo.nextVocable(lastVocable);
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
	}

	@Test
	public void testNextVocableDbErrorSholdThrow() throws SQLException {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		conn.close();
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.nextVocable(firstVocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testInitializeWhenNoTable() throws SQLException {
		// setup
		executeDbCommand("DROP TABLE IF EXISTS " + TABLE_NAME);
		// exercise
		vocableRepo.initialize();
		// verify
		// try to find the table
		boolean wantedTable = false;
		try (ResultSet rs = conn.getMetaData().getTables(null, null, TABLE_NAME, new String[] { "TABLE" });) {
			while (rs.next()) {
				wantedTable = true;
				assertThat(wantedTable).isTrue();
				try (Statement stmt = conn.createStatement();
						ResultSet tableRs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);) {
					ResultSetMetaData rsmd = tableRs.getMetaData();
					assertThat(rsmd.getColumnLabel(1)).isEqualTo("PHRASE");
					assertThat(rsmd.getColumnTypeName(1)).isEqualTo("VARCHAR");
					assertThat(rsmd.getColumnLabel(2)).isEqualTo("TRANSLATION");
					assertThat(rsmd.getColumnTypeName(2)).isEqualTo("VARCHAR");
					assertThat(rsmd.getColumnLabel(3)).isEqualTo("CORRTRIES");
					assertThat(rsmd.getColumnTypeName(3)).isEqualTo("INTEGER");
					assertThat(rsmd.getColumnLabel(4)).isEqualTo("FALSETRIES");
					assertThat(rsmd.getColumnTypeName(4)).isEqualTo("INTEGER");
				}
			}
		}
		assertThat(wantedTable).isTrue();
	}

	@Test
	public void testInitializeWhenTable() throws SQLException {
		// exercise
		vocableRepo.initialize();
		// verify
		// try to find the table
		boolean wantedTable = false;
		try (ResultSet rs = conn.getMetaData().getTables(null, null, TABLE_NAME, new String[] { "TABLE" });) {
			while (rs.next()) {
				wantedTable = true;
				assertThat(wantedTable).isTrue();
				try (Statement stmt = conn.createStatement();
						ResultSet tableRs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);) {
					ResultSetMetaData rsmd = tableRs.getMetaData();
					assertThat(rsmd.getColumnLabel(1)).isEqualTo("PHRASE");
					assertThat(rsmd.getColumnTypeName(1)).isEqualTo("VARCHAR");
					assertThat(rsmd.getColumnLabel(2)).isEqualTo("TRANSLATION");
					assertThat(rsmd.getColumnTypeName(2)).isEqualTo("VARCHAR");
					assertThat(rsmd.getColumnLabel(3)).isEqualTo("CORRTRIES");
					assertThat(rsmd.getColumnTypeName(3)).isEqualTo("INTEGER");
					assertThat(rsmd.getColumnLabel(4)).isEqualTo("FALSETRIES");
					assertThat(rsmd.getColumnTypeName(4)).isEqualTo("INTEGER");
				}
			}

		}
		assertThat(wantedTable).isTrue();
	}

	@Test
	public void testInitializeWhenDbErrorShouldThrow() throws SQLException {
		// setup
		conn.close();
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.initialize()).isInstanceOf(SQLException.class);
	}

	////////////////// helping functions ////////////////////////////////

	private List<Vocable> readAllVocablesFromRepository() throws SQLException {
		String command = "SELECT * FROM " + TABLE_NAME;
		List<Vocable> allVocables = new ArrayList<Vocable>();
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(command);) {
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
		}
		return allVocables;
	}

	public Vocable addTestVocable(String phrase, String translation, int falseTries, int corrTries)
			throws SQLException {
		executeDbCommand("INSERT INTO " + TABLE_NAME + " VALUES ('" + phrase + "', '" + translation + "', " + corrTries
				+ ", " + falseTries + ")");
		Vocable v = new Vocable(phrase, translation);
		v.setCorrTries(corrTries);
		v.setFalseTries(falseTries);
		return v;
	}

	public void executeDbCommand(String command) throws SQLException {
		try (Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(command);
		}
	}
}
