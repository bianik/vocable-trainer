package myProjects.vocableTrainer.repository.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;

public class H2VocableRepository implements VocableRepository {
	private Connection conn;
	private String tableName;

	public H2VocableRepository(Connection conn, String tableName) {
		super();
		this.conn = conn;
		this.tableName = tableName;
	}

	public void initialize() throws SQLException {
		String command = "CREATE TABLE " + tableName
				+ "(phrase VARCHAR(30), translation VARCHAR(30), corrTries INTEGER, falseTries INTEGER)";
		try (Statement stmt = conn.createStatement();) {
			stmt.executeUpdate(command);
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
				v.setPhrase(rs.getString("phrase"));
				v.setTranslation(rs.getString("translation"));
				v.setCorrTries(rs.getInt("corrTries"));
				v.setFalseTries(rs.getInt("falseTries"));
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
				while (rs.next()) {
					if (rs.getString("phrase").equals(currentVocable.getPhrase())) {
						if (rs.isLast())
							rs.first();
						else
							rs.next();
						break;
					}
				}
			} else {
				rs.first();
			}
			v = new Vocable();
			// Retrieve by column name
			v.setPhrase(rs.getString("phrase"));
			v.setTranslation(rs.getString("translation"));
			v.setCorrTries(rs.getInt("corrTries"));
			v.setFalseTries(rs.getInt("falseTries"));
		}
		return v;
	}
}
