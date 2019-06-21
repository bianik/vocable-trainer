package myProjects.vocableTrainer.view.console;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.model.Vocable;

public class ConsoleTrainerViewTest {
	TrainerController trainerController = mock(TrainerController.class);
	ByteArrayOutputStream outputBuffer;

	private static final String NL = System.getProperty("line.separator");
	private static final String PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartConsoleShowStartupMessage() {
		// setup
		String userInput = "";
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String output = outputBuffer.toString();
		assertThat(output).isEqualTo(
				"##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning\n");
	}

	@Test
	public void testStartConsoleNewVocable() {
		// setup
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		String userInput = "new" + NL + PHRASE + NL + TRANSLATION + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("phrase: ");
		assertThat(output[4]).isEqualTo("translation: ");
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	public void testStartConsoleNewVocableWhenCommandN() {
		// setup
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		String userInput = "n" + NL + PHRASE + NL + TRANSLATION + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("phrase: ");
		assertThat(output[4]).isEqualTo("translation: ");
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	public void testStartConsoleNewVocableIgnoreLeadingOrTrailingWhiteSpace() {
		// setup
		Vocable vocableToAdd = new Vocable(PHRASE, TRANSLATION);
		String userInput = "new" + NL + "   " + PHRASE + "\t" + NL + " \t" + TRANSLATION + "   " + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("phrase: ");
		assertThat(output[4]).isEqualTo("translation: ");
		verify(trainerController).newVocable(vocableToAdd);
	}

	@Test
	public void testStartConsoleNewVocableWhenBlanckPhrase() {
		// setup
		String userInput = "new" + NL + NL + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("phrase: ");
		assertThat(output[4]).isEqualTo("ABORT: no phrase!");
		verify(trainerController, never()).newVocable(any());
	}

	@Test
	public void testStartConsoleNewVocableWhenBlanckTranslation() {
		// setup
		String userInput = "new" + NL + PHRASE + NL + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("phrase: ");
		assertThat(output[4]).isEqualTo("translation: ");
		assertThat(output[5]).isEqualTo("ABORT: no translation!");
		verify(trainerController, never()).newVocable(any());
	}

	@Test
	public void testStartConsoleLearningWhenNoCurrentVocable() {
		// setup
		String userInput = "learn" + NL + TRANSLATION + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		verify(trainerController).nextVocable(null);
	}

	@Test
	public void testStartConsoleLearningWhenCurrentVocable() {
		// setup
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		String userInput = "learn" + NL + TRANSLATION + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		view.setCurrentVocable(currentVocable);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		verify(trainerController).nextVocable(currentVocable);
	}

	@Test
	public void testStartConsoleLearningWhenCommandL() {
		// setup
		Vocable currentVocable = new Vocable(PHRASE, TRANSLATION);
		String userInput = "l" + NL + TRANSLATION + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		view.setCurrentVocable(currentVocable);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		verify(trainerController).nextVocable(currentVocable);
	}

	@Test
	public void testStartConsoleIgnoreWrongCommand() {
		// setup
		String userInput = "wrong command" + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("ABORT: wrong command!");
		verify(trainerController, never()).nextVocable(any());
	}

	@Test
	public void testStartConsoleWhenTaskCompletedReturnsTrue() {
		// setup
		String userInput = "wrong command" + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise & verify
		assertThat(view.startConsole()).isTrue();
	}

	@Test
	public void testStartConsoleWhenExitCommandThenReturnFalse() {
		// setup
		String userInput = "exit" + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise & verify
		assertThat(view.startConsole()).isFalse();
	}

	@Test
	public void testStartConsoleShowStartUpMessageOnlyFirstTime() {
		// iterate 3 times
		// setup
		String userInput = "wrong command" + NL + "wrong command" + NL + "wrong command" + NL;
		ConsoleTrainerView view = createConsoleTrainerViewWithUserInput(userInput);
		// exercise
		for (int i = 0; i < 3; i++)
			view.startConsole();
		// verify
		String[] output = outputBuffer.toString().split(NL);
		assertThat(output[0]).isEqualTo("##### Vocable Trainer #####");
		assertThat(output[1]).isEqualTo("enter 'n'/'new' to add a new vocable");
		assertThat(output[2]).isEqualTo("enter 'l'/'learn' to start learning");
		assertThat(output[3]).isEqualTo("ABORT: wrong command!");
		assertThat(output[4]).isEqualTo("ABORT: wrong command!");
		assertThat(output[5]).isEqualTo("ABORT: wrong command!");
	}

	private ConsoleTrainerView createConsoleTrainerViewWithUserInput(String userInput) {
		Scanner scanner = new Scanner(userInput);
		outputBuffer = new ByteArrayOutputStream();
		PrintStream outPrinter = new PrintStream(outputBuffer);
		return new ConsoleTrainerView(scanner, outPrinter, trainerController);
	}
}
