package FSync;

import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Utility {

	private static void getFileListHelper (File folder, ArrayList<Filex> list) {
		list.add(new Filex(folder.getAbsolutePath()));
		if (folder.isDirectory()) for (File f : folder.listFiles()) getFileListHelper(f,list);
	}
	
	public static ArrayList<Filex> getFileList(File folder) {
		ArrayList<Filex> list=new ArrayList<>();
		getFileListHelper(folder,list);
		return list;
	}

	public static void recurseDelete (File folder) {
		if (folder.exists()) {
			if (folder.isDirectory()) for (File f : folder.listFiles()) recurseDelete(f);
			folder.delete();
		}
	}
	
	public static void autoCopy (File f, File src, File dest) throws Exception {
		StringBuilder newPath=new StringBuilder();
		newPath.append(dest.getAbsolutePath());
		newPath.append(f.getAbsolutePath().substring(src.getAbsolutePath().length(),f.getAbsolutePath().length()));
		File newFile=new File(newPath.toString());
		newFile.getParentFile().mkdirs();
		if (f.isDirectory()) newFile.mkdir();
		else Files.copy(Paths.get(f.getAbsolutePath()),Paths.get(newPath.toString()),StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void showErrorMessage (String text) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null,text,"FSync",JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showInformationMessage (String text) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null,text,"FSync",JOptionPane.INFORMATION_MESSAGE);
	}
}
