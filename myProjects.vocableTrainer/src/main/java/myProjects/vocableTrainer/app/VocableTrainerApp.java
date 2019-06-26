package myProjects.vocableTrainer.app;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;
import myProjects.vocableTrainer.view.swing.SwingTrainerView;

public class VocableTrainerApp implements Runnable {
	// Database credentials
	private static final String USER = "sa";
	private static final String PASS = "";
	private static final String TABLE_NAME = "VOCABLES";
	private static final String TCP_PORT = "1523"; // get tcpPort from pom
	// JDBC driver name and database URL, use database server running in Docker
	// container
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:tcp://localhost:" + TCP_PORT + "/" + TABLE_NAME;
	private Connection conn;

	public static void main(String[] args) {
		new Thread(new VocableTrainerApp()).start();
	}

	@Override
	public void run() {
		EventQueue.invokeLater(() -> {
			try {
				// set up repository
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				VocableRepository vocableRepository = new H2VocableRepository(conn, TABLE_NAME);
				vocableRepository.initialize();
				SwingTrainerView swingTrainerView = new SwingTrainerView(); // call and test methods on this
				TrainerController trainerController= new TrainerController(vocableRepository, swingTrainerView);
				swingTrainerView.setTrainerController(trainerController);
				swingTrainerView.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
