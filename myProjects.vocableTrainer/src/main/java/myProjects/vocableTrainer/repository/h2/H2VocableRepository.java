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

	public Vocable findByPhrase(String phrase) {
		return findBy("PHRASE", phrase);
	}

	public Vocable findByTranslation(String translation) {
		return findBy("TRANSLATION", translation);
	}
	
	private Vocable findBy(String column, String argument) {
		String command = "SELECT * FROM " + tableName + " WHERE " + column + " = '" + argument + "'";
		Statement stmt = null;
		ResultSet rs =  null;
		Vocable v = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(command);
	         // extract data from result set 
	         if(rs.first()) { 
	        	v = new Vocable();
	            // Retrieve by column name 
	            v.setPhrase(rs.getString("phrase"));
	            v.setTranslation(rs.getString("translation"));
	            v.setCorrTries(rs.getInt("corrTries"));
	            v.setFalseTries(rs.getInt("falseTries"));
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if(rs != null) rs.close();} catch (SQLException e) {}
			try {if(stmt != null) stmt.close();} catch (SQLException e) {}
		}
		return v;
	}

	public void saveVocable(Vocable vocable) {
		String command = "INSERT INTO " + tableName + " VALUES ('" + vocable.getPhrase() + "', '"
				+ vocable.getTranslation() + "', " + vocable.getCorrTries() + ", " + vocable.getFalseTries() + ")";
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if(rs != null) rs.close();} catch (SQLException e) {}
			try {if(stmt != null) stmt.close();} catch (SQLException e) {}
		}
	}

	public void updateVocable(Vocable vocable) {
		String command = "UPDATE " + tableName + " SET CORRTRIES = " + vocable.getCorrTries() + ", FALSETRIES = " + vocable.getFalseTries() + " WHERE PHRASE = '"
				+ vocable.getPhrase() + "'";
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if(rs != null) rs.close();} catch (SQLException e) {}
			try {if(stmt != null) stmt.close();} catch (SQLException e) {}
		}
	}

	public Vocable nextVocable(Vocable currentVocable) {
		// TODO Auto-generated method stub
		return null;
	}

}
