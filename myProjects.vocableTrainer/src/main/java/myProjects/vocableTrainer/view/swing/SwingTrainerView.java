package myProjects.vocableTrainer.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import myProjects.vocableTrainer.model.Vocable;
import myProjects.vocableTrainer.view.TrainerView;
import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;

public class SwingTrainerView extends JFrame implements TrainerView{

	private JPanel contentPane;
	private JTextField phraseTxtField;
	private JTextField translationTxtField;
	private JTextField enterTextField;

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
		setMinimumSize(new Dimension(250, 250));
		setTitle("Vocable Trainer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 270);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel newVocablePanel = new JPanel();
		newVocablePanel.setName("newVocable");
		newVocablePanel.setBorder(new TitledBorder(null, "new vocable", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(newVocablePanel);
		GridBagLayout gbl_newVocablePanel = new GridBagLayout();
		gbl_newVocablePanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_newVocablePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_newVocablePanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_newVocablePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		newVocablePanel.setLayout(gbl_newVocablePanel);
		
		JLabel lblPhrase = new JLabel("phrase");
		lblPhrase.setName("newPhraseLabel");
		GridBagConstraints gbc_lblPhrase = new GridBagConstraints();
		gbc_lblPhrase.anchor = GridBagConstraints.EAST;
		gbc_lblPhrase.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhrase.gridx = 0;
		gbc_lblPhrase.gridy = 0;
		newVocablePanel.add(lblPhrase, gbc_lblPhrase);
		
		phraseTxtField = new JTextField();
		phraseTxtField.setName("newPhraseTextBox");
		GridBagConstraints gbc_phraseTxtField = new GridBagConstraints();
		gbc_phraseTxtField.gridwidth = 2;
		gbc_phraseTxtField.insets = new Insets(0, 0, 5, 0);
		gbc_phraseTxtField.fill = GridBagConstraints.HORIZONTAL;
		gbc_phraseTxtField.gridx = 1;
		gbc_phraseTxtField.gridy = 0;
		newVocablePanel.add(phraseTxtField, gbc_phraseTxtField);
		phraseTxtField.setColumns(10);
		
		JLabel lblTranslation = new JLabel("translation");
		lblTranslation.setName("newTranslationLabel");
		GridBagConstraints gbc_lblTranslation = new GridBagConstraints();
		gbc_lblTranslation.anchor = GridBagConstraints.EAST;
		gbc_lblTranslation.insets = new Insets(0, 0, 5, 5);
		gbc_lblTranslation.gridx = 0;
		gbc_lblTranslation.gridy = 1;
		newVocablePanel.add(lblTranslation, gbc_lblTranslation);
		
		translationTxtField = new JTextField();
		translationTxtField.setName("newTranslationTextBox");
		GridBagConstraints gbc_translationTxtField = new GridBagConstraints();
		gbc_translationTxtField.gridwidth = 2;
		gbc_translationTxtField.insets = new Insets(0, 0, 5, 0);
		gbc_translationTxtField.fill = GridBagConstraints.HORIZONTAL;
		gbc_translationTxtField.gridx = 1;
		gbc_translationTxtField.gridy = 1;
		newVocablePanel.add(translationTxtField, gbc_translationTxtField);
		translationTxtField.setColumns(10);
		
		JLabel lblAddMessage = new JLabel(" ");
		lblAddMessage.setName("newVocableMessageLabel");
		GridBagConstraints gbc_lblAddMessage = new GridBagConstraints();
		gbc_lblAddMessage.anchor = GridBagConstraints.EAST;
		gbc_lblAddMessage.gridwidth = 2;
		gbc_lblAddMessage.insets = new Insets(0, 0, 0, 5);
		gbc_lblAddMessage.gridx = 0;
		gbc_lblAddMessage.gridy = 2;
		newVocablePanel.add(lblAddMessage, gbc_lblAddMessage);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridx = 2;
		gbc_btnAdd.gridy = 2;
		newVocablePanel.add(btnAdd, gbc_btnAdd);
		
		JPanel checkVocablePanel = new JPanel();
		checkVocablePanel.setName("checkVocable");
		checkVocablePanel.setBorder(new TitledBorder(null, "check vocable", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(checkVocablePanel);
		GridBagLayout gbl_checkVocablePanel = new GridBagLayout();
		gbl_checkVocablePanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_checkVocablePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_checkVocablePanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_checkVocablePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		checkVocablePanel.setLayout(gbl_checkVocablePanel);
		
		JLabel lblShowTitle = new JLabel("translation");
		lblShowTitle.setName("checkShowTitleLabel");
		GridBagConstraints gbc_lblShowTitle = new GridBagConstraints();
		gbc_lblShowTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblShowTitle.anchor = GridBagConstraints.EAST;
		gbc_lblShowTitle.gridx = 0;
		gbc_lblShowTitle.gridy = 0;
		checkVocablePanel.add(lblShowTitle, gbc_lblShowTitle);
		
		JLabel lblShow = new JLabel(" ");
		lblShow.setName("checkShowLabel");
		GridBagConstraints gbc_lblShow = new GridBagConstraints();
		gbc_lblShow.gridwidth = 3;
		gbc_lblShow.insets = new Insets(0, 0, 5, 5);
		gbc_lblShow.gridx = 1;
		gbc_lblShow.gridy = 0;
		checkVocablePanel.add(lblShow, gbc_lblShow);
		
		JLabel lblEnter = new JLabel("phrase");
		lblEnter.setName("checkEnterLabel");
		GridBagConstraints gbc_lblEnter = new GridBagConstraints();
		gbc_lblEnter.anchor = GridBagConstraints.EAST;
		gbc_lblEnter.insets = new Insets(0, 0, 5, 5);
		gbc_lblEnter.gridx = 0;
		gbc_lblEnter.gridy = 1;
		checkVocablePanel.add(lblEnter, gbc_lblEnter);
		
		enterTextField = new JTextField();
		enterTextField.setName("checkEnterTextBox");
		GridBagConstraints gbc_enterTextField = new GridBagConstraints();
		gbc_enterTextField.gridwidth = 3;
		gbc_enterTextField.insets = new Insets(0, 0, 5, 5);
		gbc_enterTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_enterTextField.gridx = 1;
		gbc_enterTextField.gridy = 1;
		checkVocablePanel.add(enterTextField, gbc_enterTextField);
		enterTextField.setColumns(10);
		
		JLabel lblCheckMessage = new JLabel(" ");
		lblCheckMessage.setName("checkVocableMessageLabel");
		GridBagConstraints gbc_lblCheckMessage = new GridBagConstraints();
		gbc_lblCheckMessage.gridwidth = 2;
		gbc_lblCheckMessage.insets = new Insets(0, 0, 0, 5);
		gbc_lblCheckMessage.gridx = 0;
		gbc_lblCheckMessage.gridy = 2;
		checkVocablePanel.add(lblCheckMessage, gbc_lblCheckMessage);
		
		JButton btnCheck = new JButton("Check");
		btnCheck.setEnabled(false);
		GridBagConstraints gbc_btnCheck = new GridBagConstraints();
		gbc_btnCheck.insets = new Insets(0, 0, 0, 5);
		gbc_btnCheck.gridx = 2;
		gbc_btnCheck.gridy = 2;
		checkVocablePanel.add(btnCheck, gbc_btnCheck);
		
		JButton btnNext = new JButton("Next");
		btnNext.setEnabled(false);
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.gridx = 3;
		gbc_btnNext.gridy = 2;
		checkVocablePanel.add(btnNext, gbc_btnNext);
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
