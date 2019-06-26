package myProjects.vocableTrainer.app;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;
import myProjects.vocableTrainer.view.swing.SwingTrainerView;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class VocableTrainerApp implements Runnable {
	// Database credentials
	@Option(names = { "--h2-user" }, description = "h2 DB username")
	private String h2User = "sa";
	@Option(names = { "--h2-password" }, description = "h2 DB password")
	private String h2Pass = "";
	@Option(names = { "--h2-table", "-t" }, description = "h2 DB table name")
	private String h2Table = "VOCABLES";
	@Option(names = { "--h2-port", "-p" }, description = "h2 DB TCP port")
	private String h2Port = "1523";
	@Option(names = { "--h2-host" }, description = "h2 DB TCP port")
	private String h2Host = "localhost";
	@Option(names = { "--h2-in-memory", "-m" }, description = "h2 DB username")
	private boolean h2InMemo = false;
	@Option(names = { "--h2-no-init" }, description = "h2 DB username")
	private boolean h2NoInit = false;
	// JDBC driver name and database URL, use database server running in Docker container
	private static final String JDBC_DRIVER = "org.h2.Driver";
	private String dbUrl = "jdbc:h2:tcp://" + h2Host + ":" + h2Port + "/" + h2Table;
	private String dbUrlInMemo = "jdbc:h2:mem:";
	private Connection conn;

	public static void main(String[] args) {
		new CommandLine(new VocableTrainerApp()).execute(args);
	}

	@Override
	public void run() {
		EventQueue.invokeLater(() -> {
			try {
				// set up repository
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(h2InMemo ? dbUrlInMemo : dbUrl, h2User, h2Pass);
				VocableRepository vocableRepository = new H2VocableRepository(conn, h2Table);
				if (!h2NoInit)
					vocableRepository.initialize();
				SwingTrainerView swingTrainerView = new SwingTrainerView(); // call and test methods on this
				TrainerController trainerController = new TrainerController(vocableRepository, swingTrainerView);
				swingTrainerView.setTrainerController(trainerController);
				swingTrainerView.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
