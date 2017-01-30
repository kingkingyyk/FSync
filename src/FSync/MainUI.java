package FSync;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import FSync.FSync.FileIdentificationType;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

public class MainUI extends JFrame {
	private static final long serialVersionUID = 8100167928675856160L;
	private static final DecimalFormat doubleFormatter=new DecimalFormat("0.00%");
	private JPanel contentPane;
	private JTextField textFieldSource;
	private JTextField textFieldDestination;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private JComboBox<String> comboBoxIdentification;
	private JButton btnSource;
	private JButton btnDestination;
	private JButton btnSync;
	private JCheckBox chckbxFinalVerification;

	public MainUI() {
		setResizable(false);
		setTitle("FSync");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 184);
		setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSource = new JLabel("Source :");
		lblSource.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSource.setBounds(10, 11, 70, 14);
		contentPane.add(lblSource);
		
		textFieldSource = new JTextField();
		textFieldSource.setEnabled(false);
		textFieldSource.setBounds(90, 8, 235, 20);
		contentPane.add(textFieldSource);
		textFieldSource.setColumns(10);
		
		btnSource = new JButton("Browse...");
		btnSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc=new JFileChooser(textFieldSource.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(MainUI.this)==JFileChooser.APPROVE_OPTION) {
					textFieldSource.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnSource.setBounds(335, 7, 89, 23);
		contentPane.add(btnSource);
		
		JLabel lblDestination = new JLabel("Destination :");
		lblDestination.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDestination.setBounds(10, 42, 70, 14);
		contentPane.add(lblDestination);
		
		textFieldDestination = new JTextField();
		textFieldDestination.setEnabled(false);
		textFieldDestination.setColumns(10);
		textFieldDestination.setBounds(90, 39, 235, 20);
		contentPane.add(textFieldDestination);
		
		btnDestination = new JButton("Browse...");
		btnDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc=new JFileChooser(textFieldDestination.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showSaveDialog(MainUI.this)==JFileChooser.APPROVE_OPTION) {
					textFieldDestination.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnDestination.setBounds(335, 38, 89, 23);
		contentPane.add(btnDestination);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 101, 414, 23);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnSync = new JButton("Sync");
		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File src=new File(textFieldSource.getText());
				File dest=new File(textFieldDestination.getText());
				if (textFieldSource.getText().isEmpty() || !src.exists()) Utility.showErrorMessage("Source folder is not accessible.");
				else if (textFieldDestination.getText().isEmpty() || !dest.exists()) Utility.showErrorMessage("Destination folder is not accessible.");
				else if (src.equals(dest)) Utility.showErrorMessage("Source and destination must be different!");
				else {
					btnSync.setEnabled(false);
					Thread t=new Thread() {
						public void run () {
							try { FSync.sync(src,dest);
							} catch (Exception e) {
								e.printStackTrace();
								Utility.showErrorMessage("Error : "+e.getMessage());
							}
							btnSync.setEnabled(true);
							setProgressBarValue(0);
							setProgressBarMax(0);
							setStatus("Ready");
						}
					};
					t.start();
				}
			}
		});
		btnSync.setBounds(324, 129, 100, 23);
		contentPane.add(btnSync);
		
		lblStatus = new JLabel("Ready");
		lblStatus.setBounds(10, 133, 304, 14);
		contentPane.add(lblStatus);
		
		comboBoxIdentification = new JComboBox<>();
		comboBoxIdentification.setModel(new DefaultComboBoxModel<String>(new String[] {"SHA-512 Checksum", "Date Modified"}));
		comboBoxIdentification.setBounds(90, 70, 228, 20);
		contentPane.add(comboBoxIdentification);
		
		JLabel lblIdentification = new JLabel("Identification :");
		lblIdentification.setBounds(10, 73, 70, 14);
		contentPane.add(lblIdentification);
		
		chckbxFinalVerification = new JCheckBox("Final Verification");
		chckbxFinalVerification.setBounds(321, 71, 103, 23);
		contentPane.add(chckbxFinalVerification);
	}
	
	public void setStatus(String s) {
		lblStatus.setText(s);
	}
	
	public void setProgressBarMin (int v) {
		progressBar.setMinimum(v);
		progressBar.setString(doubleFormatter.format((double)progressBar.getValue()/progressBar.getMaximum()));
	}
	
	public void setProgressBarMax (int v) {
		v=Math.max(v,1);
		progressBar.setMaximum(v);
		progressBar.setString(doubleFormatter.format((double)progressBar.getValue()/progressBar.getMaximum()));
	}
	
	public void setProgressBarValue (int v) {
		progressBar.setValue(v);
		progressBar.setString(doubleFormatter.format((double)progressBar.getValue()/progressBar.getMaximum()));
	}
	
	public void setButtonsEnabled(boolean flag) {
		this.btnSource.setEnabled(flag);
		this.btnDestination.setEnabled(flag);
		this.comboBoxIdentification.setEnabled(flag);
		this.btnSync.setEnabled(flag);
		this.chckbxFinalVerification.setEnabled(flag);
	}
	
	public FileIdentificationType getIdentificationType() {
		return FileIdentificationType.values()[comboBoxIdentification.getSelectedIndex()];
	}
	
	public File getSourceFolder() {
		return new File(textFieldSource.getText());
	}
	
	public File getDestinationFolder() {
		return new File(textFieldDestination.getText());
	}
	
	public boolean hasFinalVerification() {
		return this.chckbxFinalVerification.isSelected();
	}
}
