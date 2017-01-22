package covers1624.launchwrapper.digest;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by covers1624 on 2/16/2016.
 */
public class MD5SumCruncher {

	public static MD5Sum calculateFileSum(File file) {
		MD5Sum sum = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytesRead;
			byte[] smallBuffer = new byte[1024];
			while ((bytesRead = fileInputStream.read(smallBuffer)) != -1) {
				messageDigest.update(smallBuffer, 0, bytesRead);
			}
			fileInputStream.close();
			sum = new MD5Sum(messageDigest.digest());
		} catch (Exception e) {

		}
		if (sum == null) {
			sum = new MD5Sum("This is a blank MD5 Sum");
		}
		return sum;
	}
}
