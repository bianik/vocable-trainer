package myProjects.vocableTrainer.controller;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;

public class TrainerController {
	private VocableRepository vocableRepository;
	
	public TrainerController(VocableRepository vocableRepository) {
		super();
		this.vocableRepository = vocableRepository;
	}
	
	public void newVocable(Vocable voc) {
		vocableRepository.findByPhrase(voc.getPhrase());
		vocableRepository.saveVocable(voc);
	}

}
