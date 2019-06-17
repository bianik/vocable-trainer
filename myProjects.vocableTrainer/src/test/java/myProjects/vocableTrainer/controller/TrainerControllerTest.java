package myProjects.vocableTrainer.controller;

import static org.mockito.Mockito.*;

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
	public void testNewVocableWhenVocableDoesNotAlreadyExist() {
		// setup
		Vocable vocable = new Vocable(CORRECT_PHRASE, TRANSLATION);
		when(vocableRepository.findByPhrase(CORRECT_PHRASE)).thenReturn(null);
		// exercise
		trainerController.newVocable(vocable);
		// verify
		InOrder inOrder = inOrder(vocableRepository, trainerView);
		inOrder.verify(vocableRepository).findByPhrase("phrase 1");
		inOrder.verify(vocableRepository).saveVocable(vocable);
		inOrder.verify(trainerView).showMessageVocableAdded("Vocable added", vocable);
	}

	@Test
	public void testNewVocableWhenVocableDoesAlreadyExist() {
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
		inOrder.verify(trainerView).showMessageVocableAdded("Vocable already exists", vocableToAdd);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhrase() {
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
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhraseWithDifferentTryValues() {
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
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrectPhrase() {
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
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrectPhraseWithDifferentTryValues() {
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
	}
}
