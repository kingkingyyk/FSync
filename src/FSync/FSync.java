package FSync;

import java.io.File;
import java.util.ArrayList;
import javax.swing.UIManager;

public class FSync {

	public static MainUI ui;
	
	public static void main (String [] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		 ui=new MainUI();
		ui.setLocationRelativeTo(null);
		ui.setVisible(true);
	}
	
	public static void sync (File src, File dest) throws Exception {
		ui.setStatus("Querying source files...");
		ArrayList<Filex> srcFiles=Utility.getFileList(src);
		ui.setStatus("Querying destination files...");
		ArrayList<Filex> destFiles=Utility.getFileList(dest);
		
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(srcFiles.size());
		int count=0;
		for (Filex f : srcFiles) {
			ui.setStatus("Processing source : "+f.getName());
			f.calculateChecksum();
			ui.setProgressBarValue(++count);
		}
		
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(destFiles.size());
		count=0;
		for (Filex f : destFiles) {
			ui.setStatus("Processing destination : "+f.getName());
			f.calculateChecksum();
			ui.setProgressBarValue(++count);
		}
		
		ArrayList<Filex> union=new ArrayList<>();
		union.addAll(destFiles);
		union.retainAll(srcFiles);
		
		ArrayList<Filex> toRemove=new ArrayList<>();
		toRemove.addAll(destFiles);
		toRemove.removeAll(srcFiles);
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(toRemove.size());
		count=0;
		for (Filex f : toRemove) {
			if (f.exists()) {
				ui.setStatus("Deleting : "+f.getName());
				Utility.recurseDelete(f);
			}
			ui.setProgressBarValue(++count);
		}
		
		ArrayList<Filex> toCopy=new ArrayList<>();
		toCopy.addAll(srcFiles);
		toCopy.removeAll(destFiles);
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(toCopy.size());
		count=0;
		for (Filex f : toCopy) {
			if (f.exists()) {
				ui.setStatus("Copying : "+f.getName());
				Utility.autoCopy(f,src,dest);
			}
			ui.setProgressBarValue(++count);
		}
		
		Utility.showInformationMessage("Ya ho! We have skipped copying "+union.size()+" folders & files!");
	}

}
