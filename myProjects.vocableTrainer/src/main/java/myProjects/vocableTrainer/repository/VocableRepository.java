package myProjects.vocableTrainer.repository;

import myProjects.vocableTrainer.model.Vocable;

public interface VocableRepository {
	public Vocable findByPhrase(String phrase);
	
	public Vocable findByTranslation(String translation);
	
	public void saveVocable(Vocable vocable);
	
	public void updateVocable(Vocable vocable);
}
