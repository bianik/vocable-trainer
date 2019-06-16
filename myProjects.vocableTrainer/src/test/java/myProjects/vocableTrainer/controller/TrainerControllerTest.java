package myProjects.vocableTrainer.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.view.TrainerView;

public class TrainerControllerTest {

	@Mock
	private TrainerView trainerView;
	
	@Mock
	private VocableRepository vocableRepositor;
	
	@InjectMocks
	private TrainerController trainerController;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNewVocableWhenVocableDoesNotAlreadyExist() {
		trainerController.newVocable(null);
	}

}
