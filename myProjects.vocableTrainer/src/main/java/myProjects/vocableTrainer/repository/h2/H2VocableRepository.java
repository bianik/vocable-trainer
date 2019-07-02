package myProjects.vocableTrainer.repository.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;

public class H2VocableRepository implements VocableRepository {
	private static final String FALSE_TRIES = "falseTries";
	private static final String CORR_TRIES = "corrTries";
	private static final String TRANSLATION = "translation";
	private static final String PHRASE = "phrase";
	private Connection conn;
	private String tableName;

	public H2VocableRepository(Connection conn, String tableName) {
		super();
		this.conn = conn;
		this.tableName = tableName;
	}

	public void initialize() throws SQLException {
		String command1 = "DROP TABLE IF EXISTS " + tableName;
		String command2 = "CREATE TABLE " + tableName
				+ "(phrase VARCHAR(30), translation VARCHAR(30), corrTries INTEGER, falseTries INTEGER)";
		try (Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(command1);
			stmt.executeUpdate(command2);
		}
	}

	public Vocable findByPhrase(String phrase) throws SQLException {
		return findBy("PHRASE", phrase);
	}

	public Vocable findByTranslation(String translation) throws SQLException {
		return findBy("TRANSLATION", translation);
	}

	private Vocable findBy(String column, String argument) throws SQLException {
		String command = "SELECT * FROM " + tableName + " WHERE " + column + " = '" + argument + "'";
		Vocable v = null;
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(command);) {
			// extract data from result set
			if (rs.first()) {
				v = new Vocable();
				// Retrieve by column name
				v.setPhrase(rs.getString(PHRASE));
				v.setTranslation(rs.getString(TRANSLATION));
				v.setCorrTries(rs.getInt(CORR_TRIES));
				v.setFalseTries(rs.getInt(FALSE_TRIES));
			}
		}
		return v;
	}

	public void saveVocable(Vocable vocable) throws SQLException {
		String command = "INSERT INTO " + tableName + " VALUES ('" + vocable.getPhrase() + "', '"
				+ vocable.getTranslation() + "', " + vocable.getCorrTries() + ", " + vocable.getFalseTries() + ")";
		try (Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(command);
		}
	}

	public void updateVocable(Vocable vocable) throws SQLException {
		String command = "UPDATE " + tableName + " SET CORRTRIES = " + vocable.getCorrTries() + ", FALSETRIES = "
				+ vocable.getFalseTries() + " WHERE PHRASE = '" + vocable.getPhrase() + "'";
		try (Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(command);
		}
	}

	public Vocable nextVocable(Vocable currentVocable) throws SQLException {
		String command = "SELECT * FROM " + tableName;
		Vocable v = null;
		try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = stmt.executeQuery(command);) {
			// extract data from result set
			if (currentVocable != null) {
				do {
					rs.next();
				} while (!rs.getString(PHRASE).equals(currentVocable.getPhrase()));
				if (!rs.next()) // if this is the last entry, jump to first one
					rs.first();
			} else {
				rs.first();
			}
			v = new Vocable();
			// Retrieve by column name
			v.setPhrase(rs.getString(PHRASE));
			v.setTranslation(rs.getString(TRANSLATION));
			v.setCorrTries(rs.getInt(CORR_TRIES));
			v.setFalseTries(rs.getInt(FALSE_TRIES));
		}
		return v;
	}
}
