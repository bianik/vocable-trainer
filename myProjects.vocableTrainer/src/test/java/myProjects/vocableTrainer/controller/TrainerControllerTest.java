package myProjects.vocableTrainer.controller;

import static org.junit.Assert.*;
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
		Vocable vocable = new Vocable("phrase 1", "translation 1");
		when(vocableRepository.findByPhrase("phrase 1")).thenReturn(null);
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
		Vocable vocableToAdd = new Vocable("phrase 1", "translation 1");
		Vocable existingVocable = new Vocable("phrase 1", "translation 1");
		when(vocableRepository.findByPhrase("phrase 1")).thenReturn(existingVocable);
		// exercise
		trainerController.newVocable(vocableToAdd);
		// verify
		InOrder inOrder = inOrder(vocableRepository, trainerView);
		inOrder.verify(vocableRepository).findByPhrase("phrase 1");
		inOrder.verify(vocableRepository, never()).saveVocable(vocableToAdd);
		inOrder.verify(trainerView).showMessageVocableAdded("Vocable already exists", vocableToAdd);
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenCorrectPhrase() {
		// setup
		final String PHRASE = "phrase 1";
		final String TRANSLATION = "translation 1";
		Vocable vocableToCheck = new Vocable(PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(PHRASE, TRANSLATION));
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		verify(trainerView).showCheckResult("correct", true);
		verify(correctVocable).incCorrTries();
		verify(correctVocable, never()).incFalseTries();
	}

	@Test
	public void testCheckVocableOnGivenPhraseWhenIncorrectPhrase() {
		// setup
		final String CORRECT_PHRASE = "phrase 1";
		final String GIVEN_INCORRECT_PHRASE = "wrong phrase";
		final String TRANSLATION = "translation 1";
		Vocable vocableToCheck = new Vocable(GIVEN_INCORRECT_PHRASE, TRANSLATION);
		Vocable correctVocable = spy(new Vocable(CORRECT_PHRASE, TRANSLATION));
		when(vocableRepository.findByTranslation(TRANSLATION)).thenReturn(correctVocable);
		// exercise
		trainerController.checkVocableOnGivenPhrase(vocableToCheck);
		// verify
		verify(vocableRepository).findByTranslation(TRANSLATION);
		verify(trainerView).showCheckResult("incorrect(0/1=0% corr. tries) - correct phrase: '" + CORRECT_PHRASE + "'", false);
		verify(correctVocable).incFalseTries();
		verify(correctVocable, never()).incCorrTries();
	}
}
