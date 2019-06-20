package myProjects.vocableTrainer.controller;

import java.sql.SQLException;

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
		try {
			if (vocableRepository.findByPhrase(voc.getPhrase()) == null) {
				vocableRepository.saveVocable(voc);
				trainerView.showMessageVocableAdded("Vocable added", voc);
			} else {
				trainerView.showMessageVocableAdded("Vocable already exists", voc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkVocableOnGivenPhrase(Vocable vocableToCheck) {
		Vocable correctVocable;
		try {
			correctVocable = vocableRepository.findByTranslation(vocableToCheck.getTranslation());
		if (vocableToCheck.compareTo(correctVocable)) {
			correctVocable.incCorrTries();
			int c = correctVocable.getCorrTries(), f = correctVocable.getFalseTries();
			trainerView.showCheckResult("correct(" + c + "/" + (c + f) + "="
					+ Integer.toString(Math.round(100 * ((float) c / (c + f)))) + "% corr. tries)", true);
		} else {
			correctVocable.incFalseTries();
			int c = correctVocable.getCorrTries(), f = correctVocable.getFalseTries();
			trainerView.showCheckResult(
					"incorrect(" + c + "/" + (c + f) + "=" + Integer.toString(Math.round(100 * ((float) c / (c + f))))
							+ "% corr. tries) - correct phrase: '" + correctVocable.getPhrase() + "'",
					false);
		}
		vocableRepository.updateVocable(correctVocable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nextVocable(Vocable currentVocable) {
		try {
			trainerView.showNextVocable(vocableRepository.nextVocable(currentVocable));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
