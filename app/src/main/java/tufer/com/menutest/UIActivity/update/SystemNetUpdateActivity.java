
package tufer.com.menutest.UIActivity.update;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.FactoryManager;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;

import tufer.com.menutest.R;
import tufer.com.menutest.org.dtools.ini.BasicIniFile;
import tufer.com.menutest.org.dtools.ini.IniFile;
import tufer.com.menutest.org.dtools.ini.IniFileReader;
import tufer.com.menutest.org.dtools.ini.IniSection;


public class SystemNetUpdateActivity extends Activity {

	private UpdateService.ServiceBinder mServiceBinder;

	private final static String TAG = "SystemNetUpdateActivity";

	private final static String NAME = "share_pres";

	private final static String DOWNLOAD_ADDRESS = "url";

	public static Context activityContext;

	private final static String VERSION = "version";

	private final static int STATE_UPDATE_SUCCESS = 100;

	private final static int STATE_UPDATE_PROGRESS = 101;

	private final static int STATE_UPDATE_ERROR = 102;

	private final static int GET_VERSION_INFOR = 6;

	private final static int DOWNLOADING = 1000000001;



	private final String CFG_FILE_PATH = "/system/bin/version.ini";

	private final String RECOVERY_PATH = "/cache/recovery";

	private final String CACHE_PATH = "/cache/";

	private final String DATABASE_PATH_818 = "/data/data/com.android.settings/databases";

	private final String DATABASE_PATH_628 = "/data/data/com.haier.settings/databases";

	private String pathName = "";

	private ProgressDialog pBar;
	private Handler haveDownHandler = new Handler();

	//private final static String BROAD_ACTION = "SDCARD_ACTION";
	private final static String PERCENT = "percent";
	private final static String PERCENT_CHANGED = "percent_changed";

	private Button mBtnUpdate;

	private TextView mtxtUpdateInfo = null;

	private ListView inputSourceListView;

	private String mTxtInfo = "";

	private String mTxtInfo2 = "";

	private TextView title;

	private String updateInfor;

	private String systemVersion;

	private String newVersion;

	private boolean hasNewVersion = false;

	private boolean isHasStorage;

	private int mNumRetries;

	private boolean isDownloaded = false;
	private VersionInfor mInfor;

	private String downUrl;

	private String msg;

	public static String changeInfoEN;

	public static String changeInfoCH;

	public String locale = "";

	public static int otaLength;

	private LinearLayout mLayout;

	private LinearLayout mLayout_help;

	private ProgressBar mProgressBar;

	private TextView current_progress;

	private int mProgress = 0;

	private boolean isUpdating = false;

	private String size;

	private String mac;
	public static boolean check = false;
	private FactoryManager fm = TvManager.getInstance().getFactoryManager();
	//private Updater updater = null;
	//ini-------------
	public static String mUrl = "";
	private String mBrand = ""; 
	private String ChipPlatform = ""; 
	private String CUS = ""; 
	private String PANEL = ""; 
	private String DeviceType = "";
	//ini-------------

	private Updater mUpdater = null;
	private Handler mHandler = new Handler();
	private Handler mUpdateHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG,"mUpdateHandler = "+msg.what);
			switch (msg.what) {

			case STATE_UPDATE_SUCCESS:
				Log.d(TAG,"********* handleMessage UPDATE_SUCCESS");
				mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.updated));
				break;
			case STATE_UPDATE_PROGRESS:
				Log.d(TAG,"********* handleMessage STATE_UPDATE_PROGRESS");
				current_progress.setText(mProgress + "%");
				mProgressBar.setProgress(mProgress);
				if (mProgress == 100) {
					mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.check_success));
					mProgressBar.setVisibility(View.INVISIBLE);
					mLayout.setVisibility(View.INVISIBLE);
				}
				break;

			case STATE_UPDATE_ERROR:
				Log.d(TAG,"********* handleMessage STATE_UPDATE_ERROR");
				mtxtUpdateInfo.setText(R.string.msg_upgrade_err);
				mBtnUpdate.setEnabled(true);
				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);

				mProgressBar.setVisibility(View.INVISIBLE);
				mLayout.setVisibility(View.INVISIBLE);
				mServiceBinder.setmState(Updater.STATE_UPDATE_ERROR);
				break;

			default:
				break;
			}
		}
	};


	private boolean readConfig() {

		String pathFile ="/system/bin/version.ini";
		Log.d(TAG, "Updater.readConfig, inifile=" + pathFile);
		boolean flag = false;
		File file = new File(pathFile);
		IniFile ini = new BasicIniFile();
		IniFileReader reader = new IniFileReader(ini, file);
		try {
			reader.read();
			IniSection section = ini.getSection("MISC_SOFTWARE_UPGRADE");
			
			mUrl = section.getItem("url").getValue();
			mBrand = section.getItem("brand").getValue();
			ChipPlatform = section.getItem("ChipPlatform").getValue();
			CUS = section.getItem("CUS").getValue();
			PANEL = section.getItem("PANEL").getValue();
			DeviceType = section.getItem("DeviceType").getValue();
			Log.d(TAG,"  DeviceType:"+DeviceType+" PANEL:"+PANEL);
			flag = true;
		} catch (IOException e) {
			// exception thrown as an input\output exception occured
			e.printStackTrace();
		}
		if (!flag) {
			return false;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_net_update);
		check = false;
		readConfig();
		Intent intent = new Intent(SystemNetUpdateActivity.this, UpdateService.class);
		SystemNetUpdateActivity.this.bindService(intent, serviceConnection,Context.BIND_AUTO_CREATE);

		findViews();
		init();

		NetInfo net = new NetInfo(this);
		net.getNetInfo();
		mac = net.getMac();
		Log.d(TAG, "ip=" + net.getIpAddr());
		Log.d(TAG, "name=" + net.getNetName());
		Log.d(TAG, "mac=" + net.getMac());

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		registerReceiver(sdReceiver, intentFilter);


		pathName = SystemNetUpdateActivity.readFileByLines("/cache/Setttings_install.txt");
		Log.d(TAG, "onCreate.pathName = "+pathName);
		if (pathName != null && pathName.equals("ok")) {
			Log.d(TAG, "onCreate.ok->delete");
			new deleteUpgradeFileThread().start();
		}else if(readFileByAllLines("/cache/recovery/last_install")){
			Log.d(TAG, "onCreate.last_install->delete");
			new deleteUpgradeFileThread().start();
		}

		activityContext = this;

		otaLength = getOTASize();
		Log.d(TAG,"SystemNetUpdateActivity1.java otaLength = "+otaLength);
		registerListeners();

	}


	public void deleteUpgradeFile() {
		Log.d(TAG, "deleteUpgradeFile()");
	
		String cachePath1 = SystemNetUpdateActivity.readFileByLines("/cache/Setttings_ota.txt");
		Log.d(TAG, "deleteUpgradeTempFile().cachePath1:" + cachePath1);
		deletefile(cachePath1);

		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		if (sdcardPath != null && sdcardPath.length() > 0){
			if (!sdcardPath.endsWith("/")) {
			
				//String sdcardPath1 = sdcardPath +  "/" + DeviceType+PANEL+".ota.zip";
				String sdcardPath1 = sdcardPath +  "/" + DeviceType;
				Log.d(TAG, "deleteUpgradeTempFile().sdcardPath1:" + sdcardPath1);
				deletefile(sdcardPath1);
			}
		}
		delFolder(RECOVERY_PATH);
		delFolder(DATABASE_PATH_628);
		delFolder(DATABASE_PATH_818);
		deletefile("/cache/Setttings_install.txt");
		deletefile("/cache/Setttings_ota.txt");
		deletefile("/cache/Setttings_storage.txt");
	}



	ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mServiceBinder = (UpdateService.ServiceBinder) service;
			Log.d(TAG, "mServiceBinder.getmState()=======" + mServiceBinder.getmState());
			mUpdater = new Updater(mac, mServiceBinder);


			if (Updater.STATE_DOWNLOADING == mServiceBinder.getmState()) {
				mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
				mTxtInfo += getString(R.string.new_version);
				mTxtInfo += mServiceBinder.getmNewVersion() + "\n";
				mtxtUpdateInfo.setText(mTxtInfo);
				mLayout.setVisibility(View.VISIBLE);
				mLayout_help.setVisibility(View.VISIBLE);
				mBtnUpdate.setVisibility(View.VISIBLE);
				mBtnUpdate.setText(getString(R.string.download));
				mBtnUpdate.setEnabled(false);
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
			} else if (Updater.STATE_DOWNLOAD_OK == mServiceBinder.getmState()) {
				Log.d(TAG,"STATE_DOWNLOAD_OK->3");
				NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(DOWNLOADING);
				mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
				mTxtInfo += getString(R.string.new_version);
				mTxtInfo += mServiceBinder.getmNewVersion() + "\n";
				mLayout.setVisibility(View.VISIBLE);
				mLayout_help.setVisibility(View.VISIBLE);
				current_progress.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
				mtxtUpdateInfo.setVisibility(View.VISIBLE);
				mBtnUpdate.setVisibility(View.VISIBLE);
				current_progress.setText("100%");
				mProgressBar.setProgress(100);
				mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.msg_download_ok));

				mBtnUpdate.setText(getString(R.string.update));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
			} else {
				mUpdater.checkVersion();
			}
			mHandler = new Handler();
			mHandler.postDelayed(mRunable, 2000);

		}
	};

	@Override
	public void onBackPressed() {
		Log.d(TAG,"onBackPressed");
		check = true;
		super.onBackPressed();
		//yj.chen add back to menu
		Intent intent = new Intent("com.tecontv.back.to.menusetting");
		this.sendBroadcast(intent);

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		unbindService(serviceConnection);
		super.onDestroy();
		//  finish();
		unregisterReceiver(sdReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG,"onResume");
		mProgress = getPercentData(PERCENT);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		///if (isUpdating) {
		//	return true;
		//}

		//		if(keyCode == KeyEvent.KEYCODE_BACK)
		//		{
		//			Log.d(TAG,"yj.chen  KeyEvent.KEYCODE_BACK");
		//			mSettingsToolBar.showThisPage();
		//		}
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			Log.d(TAG,"yj.chen  KeyEvent.KEYCODE_MENU");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getOTASize(){
		int fileLen = 0;
		//String path="/cache/"+DeviceType+PANEL+".ota.zip";
		String path="/cache/"+DeviceType +".zip";
		try{
			File dF = new File(path); 
			FileInputStream fis; 
			fis = new FileInputStream(dF); 
			fileLen = fis.available();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		Log.d(TAG,"" +
				"" +
				"" +
				"()->fileLen = "+fileLen+"  ,path:"+path);
		return fileLen;
	}


	private void findViews() {

		mInfor = new VersionInfor();

		mtxtUpdateInfo = (TextView) findViewById(R.id.net_update_info);
		mtxtUpdateInfo.setText(getString(R.string.check_new_version));
		title = (TextView) findViewById(R.id.updateTitle);


		mBtnUpdate = (Button) findViewById(R.id.immediate);
		mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);


		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		current_progress = (TextView) findViewById(R.id.current_progress);
		mLayout = (LinearLayout) findViewById(R.id.show_progress);
		mLayout_help = (LinearLayout) findViewById(R.id.upgrade_help);

	}



	private void init(){
		Log.d(TAG,"init()");
		title.setText(getString(R.string.system_net_update));
		mTxtInfo = getString(R.string.current_version);
		mtxtUpdateInfo.setText(getString(R.string.check_new_version));
		mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
		mBtnUpdate.setVisibility(View.INVISIBLE);
	}


	private void registerListeners() {
		mBtnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int state = mServiceBinder.getmState();
				Log.d(TAG, "onClick, state=" + state);

				if (Updater.STATE_RECIECE_VER_OK == state) {
					// mLayout.setVisibility(View.INVISIBLE);
					mBtnUpdate.setEnabled(false);
					mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);

					Intent intent = new Intent(SystemNetUpdateActivity.this, UpdateService.class);
					SystemNetUpdateActivity.this.startService(intent);
					mHandler.postDelayed(mRunable, 2000);
				} else if (Updater.STATE_DOWNLOAD_OK == state) {
					Log.d(TAG,"STATE_DOWNLOAD_OK->2");
					mTxtInfo += getString(R.string.check_package);
					mtxtUpdateInfo.setText(mTxtInfo);
					// isDownloaded = false;
					mLayout.setVisibility(View.VISIBLE);
					mLayout_help.setVisibility(View.VISIBLE);
					current_progress.setText("");
					mProgressBar.setProgress(0);
					mProgressBar.setVisibility(View.VISIBLE);

					mBtnUpdate.setEnabled(false);
					mBtnUpdate.setText(getString(R.string.update));
					mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
					new UpdateTVOSThread().start();

				}else if (Updater.STATE_FILE_EXISTS == state) {
					Log.d(TAG,"STATE_FILE_EXISTS->2");
					mTxtInfo += getString(R.string.check_package);
					mtxtUpdateInfo.setText(mTxtInfo);
					// isDownloaded = false;
					mLayout.setVisibility(View.VISIBLE);
					mLayout_help.setVisibility(View.VISIBLE);
					current_progress.setText("");
					mProgressBar.setProgress(0);
					mProgressBar.setVisibility(View.VISIBLE);

					mBtnUpdate.setEnabled(false);
					mBtnUpdate.setText(getString(R.string.update));
					mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
					new UpdateTVOSThread().start();
				}else if (Updater.STATE_FILE_EXISTS_MD5_CHECK_ERR == state) {
					Log.d(TAG,"STATE_FILE_EXISTS_MD5_CHECK_ERR->2");
					mTxtInfo2 = getString(R.string.md5_check_error);
					mtxtUpdateInfo.setText(mTxtInfo2);
					// isDownloaded = false;
					mLayout.setVisibility(View.VISIBLE);
					mLayout_help.setVisibility(View.VISIBLE);
					current_progress.setText("");
					mProgressBar.setProgress(0);
					mProgressBar.setVisibility(View.VISIBLE);

					mBtnUpdate.setEnabled(false);
					mBtnUpdate.setText(getString(R.string.delete));
					mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
					//new deleteOTAThread().start();
					new deleteUpgradeFileThread().start();
				}else {
					Log.d(TAG,"mBtnUpdate.setOnClickListener else finish");
					//mSettingsToolBar.showThisPage();
					finish();
					Intent intent = new Intent("com.tecontv.back.to.menusetting");//yj.chen add
					activityContext.sendBroadcast(intent);
				}
			}
		});
	}



	private void updateTVOS() {
		if (vertify()) {
	
			System.out.println("********* vertify ok");
			mUpdateHandler.sendEmptyMessage(STATE_UPDATE_SUCCESS);
		} else {
			System.out.println("********* vertify err");
			mUpdateHandler.sendEmptyMessage(STATE_UPDATE_ERROR);
		}
	}



	private boolean vertify() {
		String pkgName = mServiceBinder.getmSavePath();
		Log.d(TAG, "vertify(), pkgName = " + pkgName);
		File file = new File(pkgName);

		try {
			ProgressListener progressListener = new ProgressListener() {

				@Override
				public void onProgress(int progress) {
					mProgress = progress;
					mUpdateHandler.sendEmptyMessage(STATE_UPDATE_PROGRESS);
				}
			};

			//FileDownloader.fileService.delete(FileDownloader.downloadUrl);
			mServiceBinder.deleteUpdateDB();

			RecoverySystem.verifyPackage(file, progressListener, null);
			System.out.println("+++++++++++verify+++++++++++");


			commitPercentValue(PERCENT, 0);
			Updater.writeStringToFile("ok", "/cache/Setttings_install.txt");

			System.out.println("begin install");
			try {
				fm.setEnvironmentPowerMode(EnumAcOnPowerOnMode.values()[2]);
			} catch (TvCommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RecoverySystem.installPackage(this, file);
			System.out.println("end install");

			isUpdating = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void commitURLValue(String key, String value) {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		edit.putString(key, value);
		edit.commit();
	}


	private BroadcastReceiver sdReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "sdcard action:::::" + action);
			///[2012-3-28 yanhd]update 
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				msg = getString(R.string.sdcard_insert) +  "\n";		
			} else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
				msg = getString(R.string.sdcard_remove) + "\n";
			}
			mtxtUpdateInfo.setText(msg);
			/*
			if (action.equals(BROAD_ACTION)) {
				msg = intent.getStringExtra("sd") + "\n";
				mtxtUpdateInfo.setText(msg);

			}
			 */
		}
	};

	private BroadcastReceiver percentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PERCENT_CHANGED)) {
				mProgress = intent.getIntExtra(PERCENT, 0);
				Log.d(TAG,"cur_progress:" + mProgress);
				//isDownLoad();
			}
		}
	};


	private int getPercentData(String key) {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preference.getInt(key, 0);
	}


	private void commitPercentValue(String key, int percent) {
		SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		edit.putInt(key, percent);
		edit.commit();
	}



	class UpdateTVOSThread extends Thread {
		@Override
		public void run() {
			super.run();
			updateTVOS();
		}
	}


	class deleteOTAThread extends Thread {
		@Override
		public void run() {
			super.run();
			deleteUpgradeTempFile();
		}
	}


	class deleteUpgradeFileThread extends Thread {
		@Override
		public void run() {
			super.run();
			deleteUpgradeFile();
		}
	}


	private long getCacheFreeSize() {

		StatFs sf = new StatFs("/cache");

		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		return availCount*blockSize;
	}


	private String FormetFileSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSize = "";
		if (size < 1024) {
			fileSize = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSize = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSize = df.format((double) size / 1048576) + "M";
		} else {
			fileSize = df.format((double) size / 1073741824) + "G";
		}
		return fileSize;
	}

	private void getProgress() {
		Log.d(TAG,"getProgress()");
		int progress = 0;
		int curr = mServiceBinder.getDownPos();
		int total = mServiceBinder.getLength();
		if (total > 0) {
			progress = (int) (curr * 100.0 / total);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getResources().getString(R.string.msg_downloading));
		sb.append(" (");
		sb.append(FormetFileSize(curr));
		sb.append(" / ");
		sb.append(FormetFileSize(total));
		sb.append(" )...");
		mBtnUpdate.setVisibility(View.VISIBLE);
		mLayout_help.setVisibility(View.VISIBLE);
		mLayout.setVisibility(View.VISIBLE);
		mtxtUpdateInfo.setVisibility(View.VISIBLE);
		current_progress.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		mtxtUpdateInfo.setText(mTxtInfo + sb.toString());
		current_progress.setText(progress + "%");
		mProgressBar.setProgress(progress);
	}

	private Runnable mRunable = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "check = " + check);
			if (check) {
				return;
			}

			//locale = Locale.getDefault().toString();
			locale = getResources().getConfiguration().locale.getCountry();

			boolean bPost = true;
			Log.d(TAG, "Runable.run()->locale = "+locale);
			int state = mServiceBinder.getmState();
			Log.d(TAG, "Runable.run().state=" + state);

			switch (state) {
			case Updater.STATE_NOTHING: {
				Log.e("chend", "Runable.run().state=" + state);
				break;
			}
			case Updater.STATE_REQUEST_VER: {
				Log.e("chend1", "Runable.run().state=" + state);
				break;
			}
			case Updater.STATE_READ_CFG_ERR: {
				Log.e("chend2", "Runable.run().state=" + state);

				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_readconfig_err);

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}
			case Updater.STATE_READ_MAC_ERR: {
				Log.e("chend3", "Runable.run().state=" + state);
		
				bPost = false;
				//mtxtUpdateInfo.setText(R.string.msg_readconfig_err);
				mtxtUpdateInfo.setText(R.string.msg_net_err);
				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}
			//jim.lei added in 0822
			case Updater.STATE_MAC_CHECK_ERR: {

				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_check_mac_err);
				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}		
			case Updater.STATE_VER_CHECK_ERR: {
			
				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_check_ver_err);
				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}			
			//end added			
			case Updater.STATE_RECIECE_VER_ERR: {
		
				bPost = false;
				//mtxtUpdateInfo.setText(R.string.msg_version_newest);
				mtxtUpdateInfo.setText(R.string.msg_net_err);

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}
			case Updater.STATE_RECIECE_VER_OK: {
			
				bPost = false;
				mTxtInfo += mServiceBinder.getmUpgradeCode() + "\n";
				mTxtInfo += getString(R.string.new_version);
				mTxtInfo += mServiceBinder.getmNewVersion() + "\n";
				String newStr = "";
				if(locale.equals("CN")) {
					try{
						if(changeInfoCH != null){
							newStr = new String(changeInfoCH.getBytes(), "UTF-8");
						}
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
				}else if(locale.equals("US")){
					if(changeInfoEN != null){
						newStr = new String(changeInfoEN.getBytes());
					}
				}

				String[] splitStr = newStr.split("##");
				for(int i=0 ; i < splitStr.length ; i++){         
					mTxtInfo += splitStr[i] + "\n";;
				}

				mtxtUpdateInfo.setText(mTxtInfo);

				mBtnUpdate.setText(getString(R.string.download));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.setVisibility(View.VISIBLE);

				mLayout.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(0);
				mProgressBar.setVisibility(View.VISIBLE);

				mLayout_help.setVisibility(View.VISIBLE);
				break;
			}
			case Updater.STATE_NO_PACKAGE: {
		
				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_version_newest);

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}
			case Updater.STATE_VER_IS_NEWEST: {
		
				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_version_newest);

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.setVisibility(View.VISIBLE);
				break;
			}
			case Updater.STATE_DOWNLOADING: {
		
				getProgress();
				break;
			}
			case Updater.STATE_DOWNLOAD_ERR: {
		
				bPost = false;
				mtxtUpdateInfo.setText(R.string.msg_download_err);

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				break;

			}
			case Updater.STATE_DOWNLOAD_OK: {
			
				bPost = false;
				current_progress.setText("100%");
				mProgressBar.setProgress(100);
				mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.msg_download_ok));

				mBtnUpdate.setText(getString(R.string.update));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				break;
			}
			//jim.lei added in 0925
			case Updater.STATE_FILE_EXISTS:{
			
				Log.d(TAG,"STATE_FILE_EXISTS");
				bPost = false;
				NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(DOWNLOADING);           
				mTxtInfo += Updater.curVersionExits + "\n";
				mTxtInfo += getString(R.string.new_version);
				mTxtInfo += Updater.serverVersionExits + "\n";
				String newStr = "";                

				if(locale.equals("CN")) {
					try{
						newStr = new String(changeInfoCH.getBytes(), "UTF-8");
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
				}else if(locale.equals("US")){
					newStr = new String(changeInfoEN.getBytes());
				}

				String[] splitStr = newStr.split(" ");
				for(int i=0 ; i < splitStr.length ; i++){         
					mTxtInfo += splitStr[i] + "\n";;
				}                

				mtxtUpdateInfo.setText(mTxtInfo);                
				mLayout.setVisibility(View.VISIBLE);
				mLayout_help.setVisibility(View.VISIBLE);
				current_progress.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
				mtxtUpdateInfo.setVisibility(View.VISIBLE);
				mBtnUpdate.setVisibility(View.VISIBLE);
				current_progress.setText("100%");
				mProgressBar.setProgress(100);
				//                mtxtUpdateInfo.setText(mTxtInfo + getString(R.string.msg_download_ok));

				mBtnUpdate.setText(getString(R.string.update));
				mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
				break;
			}
			case Updater.STATE_FILE_EXISTS_MD5_CHECK_ERR:{
				Log.e("chend14", "Runable.run().state=" + state);
				Log.d(TAG,"STATE_FILE_EXISTS_MD5_CHECK_ERR");
				bPost = false;
				NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(DOWNLOADING);           
				mTxtInfo2 += getString(R.string.md5_check_error);

				mtxtUpdateInfo.setText(mTxtInfo2);                
				//mLayout.setVisibility(View.VISIBLE);
				mLayout_help.setVisibility(View.VISIBLE);
				//current_progress.setVisibility(View.VISIBLE);
				//mProgressBar.setVisibility(View.VISIBLE);
				mtxtUpdateInfo.setVisibility(View.VISIBLE);
				mBtnUpdate.setVisibility(View.VISIBLE);
				//current_progress.setText("100%");
				//mProgressBar.setProgress(100);
				mBtnUpdate.setText(getString(R.string.delete));
				mBtnUpdate.setBackgroundResource(R.drawable.button_bg);
				break;
			}			
			//end added			
			case Updater.STATE_NOT_ENOUGH_STORAGE: {
				Log.e("chend15", "Runable.run().state=" + state);
				bPost = false;

				String storageFlag = readFileByLines("/cache/Setttings_storage.txt");
				Updater.writeStringToFile("null","/cache/Setttings_storage.txt");

				Log.d(TAG, "SystemNetUpdateActivity1.storageFlag:" + storageFlag);
				Log.d(TAG, "SystemNetUpdateActivity1.IsSdcard:" + Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));

				if (storageFlag != null && storageFlag.equals("cache")) {
					mtxtUpdateInfo.setText(R.string.err_not_enough_storage_for_cache);
				} else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
						&& storageFlag != null && storageFlag.equals("sdcard")) {
					mtxtUpdateInfo.setText(R.string.err_not_enough_storage_for_sdcard);
				} else {
					mtxtUpdateInfo.setText(R.string.err_not_enough_storage);
				}

				mBtnUpdate.setText(getString(R.string.exit));
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				break;
			}
			}

			if (bPost) {
				mHandler.postDelayed(mRunable, 2000);
			} else {
				mBtnUpdate.setEnabled(true);
				mBtnUpdate.setBackgroundResource(R.drawable.channel_tuning_confirm);
				mBtnUpdate.requestFocus();
			}
		}
	};

	//jim.lei added in 1218
	public void deleteUpgradeTempFile() {

		String cachePath1 = Updater.CACHE_PATH + DeviceType + ".zip";
		//String cachePath1 = Updater.CACHE_PATH + Updater.OTA_NAME + Updater.TMP_SUFFIX;
		Log.d(TAG, "deleteUpgradeTempFile().cachePath1:" + cachePath1);
		deletefile(cachePath1);

		String cachePath2 = Updater.CACHE_PATH+ DeviceType + ".zip";
		Log.d(TAG, "deleteUpgradeTempFile().cachePath2:" + cachePath2);
		deletefile(cachePath2);

		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		if (sdcardPath != null && sdcardPath.length() > 0){
			if (!sdcardPath.endsWith("/")) {
		
				String sdcardPath1 = sdcardPath +  "/" + DeviceType+".zip";
				//String sdcardPath1 = sdcardPath +  "/" + Updater.OTA_NAME + Updater.TMP_SUFFIX;
				Log.d(TAG, "deleteUpgradeTempFile().sdcardPath1:" + sdcardPath1);
				deletefile(sdcardPath1);

	
				String sdcardPath2  = sdcardPath + "/" + DeviceType+".zip";
				Log.d(TAG, "deleteUpgradeTempFile().sdcardPath2:" + sdcardPath2);
				deletefile(sdcardPath2);
			}
		}
		finish();
	}

	private void deletefile(String deletePath) {

		Log.d(TAG, "deletefile().deletefile:" + deletePath);
		if(deletePath == null){
			mServiceBinder.setmState(Updater.STATE_VER_IS_NEWEST);
			return;
		}
		File file = new File(deletePath);


		Log.d(TAG, "deletefile().file.isFile():" + file.isFile());
		Log.d(TAG, "deletefile().file.exists():" + file.exists());

		if (file.isFile() && file.exists()) {
	
			file.delete();
		}
	}

	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); 
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete();
		}
		catch (Exception e) {
		
			e.printStackTrace();
		}
	}


	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			}
			else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path+"/"+ tempList[i]);
				delFolder(path+"/"+ tempList[i]);
			}
		}
	}
	//end added


	public static String readFileByLines(String path) {
		Log.d(TAG, "SystemNetUpdateActivity1.readFileByLines().path:" + path);
		File file = new File(path);
		BufferedReader reader = null;
		String context = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				Log.d(TAG, "SystemNetUpdateActivity1.readFileByLines():" + tempString);
				context = tempString;
			}
			reader.close();
		} catch (IOException e) {
			//e.printStackTrace();
			Log.d(TAG, "SystemNetUpdateActivity1.readFileByLines().IOException:" + e.getLocalizedMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return context;
	}

	public boolean readFileByAllLines(String path) {
		Log.d(TAG, "readFileByAllLines().path:" + path);
		File file = new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				Log.d(TAG, "readFileByAllLines():" + tempString);
				if(tempString != null && "1".equals(tempString))
					return true;
			}
			reader.close();
		} catch (IOException e) {
			//e.printStackTrace();
			Log.d(TAG, "readFileByAllLines().IOException:" + e.getLocalizedMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return false;
	}
}
