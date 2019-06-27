package myProjects.vocableTrainer.app;

import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import myProjects.vocableTrainer.controller.TrainerController;
import myProjects.vocableTrainer.repository.VocableRepository;
import myProjects.vocableTrainer.repository.h2.H2VocableRepository;
import myProjects.vocableTrainer.view.console.ConsoleTrainerView;
import myProjects.vocableTrainer.view.swing.SwingTrainerView;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class VocableTrainerApp implements Runnable {
	@Option(names = { "--console", "-c" }, description = "use console interface instead of GUI")
	private boolean consoleUI = false;
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
	@Option(names = { "--h2-in-memory", "-m" }, description = "h2 DB use in-memory DB")
	private boolean h2InMemo = false;
	@Option(names = { "--h2-no-init" }, description = "h2 DB do not initialize")
	private boolean h2NoInit = false;
	// JDBC driver name and database URL, use database server running in Docker container
	private static final String JDBC_DRIVER = "org.h2.Driver";
	private String dbUrl = "jdbc:h2:tcp://" + h2Host + ":" + h2Port + "/" + h2Table;
	private String dbUrlInMemo = "jdbc:h2:mem:";
	private Connection conn;

	// console i/o
	Scanner scanner = new Scanner(System.in);
	PrintStream outPrinter = new PrintStream(System.out);
	private boolean exitTesting = false;

	public static void main(String[] args) {
		new CommandLine(new VocableTrainerApp()).execute(args);
	}

	@Override
	public void run() {
		// set up repository
		try {
			// setting up the repository
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(h2InMemo ? dbUrlInMemo : dbUrl, h2User, h2Pass);
			VocableRepository vocableRepository = new H2VocableRepository(conn, h2Table);
			if (!h2NoInit)
				vocableRepository.initialize();
			// setting up and starting the UI and controller
			if (!consoleUI) { // GUI
				EventQueue.invokeLater(() -> {
					SwingTrainerView swingTrainerView = new SwingTrainerView();
					TrainerController trainerController = new TrainerController(vocableRepository, swingTrainerView);
					swingTrainerView.setTrainerController(trainerController);
					swingTrainerView.setVisible(true);
				});
			} else { // console UI
				ConsoleTrainerView console = new ConsoleTrainerView(scanner, outPrinter);
				TrainerController trainerController = new TrainerController(vocableRepository, console);
				console.setTrainerContr(trainerController);
				while (console.startConsole() && !exitTesting) {	// run until exit command
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// for stopping thread from test
	void stop() {	
		exitTesting = true;
	}
	// setting IO-ports and arguments from test
	ByteArrayOutputStream setIOArgs(Scanner scanner, String h2User, String h2Pass, String h2Table, String h2Port,
			String h2Host, boolean h2InMemo, boolean h2NoInit) {
		this.scanner = scanner;
		this.h2User = h2User;
		this.h2Pass = h2Pass;
		this.h2Table = h2Table;
		this.h2Port = h2Port;
		this.h2Host = h2Host;
		this.h2InMemo = h2InMemo;
		this.h2NoInit = h2NoInit;
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		outPrinter = new PrintStream(outputBuffer);
		return outputBuffer;
	}
}
