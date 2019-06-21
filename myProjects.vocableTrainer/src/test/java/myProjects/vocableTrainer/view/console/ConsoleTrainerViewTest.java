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
		assertThat(output).isEqualTo("##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning\n");
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

	private ConsoleTrainerView createConsoleTrainerViewWithUserInput(String userInput) {
		Scanner scanner = new Scanner(userInput);
		outputBuffer = new ByteArrayOutputStream();
		PrintStream outPrinter = new PrintStream(outputBuffer);
		return new ConsoleTrainerView(scanner, outPrinter, trainerController);
	}
}
