package FSync;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.UIManager;

public class FSync {

	public static MainUI ui;
	public static enum FileIdentificationType {SHA1,MODIFIEDDATE};

	public static void main (String [] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		 ui=new MainUI();
		ui.setLocationRelativeTo(null);
		ui.setVisible(true);
	}
	
	public static void compareAndDeleteDest (File src, File dest) {
		//========= Compare and delete files in destination
		ui.setStatus("Querying source files...");
		ArrayList<String> srcFilesPath=new ArrayList<>();
		for (String srcFilesP : Utility.getFileListPath(src)) srcFilesPath.add(srcFilesP.substring(src.getAbsolutePath().length(),srcFilesP.length()));
		srcFilesPath.get(0);
		
		ui.setStatus("Querying destination files...");
		ArrayList<String> destFilesPath=new ArrayList<>();
		for (String destFilesP : Utility.getFileListPath(dest)) destFilesPath.add(destFilesP.substring(dest.getAbsolutePath().length(),destFilesP.length()));
		destFilesPath.get(0);
		
		ui.setStatus("Deleting redundant files...");
		ArrayList<String> toRemoveFiles=new ArrayList<>();
		toRemoveFiles.addAll(destFilesPath);
		toRemoveFiles.removeAll(srcFilesPath);
		Collections.reverse(toRemoveFiles);

		ui.setProgressBarValue(0);
		ui.setProgressBarMax((int)toRemoveFiles.parallelStream().mapToLong(file -> file.length()).sum());
		int count=0;
		for (String s : toRemoveFiles) {
			File f=new File(dest.getAbsolutePath()+s);
			if (f.exists()) {
				ui.setStatus("Deleting "+s);
				count+=Utility.recurseDelete(f);
				ui.setProgressBarValue((int)count);
			}
		}
		//=================================================
	}
	
	public static long compareAndCopy (File src, File dest) throws Exception {
		//========= Compare and copy files in destination
		ui.setStatus("Querying source files...");
		HashSet<String> srcFilesPath=new HashSet<>();
		for (String srcFilesP : Utility.getFileListPath(src)) srcFilesPath.add(srcFilesP.substring(src.getAbsolutePath().length(),srcFilesP.length()));
		srcFilesPath.remove("");
		
		ui.setStatus("Querying destination files...");
		HashSet<String> destFilesPath=new HashSet<>();
		for (String destFilesP : Utility.getFileListPath(dest)) destFilesPath.add(destFilesP.substring(dest.getAbsolutePath().length(),destFilesP.length()));
		destFilesPath.remove("");
		
		ui.setStatus("Preparing file transfers...");
		ArrayList<String> toCopyFiles=new ArrayList<>();

		int barCurr=0;
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(toCopyFiles.size());
		
		long copyMaxCount=0;
		long copySkippedCount=0;
		for (String s : srcFilesPath) {
			Filex srcF=new Filex(src.getAbsolutePath()+s,src.getAbsolutePath());
			Filex destF=new Filex(dest.getAbsolutePath()+s,dest.getAbsolutePath());
			ui.setStatus("Checking "+destF.getAbsolutePath());
			if (!destF.exists() || (destF.exists() && !srcF.equals(destF))) { //same file path but different checksum
				ui.setStatus("Deleting "+s);
				destF.delete(); 
				toCopyFiles.add(s);
				copyMaxCount+=srcF.length();
			} else copySkippedCount+=srcF.length();
			ui.setProgressBarValue(++barCurr);
		}
		
		ui.setProgressBarValue(0);
		ui.setProgressBarMax((int)copyMaxCount);
		
		Collections.sort(toCopyFiles,Collections.reverseOrder());
		
		long copiedCount=0;
		for (String s : toCopyFiles) {
			File srcF=new File(src.getPath()+s);
			copiedCount+=srcF.length();
			ui.setStatus("Copying "+s);
			Utility.autoCopy(srcF, src, dest);
			ui.setProgressBarValue((int)copiedCount);
		}
		return copySkippedCount; 
		//=================================================
	}
	
	public static String formatByte (long l) {
		double value=(double)l;
		String [] unit={"bytes","KB","MB","GB","TB","PB"};
		int unitIndex=0;
		for (;unitIndex<unit.length && value>=1024;unitIndex++) value/=1024;
		return String.format("%.2f",value)+unit[unitIndex];
	}
	
	public static void sync (File src, File dest) throws Exception {
		ui.setButtonsEnabled(false);
		compareAndDeleteDest(src,dest);
		long skip=compareAndCopy(src,dest);
		
		ui.setButtonsEnabled(true);
		Utility.showInformationMessage("Ya ho! We have skipped copying "+formatByte(skip)+"!");
		
		ui.setProgressBarMax(1);
		ui.setProgressBarValue(0);
	}

}
