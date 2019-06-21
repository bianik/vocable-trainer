package myProjects.vocableTrainer.view.console;

import java.io.PrintStream;
import java.util.Scanner;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.view.TrainerView;

public class ConsoleTrainerView implements TrainerView {
	private Scanner in;
	private PrintStream out;
	private TrainerController trainerContr;
	private Vocable currentVocable = null;

	public ConsoleTrainerView(Scanner in, PrintStream out, TrainerController trainerContr) {
		super();
		this.in = in;
		this.out = out;
		this.trainerContr = trainerContr;
	}

	@Override
	public void showMessageVocableAdded(String message, Vocable vocable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCheckResult(String message, boolean result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNextVocable(String message, Vocable vocable) {
		// TODO Auto-generated method stub

	}

	public boolean startConsole() {
		out.println(
				"##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning");
		if (in.hasNextLine()) {
			String comm = in.nextLine();
			if (comm.equals("new") || comm.equals("n")) {
				out.println("phrase: ");
				String phrase = in.nextLine().trim();
				if (!phrase.isEmpty()) {
					out.println("translation: ");
					String translation = in.nextLine().trim();
					if (!translation.isEmpty()) {
						trainerContr.newVocable(new Vocable(phrase, translation));
					} else {
						out.println("ABORT: no translation!");
					}
				} else {
					out.println("ABORT: no phrase!");
				}
			} else if (comm.equals("learn") || comm.equals("l")) {
				trainerContr.nextVocable(currentVocable);
			} else if (comm.equals("exit")){
				return false;
			} else {
				out.println("ABORT: wrong command!");
			}
		}
		return true;
	}

	// package-privates setter for testing
	void setCurrentVocable(Vocable currentVocable) {
		this.currentVocable = currentVocable;
	}

}
