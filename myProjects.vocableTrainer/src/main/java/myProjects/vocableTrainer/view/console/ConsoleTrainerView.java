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

	public void startConsole() {
		out.println(
				"##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning");
		if (in.hasNextLine()) {
			in.nextLine();
			out.println("phrase: ");
			String phrase = in.nextLine();
			out.println("translation: ");
			String translation = in.nextLine();
			trainerContr.newVocable(new Vocable(phrase, translation));
		}
	}

}
