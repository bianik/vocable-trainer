package myProjects.vocableTrainer.controller;

import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.view.TrainerView;

public class TrainerControllerTest {
	private static final String CORRECT_PHRASE = "phrase 1";
	private static final String TRANSLATION = "translation 1";
	private static final String GIVEN_INCORRECT_PHRASE = "wrong phrase";
	private static final int INITIAL_CORR_TRIES = 5;
	private static final int INITIAL_FALSE_TRIES = 3;

	@Mock
	private TrainerView trainerView;

	@Mock
	private VocableRepository vocableRepository;

	@InjectMocks
	private TrainerController trainerController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNewVocableWhenVocableDoesNotAlreadyExist() throws Exception{
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		when(vocableRepository.findByPhrase(CORRECT_PHRASE)).thenReturn(null);
		// exercise
		trainerController.newVocable(vocable);
		// verify
		InOrder inOrder = inOrder(vocableRepository, trainerView);
		inOrder.verify(vocableRepository).findByPhrase("phrase 1");
		inOrder.verify(vocableRepository).saveVocable(vocable);
		inOrder.verify(trainerView).showMessageVocableAdded("Vocable added: ", vocable);
	}

	@Test
	public void testNewVocableWhenVocableDoesAlreadyExist()  throws Exception{
		// setup
		Vocable vocableToAdd = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable existingVocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		when(vocableRepository.findByPhrase("phrase 1")).thenReturn(existingVocable);
		// exercise
		trainerController.newVocable(vocableToAdd);
		// verify
		InOrder inOrder = inOrder(vocableRepository, trainerView);
		inOrder.verify(vocableRepository).findByPhrase(CORRECT_PHRASE);
		inOrder.verify(vocableRepository, never()).saveVocable(vocableToAdd);
		inOrder.verify(trainerView).showMessageVocableAdded("Vocable already exists: ", vocableToAdd);	
	}
	
	@Test
	public void testNewVocableDbErrorShowError() throws Exception{
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		doThrow(new SQLException()).when(vocableRepository).saveVocable(vocable);
		// exercise
		trainerController.newVocable(vocable);
		// verify
		verify(trainerView).showMessageVocableAdded("Database error!", null);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhrase()  throws Exception{
		Vocable vocableToCheck = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(CORRECT_PHRASE, TRANSLATION));
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		verify(trainerView).showCheckResult("correct(1/1=100% corr. tries)", true);
		verify(correctVocable).incCorrTries();
		verify(correctVocable, never()).incFalseTries();
		verify(vocableRepository).updateVocable(correctVocable);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhraseWithDifferentTryValues()  throws Exception{
		// setup
		Vocable vocableToCheck = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(CORRECT_PHRASE, TRANSLATION));
		correctVocable.setFalseTries(INITIAL_FALSE_TRIES);
		correctVocable.setCorrTries(INITIAL_CORR_TRIES);
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		verify(trainerView).showCheckResult("correct(6/9=67% corr. tries)", true); // 6/9=0.66667
		verify(correctVocable).incCorrTries();
		verify(correctVocable, never()).incFalseTries();
		verify(vocableRepository).updateVocable(correctVocable);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrectPhrase() throws Exception{
		// setup
		Vocable vocableToCheck = new Vocable(GIVEN_INCORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(CORRECT_PHRASE, TRANSLATION));
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		String checkResultMessage = "incorrect(0/1=0% corr. tries) - correct phrase: '" + CORRECT_PHRASE + "'";
		verify(trainerView).showCheckResult(checkResultMessage, false);
		verify(correctVocable).incFalseTries();
		verify(correctVocable, never()).incCorrTries();
		verify(vocableRepository).updateVocable(correctVocable);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrectPhraseWithDifferentTryValues() throws Exception{
		// setup
		Vocable vocableToCheck = new Vocable(GIVEN_INCORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(CORRECT_PHRASE, TRANSLATION));
		correctVocable.setFalseTries(INITIAL_FALSE_TRIES);
		correctVocable.setCorrTries(INITIAL_CORR_TRIES);
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		String checkResultMessage = "incorrect(5/9=56% corr. tries) - correct phrase: '" + CORRECT_PHRASE + "'"; // 5/9=0.55556
		verify(trainerView).showCheckResult(checkResultMessage, false);
		verify(correctVocable).incFalseTries();
		verify(correctVocable, never()).incCorrTries();
		verify(vocableRepository).updateVocable(correctVocable);
	}
	
	@Test
	public void testCheckVocableOnGivenPhraseDbErrorShowError() throws Exception{
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		doThrow(new SQLException()).when(vocableRepository).findByTranslation(TRANSLATION);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocable);
		// verify
		verify(trainerView).showCheckResult("Database error!", false);
	}

	@Test
	public void testNextVocableWhenCurrentVocable() throws Exception {
		// setup
		Vocable vocable1 = new Vocable(CORRECT_PHRASE, TRANSLATION);
		Vocable vocable2 = new Vocable(CORRECT_PHRASE, TRANSLATION);
		when(vocableRepository.nextVocable(vocable1)).thenReturn(vocable2);
		// exercise
		trainerController.nextVocable(vocable1);
		// verify
		verify(vocableRepository).nextVocable(vocable1);
		verify(trainerView).showNextVocable("", vocable2);
	}
	
	@Test
	public void testNextVocableWhenNoCurrentVocable() throws Exception {
		// setup
		Vocable vocable1 = new Vocable(CORRECT_PHRASE, TRANSLATION);
		when(vocableRepository.nextVocable(null)).thenReturn(vocable1);
		// exercise
		trainerController.nextVocable(null);
		// verify
		verify(vocableRepository).nextVocable(null);
		verify(trainerView).showNextVocable("", vocable1);
	}
	
	@Test
	public void testNextVocableDbErrorShowError() throws Exception{
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		doThrow(new SQLException()).when(vocableRepository).nextVocable(vocable);
		// exercise
		trainerController.nextVocable(vocable);
		// verify
		verify(trainerView).showNextVocable("Database error!", null);
	}
}
