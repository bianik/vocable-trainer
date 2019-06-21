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

public class ConsoleTrainerViewTest {
	TrainerController trainerController = mock(TrainerController.class);

	private final String newLine = System.getProperty("line.separator");

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
		Scanner scanner = new Scanner(userInput);
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		PrintStream outPrinter = new PrintStream(outputBuffer);
		ConsoleTrainerView view = new ConsoleTrainerView(scanner, outPrinter, trainerController);
		// exercise
		view.startConsole();
		// verify
		String output = outputBuffer.toString();
		assertThat(output).isEqualTo("##### Vocable Trainer #####\nenter 'n'/'new' to add a new vocable\nenter 'l'/'learn' to start learning\n");
	}
	
	

}
