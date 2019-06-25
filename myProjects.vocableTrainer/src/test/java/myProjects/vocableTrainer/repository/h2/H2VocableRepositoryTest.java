package myProjects.vocableTrainer.repository.h2;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.fail;

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
		try {
			assertThat(vocableRepo.findByTranslation("translation")).isNull();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testFindByTranslationFound() {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		// execution
		Vocable retreivedVocable = null;
		try {
			retreivedVocable = vocableRepo.findByTranslation("translation 1");
		} catch (SQLException e) {
		}
		// verify
		assertThat(retreivedVocable).isEqualTo(dbVocable);
	}

	@Test
	public void testFindByTranslationDbErrorShouldThrow() {
		// setup
		try {
			conn.close();
		} catch (SQLException e) {
		}
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.findByTranslation("translation")).isInstanceOf(SQLException.class);
	}

	@Test
	public void testSaveVocable() {
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		// execution
		try {
			vocableRepo.saveVocable(vocable);
		} catch (SQLException e) {
		}
		// verify
		assertThat(readAllVocablesFromRepository()).containsExactly(vocable);
	}

	@Test
	public void testSaveVocableDbErrorShouldThrow() {
		// setup
		try {
			conn.close();
		} catch (SQLException e) {
		}
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.saveVocable(vocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testUpdateVocable() {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		dbVocable.setCorrTries(6);
		dbVocable.setFalseTries(3);
		// execution
		try {
			vocableRepo.updateVocable(dbVocable);
		} catch (SQLException e) {
		}
		// verify
		assertThat(readAllVocablesFromRepository()).contains(dbVocable);
	}

	@Test
	public void testUpdateVocableDbErrorShouldThrow() {
		// setup
		addTestVocable("an other phrase", "an other translation", 0, 0);
		Vocable dbVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		dbVocable.setCorrTries(6);
		dbVocable.setFalseTries(3);
		try {
			conn.close();
		} catch (SQLException e) {
		}
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.updateVocable(dbVocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testNextVocableWhenNoCurrentVocable() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		// execution
		Vocable nextVocable = null;
		try {
			nextVocable = vocableRepo.nextVocable(null);
		} catch (SQLException e) {
		}
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
	}

	@Test
	public void testNextVocableWhenCurrentVocable() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		Vocable secondVocable = addTestVocable("phrase 2", "translation 2", 0, 0);
		// execution
		Vocable nextVocable = null;
		try {
			nextVocable = vocableRepo.nextVocable(firstVocable);
		} catch (SQLException e) {
		}
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
		Vocable nextVocable = null;
		try {
			nextVocable = vocableRepo.nextVocable(lastVocable);
		} catch (SQLException e) {
		}
		// verify
		assertThat(nextVocable).isEqualTo(firstVocable);
	}

	@Test
	public void testNextVocableDbErrorSholdThrow() {
		// setup
		Vocable firstVocable = addTestVocable("phrase 1", "translation 1", 0, 0);
		addTestVocable("phrase 2", "translation 2", 0, 0);
		try {
			conn.close();
		} catch (SQLException e) {
		}
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.nextVocable(firstVocable)).isInstanceOf(SQLException.class);
	}

	@Test
	public void testInitializeWhenNoTable() {
		// setup
		executeDbCommand("DROP TABLE IF EXISTS " + TABLE_NAME);
		// exercise
		try {
			vocableRepo.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
		assertThat(wantedTable).isTrue();
	}
	
	@Test
	public void testInitializeWhenTable() {
		// exercise
		try {
			vocableRepo.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
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

		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
		assertThat(wantedTable).isTrue();
	}

	@Test
	public void testInitializeWhenDbErrorShouldThrow() {
		// setup
		try {
			conn.close();
		} catch (SQLException e) {
		}
		// execute & verify
		assertThatThrownBy(() -> vocableRepo.initialize()).isInstanceOf(SQLException.class);
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
