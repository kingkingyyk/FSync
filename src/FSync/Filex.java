package FSync;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Filex extends File {
	private static final long serialVersionUID = -6953404807031383127L;
	private String checksum="";
	
	public Filex(String pathname) {
		super(pathname);
	}

	public void calculateChecksum() {
		try {
		    MessageDigest md = MessageDigest.getInstance("SHA1");
		    FileInputStream fis = new FileInputStream(this);
		    
		    byte[] dataBytes = new byte[1024];
		    int nread = 0;
		    while ((nread = fis.read(dataBytes)) != -1) md.update(dataBytes, 0, nread);
	
		    fis.close();
		    byte[] mdbytes = md.digest();
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    
		    this.checksum=sb.toString();
		} catch (Exception e) {}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Filex && this.checksum.equals(((Filex)o).checksum);
	}

}
