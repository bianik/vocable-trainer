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
		if (vocableRepository.findByPhrase(voc.getPhrase()) == null) {
			vocableRepository.saveVocable(voc);
			trainerView.showMessageVocableAdded("Vocable added", voc);
		} else {
			trainerView.showMessageVocableAdded("Vocable already exists", voc);
		}
	}

	public void checkVocableOnGivenPhrase(Vocable vocableToCheck) {
		Vocable correctVocable = vocableRepository.findByTranslation(vocableToCheck.getTranslation());
		if (vocableToCheck.compareTo(correctVocable)) {
			trainerView.showCheckResult("correct", true);
			correctVocable.incCorrTries();
		} else {
			correctVocable.incFalseTries();
			trainerView.showCheckResult("incorrect(" + correctVocable.getCorrTries() + "/"
					+ (correctVocable.getCorrTries() + correctVocable.getFalseTries()) + "="
					+ Integer.toString((int) (100.0 * (correctVocable.getCorrTries() / correctVocable.getFalseTries())))
					+ "% corr. tries) - correct phrase: '" + correctVocable.getPhrase() + "'", false);
		}
	}

}
