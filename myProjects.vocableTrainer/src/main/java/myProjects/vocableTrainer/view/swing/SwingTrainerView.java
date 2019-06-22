package myProjects.vocableTrainer.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.view.TrainerView;

public class SwingTrainerView extends JFrame implements TrainerView{

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingTrainerView frame = new SwingTrainerView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SwingTrainerView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	@Override
	public void showMessageVocableAdded(String message, Vocable vocable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showCheckResult(String message, boolean result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNextVocable(String message, Vocable vocable) {
		// TODO Auto-generated method stub
		
	}
}
