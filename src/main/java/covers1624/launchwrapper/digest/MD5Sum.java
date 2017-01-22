package covers1624.launchwrapper.digest;


/**
 * Created by covers1624 on 2/15/2016.
 */
public class MD5Sum {

	private String hexString;

	public MD5Sum(byte[] hexBytes) {
		StringBuilder stringBuilder = new StringBuilder();
		for (byte hexByte : hexBytes) {
			stringBuilder.append(Integer.toString((hexByte & 0xFF) + 0x100, 16).substring(1));
		}
		hexString = stringBuilder.toString();
	}

	public MD5Sum(String hexString) {
		this.hexString = hexString;
	}

	public String getHexString() {
		return hexString;
	}

    @Override
    public String toString() {
        return getHexString().toUpperCase();
    }

    @Override
	public boolean equals(Object obj) {
		if (obj instanceof MD5Sum) {
			MD5Sum sum = (MD5Sum) obj;
			return sum.toString().equals(toString());
		}
		return false;
	}
}
