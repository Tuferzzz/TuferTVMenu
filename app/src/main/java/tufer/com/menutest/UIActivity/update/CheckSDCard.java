package tufer.com.menutest.UIActivity.update;

import android.os.Environment;

import java.io.File;


public class CheckSDCard {


	public static boolean quickHasStorage() {
		System.out.println("Environment.MEDIA_MOUNTED:" + Environment.MEDIA_MOUNTED);
		System.out.println("Environment.getExternalStorageState():" + Environment.getExternalStorageState());
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}


	private static boolean checkFsWritable() {
		//String directoryName = Environment.getExternalStorageDirectory().toString() + "/mstara3";
		String directoryName = Environment.getExternalStorageDirectory().toString();
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}


	public static boolean hasStorage() {
		return hasStorage(true);
	}

	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

}
