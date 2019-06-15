package myProjects.vocableTrainer.view;

import myProjects.vocableTrainer.model.Vocable;

public interface TrainerView {
	void showMessageVocableAdded(String message, Vocable vocable);
	
	void showCheckResult(String message, boolean result);
	
	void showNextVocable(Vocable vocable);
}
