package myProjects.vocableTrainer.controller;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;
import myProjects.vocableTrainer.view.TrainerView;

public class TrainerControllerIT {
	private static final String CORRECT_PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String GIVEN_INCORRECT_PHRASE = "wrong phrase";
	private static final int INITIAL_CORR_TRIES = 5;
	private static final int INITIAL_FALSE_TRIES = 3;

	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "VOCABLES";
	private static final String TCP_PORT = System.getProperty("tcpPort"); // get tcpPort from pom
	// JDBC driver name and database URL, use database server running in Docker
	// container
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:tcp://localhost:" + TCP_PORT + "/" + TABLE_NAME;
	private static Connection conn;

	@Mock
	private TrainerView trainerView;

	private VocableRepository vocableRepository;

	private TrainerController trainerController;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Class.forName(JDBC_DRIVER);
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		if (conn.isClosed())
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		vocableRepository = new H2VocableRepository(conn, TABLE_NAME);
		// always start with a new table using the repository
		vocableRepository.initialize();
		trainerController = new TrainerController(vocableRepository, trainerView);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNewVocable() throws Exception{
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		// exercise
		trainerController.newVocable(vocable);
		// verify
		verify(trainerView).showMessageVocableAdded("Vocable added: ", vocable);
	}
	
	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhrase()  throws Exception{
		// setup
		Vocable vocableToCheck = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		correctVocable.setFalseTries(INITIAL_FALSE_TRIES);
		correctVocable.setCorrTries(INITIAL_CORR_TRIES);
		vocableRepository.saveVocable(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(trainerView).showCheckResult("correct(6/9=67% corr. tries)", true); // 6/9=0.66667
	}
	
	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrect() throws Exception{
		// setup
		Vocable vocableToCheck = new Vocable(GIVEN_INCORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		correctVocable.setFalseTries(INITIAL_FALSE_TRIES);
		correctVocable.setCorrTries(INITIAL_CORR_TRIES);
		vocableRepository.saveVocable(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		String checkResultMessage = "incorrect(5/9=56% corr. tries) - correct phrase: '" + CORRECT_PHRASE + "'"; // 5/9=0.55556
		verify(trainerView).showCheckResult(checkResultMessage, false);
	}
	
	@Test
	public void testNextVocableWhenCurrentVocable() throws Exception {
		// setup
		Vocable vocable1 = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable vocable2 = new Vocable(CORRECT_PHRASE, TRANSLATION);
		vocableRepository.saveVocable(vocable1);
		vocableRepository.saveVocable(vocable2);
		// exercise
		trainerController.nextVocable(vocable1);
		// verify
		verify(trainerView).showNextVocable("", vocable2);
	}
}
