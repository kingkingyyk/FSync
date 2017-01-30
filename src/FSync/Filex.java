package FSync;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Filex extends File {
	private static final long serialVersionUID = -6953404807031383127L;
	private String rootPath;
	
	public Filex(String pathname, String root) {
		super(pathname);
		this.rootPath=root;
	}

	public String calculateSHA() {
		try {
		    MessageDigest md = MessageDigest.getInstance("SHA-512");
		    FileInputStream fis = new FileInputStream(this);
		    
		    byte[] dataBytes = new byte[1024];
		    int nread = 0;
		    while ((nread = fis.read(dataBytes)) != -1) md.update(dataBytes, 0, nread);
	
		    fis.close();
		    byte[] mdbytes = md.digest();
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    
		    return sb.toString();
		} catch (Exception e) {}
		return "";
	}
	
	private String calculateChecksum() {
		if (FSync.ui.getIdentificationType()==FSync.FileIdentificationType.SHA1) {
			if (!this.isDirectory()) return calculateSHA();
			else return this.getAbsolutePath().substring(this.rootPath.length(),this.getAbsolutePath().length());
		} else {
			StringBuilder sb=new StringBuilder();
			if (!this.isDirectory()) sb.append(this.lastModified());
			sb.append("***");
			
			if (this.getPath().startsWith(FSync.ui.getSourceFolder().getPath()+File.separatorChar)) sb.append(this.getPath().substring(FSync.ui.getSourceFolder().getPath().length(),this.getPath().length()));
			else sb.append(this.getPath().substring(FSync.ui.getDestinationFolder().getPath().length(),this.getPath().length()));
			
			return sb.toString();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Filex && this.calculateChecksum().equals(((Filex)o).calculateChecksum());
	}

}
