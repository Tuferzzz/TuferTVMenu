
package tufer.com.menutest.UIActivity.update;




public interface IService {

	public int getDownPos();


	public int getLength();


	public int getPackageSize();

	public Updater.HttpThread getHttpThread();

	public int isFinished();

	public int getmState();

	public void setmState(int a);

	public void setmSavePath(String _mSavePath);

	public String getmSavePath();

	public void setmUpgradeCode(String _mUpgradeCode);

	public String getmUpgradeCode();

	public void setmNewVersion(String _mNewVersion);

	public String getmNewVersion();

	public void deleteUpdateDB();

}
