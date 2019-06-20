package myProjects.vocableTrainer.repository;

import java.sql.SQLException;

import myProjects.vocableTrainer.model.Vocable;

public interface VocableRepository {
	public Vocable findByPhrase(String phrase) throws SQLException;
	
	public Vocable findByTranslation(String translation) throws SQLException;
	
	public void saveVocable(Vocable vocable);
	
	public void updateVocable(Vocable vocable);

	public Vocable nextVocable(Vocable currentVocable);
}
