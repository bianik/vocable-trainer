package myProjects.vocableTrainer.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
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
		Vocable vocable = new Vocable("phrase 1","translation 1");
		when(vocableRepository.findByPhrase("phrase 1")).thenReturn(null);
		// exercise
		trainerController.newVocable(vocable);
		// verify
		verify(vocableRepository).findByPhrase("phrase 1");
		verify(vocableRepository).saveVocable(vocable);
	}

}
