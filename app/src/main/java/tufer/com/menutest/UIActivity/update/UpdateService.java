
package tufer.com.menutest.UIActivity.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tufer.com.menutest.R;
import tufer.com.menutest.org.dtools.ini.BasicIniFile;
import tufer.com.menutest.org.dtools.ini.IniFile;
import tufer.com.menutest.org.dtools.ini.IniFileReader;
import tufer.com.menutest.org.dtools.ini.IniSection;


public class UpdateService extends Service {

    private  String mUpgradeCode = ""; 
    private String mNewVersion = "";
    
    private static String mSavePath = ""; 
    
    private static Context intentContext;

    private final static int TASK_DOWN_PACKAGE = 2; 

    private  int mState = Updater.STATE_NOTHING;
    
    public static FileDownloader loader;

    private final  String OTA_NAME = ".zip";
    
    private static final String CACHE_PATH = "/cache/";

    //private final static String TMP_SUFFIX = ".tmp";

    private HttpThread mHttp = null; 

    private String mMd5 = ""; /* md5 */

    private final static String TAG = "UpdateService";

    private ServiceBinder sBinder;

    private final static int DOWNLOADING = 1000000001;

    private Notification notification;

    private NotificationManager notificationManager;

    private PendingIntent contentIntent;

    private RemoteViews mRemoteViews;

    private int percent=-1;
//jim.lei added in 0923
    FileAccess fileAccess = null; //File Access interface
	private int downloadSize = 0;
	private File saveFile;
	private int mTaskType = TASK_DOWN_PACKAGE; 
	private static boolean isNeedBreakPointPlay = false;
	private int breakPoint = 0;
//end added    

    //ini-------------
    public static String mUrl = ""; 
    private String mBrand = ""; 
    private String ChipPlatform = "";
    private String CUS = ""; 
    private String PANEL = ""; 
    private String DeviceType = "";
  //ini-------------
    
    private VersionInfor mInfor;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG,"mHandler msg " + msg.what);
            
            if(msg.what == 100) {
                showNotification(R.drawable.one_px, R.string.upgrade_package_download_finished, msg.what);
            }
            if (msg.what <=100) {
                mRemoteViews.setTextViewText(R.id.task_percent, msg.what + "%");
                mRemoteViews.setProgressBar(R.id.task_progressbar, 100, msg.what, false);
                notification.contentView = mRemoteViews;
                notification.contentIntent = contentIntent;
                notificationManager.notify(DOWNLOADING, notification);
            }    
        };
    };


    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readConfig();
        sBinder = new ServiceBinder();
        mInfor = new VersionInfor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    

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

    

	protected synchronized void append(int size) {
		downloadSize += size;
	}

	protected synchronized void update(int pos) {

	}
    
	
    /**
     * start download the update package
     */
	
	public int startUpdate(){
		Log.d(TAG, "UpdateService->download(1)->breakPoint = "+breakPoint);
        breakPoint = SystemNetUpdateActivity.otaLength;
        Log.d(TAG, "UpdateService->download(2)->breakPoint = "+breakPoint);
		downloadPackage(Updater.mDownloadUrl,this, saveFile,breakPoint);
		return downloadSize;
	}
	
    public synchronized void deleteUpgradeTempFile() {

        String cachePath1 = CACHE_PATH + DeviceType+OTA_NAME;
        //String cachePath1 = CACHE_PATH + OTA_NAME + TMP_SUFFIX;
        deletefile(cachePath1);
        

        String cachePath2 = CACHE_PATH +DeviceType+ OTA_NAME;
        deletefile(cachePath2);
        
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        if (sdcardPath != null && sdcardPath.length() > 0){
            if (!sdcardPath.endsWith("/")) {
      
                //String sdcardPath1 = sdcardPath +  "/" + OTA_NAME + TMP_SUFFIX;
                String sdcardPath1 = sdcardPath +  "/" + DeviceType+OTA_NAME;
                deletefile(sdcardPath1);
                
     
                String sdcardPath2  = sdcardPath + "/" + DeviceType+OTA_NAME;
                deletefile(sdcardPath2);
            }
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
    
    
    private synchronized void deletefile(String deletePath) {
        Log.d(TAG, "deletefile().deletefile:" + deletePath);
        File file = new File(deletePath);
  
        Log.d(TAG, "deletefile().file.isFile():" + file.isFile());
        Log.d(TAG, "deletefile().file.exists():" + file.exists());
        if (file.isFile() && file.exists()) {
       
            file.delete();
        }
    }
//end added    
    
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       Log.d(TAG,"UpdateService-downUrl:" + Updater.mUrl);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification(R.drawable.one_px, R.string.update_packages_download, 0);
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessage(msg);
//jim.lei added in 0923
        try{
//        	saveFile = createFile();
        	saveFile = createMultiDownFile();
        }catch(IOException e){
        	e.printStackTrace();
        }
//end added
		//startUpdate();
		mulThreadDownloader(Updater.mDownloadUrl,saveFile);
        return super.onStartCommand(intent, flags, startId);
    }


   
    private void showNotification(int drawbale, int titleId, int percent) {
    	Log.d(TAG,"showNotification percent="+percent);
        notification = new Notification(drawbale, getString(titleId),
                System.currentTimeMillis());
        mRemoteViews = new RemoteViews(getApplication().getPackageName(),
                R.layout.download_progress);
        Intent intent = new Intent();
        if (percent == 100) {
          //  intent.setClass(this, SystemNetUpdateViewHolder.class);
            Log.d(TAG,"showNotification 100");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }



//jim.lei added in 1231   
    // interface for service and activity.

    public class ServiceBinder extends Binder implements IService {

        @Override
        public int getDownPos() {
        	return downloadSize;
        }

        @Override
        public int getLength() {
        	return Updater.fileSize;
        }

        @Override
        public int getPackageSize() {
            if (null != mHttp) {
                return mHttp.getPackageSize();
            } else {
                return 0;
            }
        }

        @Override
        public Updater.HttpThread getHttpThread() {
            return null;
        }

        @Override
        public int isFinished() {
            return 0;
        }

        @Override
        public int getmState() {
            return mState;

        }

        @Override
        public void setmState(int a) {
            mState = a;
        }

        @Override
        public void setmSavePath(String _mSavePath) {
                mSavePath=_mSavePath;
            
        }

        @Override
        public String getmSavePath() {
            return mSavePath+DeviceType+OTA_NAME;
        }

        @Override
        public void setmUpgradeCode(String _mUpgradeCode) {
            mUpgradeCode=_mUpgradeCode;
        }

        @Override
        public String getmUpgradeCode() {
            return mUpgradeCode;
        }

        @Override
        public void setmNewVersion(String _mNewVersion) {
            mNewVersion=_mNewVersion;
        }

        @Override
        public String getmNewVersion() {
            return mNewVersion;
        }

        @Override
        public void deleteUpdateDB() {
        	if (loader != null)
        	loader.deleteUpdateDB();
        }

    }    
    
    
    
    public void mulThreadDownloader(final String path, final File savedir) {
        if (mState != Updater.STATE_RECIECE_VER_OK) {
            return;
        }
    	new Thread(new Runnable() {
			@Override
			public void run() {
				loader = new FileDownloader(SystemNetUpdateActivity.activityContext, path, savedir,Updater.DOWNLOAD_THREAD_NUM);
				try {
					loader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {
	                        if (Updater.fileSize > 0) {
	                        	downloadSize = size;
	                            int progress = (int) (size * 100.0 / Updater.fileSize);    
	                             if(percent!=progress){
	                            	 Log.d(TAG,"Updater.fileSize = "+Updater.fileSize+" size = "+size+"  progress = "+progress+"  percent = "+percent);
	                                 percent=progress;
	                                 Message msg = mHandler.obtainMessage();
	                                 msg.what = progress;
	                                 mHandler.sendMessage(msg);
	                             }
	                        }
							 Log.d("yesuo","size="+ size + "   downloadSize"+ downloadSize +"  Updater.fileSize=" + Updater.fileSize);
							if(size == Updater.fileSize){
				                Log.d(TAG, "synchronized...");
				                //mFinish = true;
				                if (mTaskType == TASK_DOWN_PACKAGE) {
				                  if (Updater.fileSize > 0 && size == Updater.fileSize) {                	
				                        if (checkFile(saveFile)) {
				                            mState = Updater.STATE_DOWNLOAD_OK;
				                        } else {
				                            mState = Updater.STATE_DOWNLOAD_ERR;
				                        }
				                    } else {
				                        mState = Updater.STATE_DOWNLOAD_ERR;
				                    }
				                }
				            }
							
						}	
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
    	mState = Updater.STATE_DOWNLOADING;
	}
//end added  
    
    public class HttpThread extends Thread {
        private int mPos = 0; 
        private int mLength = 0; 
        private int mPackageSize = 0;
        private int mTaskType = TASK_DOWN_PACKAGE;
        private boolean mFinish = false;
        private String mUrl = ""; 
        HttpURLConnection conn = null;
    	private File saveFile;	
    	private UpdateService updateservice;

        public HttpThread(String url,UpdateService downloader,File file,int downlength) {
            mLength = 0;
            mFinish = false;
            mUrl = url;
    		saveFile = file;
    		updateservice = downloader;
    		mPos = downlength;   
    		Log.d(TAG,"HttpThread()->mPos = "+mPos+"  downlength = "+downlength);
        }

        public synchronized int getDownPos() {
            return mPos;
        }

        public synchronized int getLength() {
            return Updater.fileSize;
        }

        public synchronized int getPackageSize() {
            return mPackageSize;
        }

        public synchronized boolean isFinish() {
            return mFinish;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
			getServerVersionInfor();
            download();
        }

  		public void getServerVersionInfor(){
    		//String VerJSONPath = mUrl+"/"+"version.json";
    		String VerJSONPath = mUrl+"/"+"version.json";
    		Log.d(TAG,"getServerVersionInfor()->VerJSONPath(1) = "+VerJSONPath);
    		VerJSONPath = VerJSONPath.replaceAll(" ", "_");
    		try {
    			Log.d(TAG,"getServerVersionInfor()->VerJSONPath(2) = "+VerJSONPath);
    			JSONObject jsonObj = GetNewVersionCode.getVersionJSON(VerJSONPath);
    			Log.d(TAG,"getServerVersionInfor()->jsonObj = "+jsonObj);
				if(jsonObj != null){
				
					mInfor.setUrl(jsonObj.get("url").toString());
					mInfor.setmBrand(jsonObj.get("brand").toString());
					mInfor.setChipPlatform(jsonObj.get("ChipPlatform").toString());
					mInfor.setCUS(jsonObj.get("CUS").toString());
					mInfor.setPANEL(jsonObj.get("PANEL").toString());	
					mInfor.setDeviceType(jsonObj.get("DeviceType").toString());
					mInfor.setsvnVer(jsonObj.get("svnVer").toString());
					mInfor.setMd(jsonObj.get("md5").toString());
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}		
  		 	
  		
        private void download() {
            Log.d(TAG, "download.SystemNetUpdateActivity1.check(1):"+ SystemNetUpdateActivity.check);
//            File file = null;
            InputStream input = null;
            int len = 0;
            int lensize = 0;
            final int BUFSIZE = 4096;
            byte[] buffer = new byte[BUFSIZE];  
			int startPos = mPos;
            //OutputStream output = null;
            // HttpURLConnection conn = null;
            try {
				URL url = new URL(Updater.mDownloadUrl);
                if (conn != null) {
                    conn.disconnect();
                }
                conn = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "download.SystemNetUpdateActivity.check is false.....");
                conn.setConnectTimeout(15 * 1000); // 15 seconds
                conn.setReadTimeout(180 * 1000); // 180 seconds
//jim.lei added in 0924
				conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729");
				String sProperty = "bytes="+startPos+"-";
				conn.setRequestProperty("RANGE",sProperty);	
//end added		                
                conn.connect();
                input = conn.getInputStream();
                synchronized (this) {
                    mLength = conn.getContentLength();
                    Log.d(TAG, "RANGE->mLength=" + mLength+" Updater.fileSize = "+Updater.fileSize);
                }
                
//                file = updateservice.createFile();
//                if (null != file) {
//                    output = new FileOutputStream(file);
//                }
                               
                
				RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rw");
				threadfile.seek(startPos); 
				//input.skip(startPos);
//                fileAccess = new FileAccess(mSavePath + OTA_NAME,startPos);
                while (len != -1) {
                    len = input.read(buffer, 0, BUFSIZE);
                    lensize = lensize + len;
                    Log.d("yesuo1", "BUFSIZE"+BUFSIZE+"lensize"+lensize);
                    if (-1 == len) {
                        break;
                    }
                  
//                    if (null != output) {
//                        output.write(buffer, 0, len);
//                    }
                    Log.d(TAG,"startPos = "+startPos+" lensize = "+lensize+" mLength = "+mLength);
					threadfile.write(buffer, 0, len);
//					startPos += fileAccess.write(buffer,0,len);
					
                  
                    synchronized (this) {
                        mPos = mPos + len;
    					updateservice.append(len);
                        //Log.d(TAG, "mPos=========" + mPos);
                      
//                        if (Updater.fileSize > 0) {
//                             int progress = (int) (mPos * 100.0 / Updater.fileSize);
                        if (Updater.fileSize > 0) {
                            int progress = (int) (mPos * 100.0 / Updater.fileSize);                        
                             if(percent!=progress){
                                 percent=progress;
                                 Message msg = mHandler.obtainMessage();
                                 msg.what = progress;
                                 mHandler.sendMessage(msg);
                             }                          
                        }
                    }
                }
                //threadfile.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "download.FileNotFoundException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Log.d(TAG, "download.MalformedURLException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "download.IOException=" + e.getLocalizedMessage());
                e.printStackTrace();
            } finally {
                finallyMethod(input, null);
            } // finally
            synchronized (this) {
                Log.d(TAG, "synchronized...");
                mFinish = true;
                if (mTaskType == TASK_DOWN_PACKAGE) {
//                    Log.d("Updater", "mPos======="+mPos+"mLength======="+mLength );
//                    if (mLength > 0 && mPos == mLength) {
                  Log.d(TAG, "mPos======="+mPos   +"Updater.fileSize======="+Updater.fileSize );
                  if (Updater.fileSize > 0 && mPos == Updater.fileSize) {                	
                        if (updateservice.checkFile(saveFile)) {
                            mState = Updater.STATE_DOWNLOAD_OK;
                        } else {
                            mState = Updater.STATE_DOWNLOAD_ERR;
                        }
                    } else {
                        mState = Updater.STATE_DOWNLOAD_ERR;
                    }
                }
            }

        }// download

//        public void finallyMethod(InputStream input, OutputStream output, String flag) {
        public void finallyMethod(InputStream input,String flag) {	
            try {
                if (null != input) {
                    input.close();
                }
            } catch (Throwable t) {
                Log.d(TAG, "input...");
            }

            try {
                if (null != conn) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Throwable t) {
                Log.d(TAG, "conn...");
            }

//            try {
//                if (null != output) {
//                    output.flush();
//                    output.close();
//                }
//            } catch (Throwable t) {
//                Log.d(TAG, "output...");
//            }
            if ("NetUpdateActivity".equals(flag)) {
                mState = Updater.STATE_READ_MAC_ERR;
            }
        }
    }

   
    private File createFile() throws IOException {
        Log.d(TAG, "Updater.mSavePath=========" +mSavePath);
        File file = null;
        //String pathName = mSavePath + DeviceType+PANEL+OTA_NAME;
		String pathName = mSavePath + DeviceType+OTA_NAME;
//        if (!pathName.endsWith(TMP_SUFFIX)) {
//            pathName = pathName + TMP_SUFFIX;
//        }
        Log.d(TAG, "pathName=" + pathName);
        file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }

  
        int idx = pathName.lastIndexOf('/');
        File dir = new File(pathName.substring(0, idx));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    
        file.createNewFile();
        return file;
    }
    
 
    private File createMultiDownFile() throws IOException {
        Log.d(TAG, "Updater.mSavePath=========" +mSavePath);
        if(mSavePath.isEmpty())
        {
        	Log.d(TAG, "mSavePath.isEmpty()");
        	mSavePath = "/cache/";
        }
        File file = null;
        String pathName = mSavePath;
        Log.d(TAG, "pathName=" + pathName);
        file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }

      
        int idx = pathName.lastIndexOf('/');
        File dir = new File(pathName.substring(0, idx));
        if (!dir.exists()) {
            dir.mkdirs();
        }

   
        file.createNewFile();
        return file;
    }    

   
    private boolean checkFile(File file) {
    	update(0);
        //String pathName = mSavePath + DeviceType+PANEL+OTA_NAME;
		String pathName = mSavePath+DeviceType+OTA_NAME;
		Log.d("chend", "pathName =" + pathName);
        String md5 = MD5.md5sum(pathName);
        if (TASK_DOWN_PACKAGE == mTaskType) {
            Log.d(TAG, "checkFile, md5=" + md5 );
            Log.d(TAG, "checkFile, aa net.mMd5 =" + Updater.mMd5 );
            if ((Updater.mMd5==null||Updater.mMd5.equals(""))){
            	Updater.mMd5 = VersionInfor.getMd();
            //	Log.d(TAG, "  VersionInfor Infor =new VersionInfor() net.ota.mMd5:"+Updater.mMd5);
            }
            if (Updater.mMd5==null||Updater.mMd5.equals("") || !Updater.mMd5.equalsIgnoreCase(md5)) {
            	Log.d(TAG, "checkFile, return false");
                return false;
            }
        }

        Log.d(TAG, "checkFile, name=" + pathName);
        File newFile = new File(pathName);
        file.renameTo(newFile);
        Log.d(TAG, "checkFile, return true");
        return true;
    }
    

    public synchronized static boolean writeStringToFile(String content, String path) {
        Log.d(TAG, "Updater.writeStringToFile.content:" + content);
        Log.d(TAG, "Updater.writeStringToFile.path:" + path);
        File file = new File(path);
        try {
            if (file.isFile()) {
                file.deleteOnExit();
                file = new File(file.getAbsolutePath());
            }
            OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            os.write(content);
            os.close();
        } catch (Exception e) {
            // e.printStackTrace();
            Log.d(TAG, "Updater.writeStringToFile.Exception:" + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public synchronized void downloadPackage(String url,UpdateService service,File file,int length) {
        if (mState != Updater.STATE_RECIECE_VER_OK) {
            return;
        }
        Log.d(TAG,"UpdateService.java downloadPackage->url = "+url);
        mHttp = new HttpThread(url,service,file,length);
        mHttp.start();
        mState = Updater.STATE_DOWNLOADING;
    }

}
