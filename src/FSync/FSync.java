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
				ui.setStatus("Deleting "+f.getName());
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
		ui.setProgressBarMax(srcFilesPath.size());
		
		long copySkippedCount=0;
		for (String s : srcFilesPath) {
			Filex srcF=new Filex(src.getAbsolutePath()+s,src.getAbsolutePath());
			Filex destF=new Filex(dest.getAbsolutePath()+s,dest.getAbsolutePath());
			ui.setStatus("Comparing "+destF.getName());
			if (!destF.exists() || (destF.exists() && !srcF.equals(destF))) { //same file path but different checksum
				ui.setStatus("Deleting "+destF.getName());
				destF.delete(); 
				toCopyFiles.add(s);
			} else copySkippedCount+=srcF.length();
			ui.setProgressBarValue(++barCurr);
		}
		
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(toCopyFiles.size());
		
		Collections.sort(toCopyFiles,Collections.reverseOrder());
		
		int copiedCount=0;
		for (String s : toCopyFiles) {
			File srcF=new File(src.getPath()+s);
			ui.setStatus("Copying "+srcF.getName());
			Utility.autoCopy(srcF, src, dest);
			ui.setProgressBarValue(++copiedCount);
		}
		return copySkippedCount; 
		//=================================================
	}
	
	public static boolean verify (File src, File dest) {
		ui.setStatus("Querying source files...");
		HashSet<String> srcFilesPath=new HashSet<>();
		for (String srcFilesP : Utility.getFileListPath(src)) srcFilesPath.add(srcFilesP.substring(src.getAbsolutePath().length(),srcFilesP.length()));
		
		ui.setStatus("Querying destination files...");
		HashSet<String> destFilesPath=new HashSet<>();
		for (String destFilesP : Utility.getFileListPath(dest)) destFilesPath.add(destFilesP.substring(dest.getAbsolutePath().length(),destFilesP.length()));
		
		HashSet<String> difference=new HashSet<>();
		difference.addAll(srcFilesPath);
		difference.removeAll(destFilesPath);
		if (difference.size()!=0) return false;
		
		ui.setProgressBarValue(0);
		ui.setProgressBarMax(srcFilesPath.size());
		int progBarValue=0;
		for (String s : srcFilesPath) {
			Filex srcF=new Filex(src.getAbsolutePath()+s,src.getAbsolutePath());
			Filex destF=new Filex(dest.getAbsolutePath()+s,dest.getAbsolutePath());
			ui.setStatus("Verifying "+destF.getName());
			if (!destF.exists() || !srcF.calculateSHA().equals(destF.calculateSHA())) return false;
			ui.setProgressBarValue(++progBarValue);
		}
		return true;
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
		if (!ui.hasFinalVerification() || (ui.hasFinalVerification() && verify(src,dest)))
			Utility.showInformationMessage("Ya ho! We have skipped copying "+formatByte(skip)+"!");
		else Utility.showInformationMessage("Checksum error! You may try sync again.\nIf the problem still presists, there might be problem with your storage device.");
		
		ui.setButtonsEnabled(true);
		
		ui.setProgressBarMax(1);
		ui.setProgressBarValue(0);
	}

}
