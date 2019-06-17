package myProjects.vocableTrainer.controller;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.view.TrainerView;

public class TrainerController {
	private VocableRepository vocableRepository;
	private TrainerView trainerView;
	
	public TrainerController(VocableRepository vocableRepository, TrainerView trainerView) {
		super();
		this.vocableRepository = vocableRepository;
		this.trainerView = trainerView;
	}
	
	public void newVocable(Vocable voc) {
		vocableRepository.findByPhrase(voc.getPhrase());
		vocableRepository.saveVocable(voc);
		trainerView.showMessageVocableAdded("Vocable added", voc);
	}

}
