package myProjects.vocableTrainer.repository;

import java.sql.SQLException;

import myProjects.vocableTrainer.model.Vocable;

public interface VocableRepository {
	public Vocable findByPhrase(String phrase) throws SQLException;
	
	public Vocable findByTranslation(String translation) throws SQLException;
	
	public void saveVocable(Vocable vocable) throws SQLException;
	
	public void updateVocable(Vocable vocable) throws SQLException;

	public Vocable nextVocable(Vocable currentVocable) throws SQLException;

	public void initialize() throws SQLException;
}
