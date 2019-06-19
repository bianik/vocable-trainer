package myProjects.vocableTrainer.repository.h2;

import java.sql.Connection;

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
		// TODO Auto-generated method stub
		return null;
	}

	public Vocable findByTranslation(String translation) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveVocable(Vocable vocable) {
		// TODO Auto-generated method stub

	}

	public void updateVocable(Vocable vocable) {
		// TODO Auto-generated method stub

	}

	public Vocable nextVocable(Vocable currentVocable) {
		// TODO Auto-generated method stub
		return null;
	}

}
