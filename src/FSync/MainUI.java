package FSync;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class MainUI extends JFrame {
	private static final long serialVersionUID = 8100167928675856160L;
	private JPanel contentPane;
	private JTextField textFieldSource;
	private JTextField textFieldDestination;
	private JProgressBar progressBar;
	private JLabel lblStatus;

	public MainUI() {
		setTitle("FSync");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 168);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSource = new JLabel("Source :");
		lblSource.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSource.setBounds(10, 11, 61, 14);
		contentPane.add(lblSource);
		
		textFieldSource = new JTextField();
		textFieldSource.setEnabled(false);
		textFieldSource.setBounds(81, 8, 244, 20);
		contentPane.add(textFieldSource);
		textFieldSource.setColumns(10);
		
		JButton btnSource = new JButton("Browse");
		btnSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					textFieldSource.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnSource.setBounds(335, 7, 89, 23);
		contentPane.add(btnSource);
		
		JLabel lblDestination = new JLabel("Destination :");
		lblDestination.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDestination.setBounds(10, 42, 61, 14);
		contentPane.add(lblDestination);
		
		textFieldDestination = new JTextField();
		textFieldDestination.setEnabled(false);
		textFieldDestination.setColumns(10);
		textFieldDestination.setBounds(81, 39, 244, 20);
		contentPane.add(textFieldDestination);
		
		JButton buttonDestination = new JButton("Browse");
		buttonDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					textFieldDestination.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		buttonDestination.setBounds(335, 38, 89, 23);
		contentPane.add(buttonDestination);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 71, 414, 23);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		JButton btnSync = new JButton("Sync");
		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File src=new File(textFieldSource.getText());
				File dest=new File(textFieldDestination.getText());
				if (textFieldSource.getText().isEmpty() || !src.exists()) Utility.showErrorMessage("Source folder is not accessible.");
				else if (textFieldDestination.getText().isEmpty() || !dest.exists()) Utility.showErrorMessage("Destination folder is not accessible.");
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
		btnSync.setBounds(324, 101, 100, 23);
		contentPane.add(btnSync);
		
		lblStatus = new JLabel("Ready");
		lblStatus.setBounds(10, 105, 304, 14);
		contentPane.add(lblStatus);
	}
	
	public void setStatus(String s) {
		lblStatus.setText(s);
	}
	
	public void setProgressBarMin (int v) {
		progressBar.setMinimum(v);
		progressBar.setString(String.format("%.2f%%",Math.max(0,progressBar.getPercentComplete()*100)));
	}
	
	public void setProgressBarMax (int v) {
		progressBar.setMaximum(v);
		progressBar.setString(String.format("%.2f%%",Math.max(0,progressBar.getPercentComplete()*100)));
	}
	
	public void setProgressBarValue (int v) {
		progressBar.setValue(v);
		progressBar.setString(String.format("%.2f%%",Math.max(0,progressBar.getPercentComplete()*100)));
	}

}
