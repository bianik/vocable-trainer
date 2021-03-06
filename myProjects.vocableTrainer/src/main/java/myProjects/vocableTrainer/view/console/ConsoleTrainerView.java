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
	private Vocable currentVocable;
	private boolean startMessage = true;

	// ANSI escape codes for colors
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";

	public ConsoleTrainerView(Scanner in, PrintStream out) {
		super();
		this.in = in;
		this.out = out;
	}

	@Override
	public void showMessageVocableAdded(String message, Vocable vocable) {
		if (vocable != null)
			out.println(message + vocable.getPhrase() + " - " + vocable.getTranslation());
		else
			out.println(ANSI_RED + message + ANSI_RESET);
	}

	@Override
	public void showCheckResult(String message, boolean result) {
		out.println((result ? ANSI_GREEN : ANSI_RED) + message + ANSI_RESET);
	}

	@Override
	public void showNextVocable(String message, Vocable vocable) {
		if (vocable != null) {
			out.println("translation: " + vocable.getTranslation() + "\nenter phrase: ");
		} else {
			out.println(ANSI_RED + message + ANSI_RESET);
		}
		currentVocable = vocable;
	}

	public boolean startConsole() {
		if (startMessage) {
			out.println(
					"##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning");
			startMessage = false;
		}
		if (in.hasNextLine()) {
			switch (in.nextLine()) {
			case "new":
			case "n":
				newVocable();
				break;
			case "learn":
			case "l":
				learn();
				break;
			case "exit":
				return false;
			default:
				out.println("ABORT: wrong command!");
			}
		}
		return true;
	}

	private void learn() {
		trainerContr.nextVocable(currentVocable);
		if (currentVocable != null) {
			String inputToCheck = in.nextLine().trim();
			trainerContr.checkVocableOnGivenPhrase(new Vocable(inputToCheck, currentVocable.getTranslation()));
		}
	}

	private void newVocable() {
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
	}

	public void setTrainerContr(TrainerController trainerContr) {
		this.trainerContr = trainerContr;
	}

	// package-private setter for testing
	void setCurrentVocable(Vocable currentVocable) {
		this.currentVocable = currentVocable;
	}

	// package-private getter for testing
	Vocable getCurrentVocable() {
		return currentVocable;
	}

}
