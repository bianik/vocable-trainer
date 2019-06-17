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
		verify(vocableRepository).findByPhrase("phrase 1");
		verify(vocableRepository, never()).saveVocable(vocableToAdd);
	}

}
