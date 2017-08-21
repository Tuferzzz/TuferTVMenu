
package tufer.com.menutest.UIActivity.update;

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tufer.com.menutest.org.dtools.ini.BasicIniFile;
import tufer.com.menutest.org.dtools.ini.IniFile;
import tufer.com.menutest.org.dtools.ini.IniFileReader;
import tufer.com.menutest.org.dtools.ini.IniSection;


public class Updater {

	private FileService fileService; 
	
  
    private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	

    private int block;
    

    private DownloadThread[] threads;
	

    public static int fileSize = 0;   

    
    private HttpThread mHttp = null; 

    private final static String TAG = "Updater";

    private String mData = ""; 

    public static final String CACHE_PATH = "/cache/";

    public final static int DOWNLOAD_THREAD_NUM =10;//3;
    

    public final static int STATE_NOTHING = 0; 

    public final static int STATE_REQUEST_VER = 1; 

    public final static int STATE_READ_CFG_ERR = 2;

    public final static int STATE_READ_MAC_ERR = 3; 

    public final static int STATE_RECIECE_VER_ERR = 4;

    public final static int STATE_RECIECE_VER_OK = 5;

    public final static int STATE_NO_PACKAGE = 6; 

    public final static int STATE_VER_IS_NEWEST = 7; 

    public final static int STATE_DOWNLOADING = 8;

    public final static int STATE_DOWNLOAD_ERR = 9; 

    public final static int STATE_DOWNLOAD_OK = 10;

    public final static int STATE_NOT_ENOUGH_STORAGE = 11; 
    
    public final static int STATE_UPDATE_ERROR = 12; 
    public final static int STATE_MAC_CHECK_ERR = 13; 
    public final static int STATE_VER_CHECK_ERR = 14; 
    
    public final static int STATE_FILE_EXISTS = 15;
    
    public final static int STATE_FILE_EXISTS_MD5_CHECK_ERR = 16;
  
    private final static int TASK_CHECK_VERSION = 1; 

    private final static int TASK_DOWN_PACKAGE = 2;

   
    private final static int CFG_RECOVERY = 1; 

    private final static int CFG_BACKUP = 2;

    private final static String CFG_FILE_PATH = "/system/bin/version.ini";

    private final static String VERSION_INFO = "versin_info.txt";

    public  static String OTA_NAME = ".zip";
    
    public static int conLength = 0;

    //public final static String TMP_SUFFIX = ".tmp";
    public static String mUrl = "";
    public static String preUrl = "";
    private String svnVer = "";
    private String cmpStr = ""; 
    private String mBrand = ""; 
    private String ChipPlatform = ""; 
    private String CUS = ""; 
    private String PANEL = ""; 
    private String DeviceType = "";
    private static VersionInfor mInfor;
    

	private String curVersion = "";

	private String serverVersion = "";
	

	public static String curVersionExits = "";

	public static String serverVersionExits = "";
	
    private static String mSystemMac = ""; 

    public static String mDownloadUrl = ""; 
    public static String mMd5 = ""; /* md5 */
    private UpdateService.ServiceBinder mServiceBinder;
    

    
    public Updater(String mac, UpdateService.ServiceBinder binder) {
        mSystemMac = mac;
        mServiceBinder = binder;
        init();
        Log.d(TAG," Updater(String mac, ServiceBinder binder) DeviceType:"+DeviceType);
        if(DeviceType.equals("")||DeviceType== null)
          readConfig(CFG_FILE_PATH);
        //OTA_NAME = DeviceType+PANEL+".ota.zip";
        OTA_NAME =DeviceType + ".zip";
     
    }


    private void init() {
        mUrl = "";
        svnVer = "";
        mBrand = "";
        mDownloadUrl = "";
        ChipPlatform = "";
        CUS = "";
        PANEL = "";
        DeviceType = "";
        mMd5 = "";
        mInfor = new VersionInfor();
    }
    
  //jim added for test
  	
  		private int checkVersionStatus() {
  			int ret = 0;
  			serverVersion = mInfor.getsvnVer();
			//boolean Size = compareVersion(serverVersion,curVersion);
  			mMd5 = mInfor.getMd();
  			SystemNetUpdateActivity.changeInfoEN = mInfor.getChangeInfoEN();
  			SystemNetUpdateActivity.changeInfoCH = mInfor.getChangeInfoCH();
  
  			
  			Log.d(TAG,"Updater.java checkVersionStatus()->curVersion:" + curVersion);
  			Log.d(TAG,"Updater.java checkVersionStatus()->serverVersion:" + serverVersion);
  			Log.d(TAG,"Updater.java checkVersionStatus()->mMd5:" + mMd5);
  			
  			if((curVersion == null) || (serverVersion == null) || ("".equals(curVersion)) || ("".equals(serverVersion))){
  				ret = -2;
  				return ret;
  			}
  		
  			if(curVersion.equals(serverVersion)){
  				ret = -1;
  			}
  		
  			else{
  					
  			//	if(Integer.parseInt(serverVersion) > Integer.parseInt(curVersion)){
				if(isAppNewVersion(serverVersion,curVersion)){
  					ret = 0;
  				}else{
  					ret = -1;
  				}
  			}
  			mServiceBinder.setmUpgradeCode(curVersion);
  			mServiceBinder.setmNewVersion(serverVersion);
  			return ret;
  		}
		
		public static boolean isAppNewVersion(String localVersion, String onlineVersion)
    {
        if (localVersion.equals(onlineVersion))
        {
            return false;
        }
        String[] localArray = localVersion.split("\\.");
        String[] onlineArray = onlineVersion.split("\\.");
     
        int length = localArray.length < onlineArray.length ? localArray.length : onlineArray.length;
     
        for (int i = 0; i < length; i++)
        {
            if (Integer.parseInt(onlineArray[i]) > Integer.parseInt(localArray[i]))
            {
                return false;
            }
            else if (Integer.parseInt(onlineArray[i]) < Integer.parseInt(localArray[i]))
            {
                return true;
            }
         
        }
     
        return true;
    }		
  		
  		private void checkExitsVersionStatus() {
  			serverVersionExits = mInfor.getsvnVer();
  			mMd5 = mInfor.getMd();
  			SystemNetUpdateActivity.changeInfoEN = mInfor.getChangeInfoEN();
  			SystemNetUpdateActivity.changeInfoCH = mInfor.getChangeInfoCH();
  			
  			Log.d(TAG,"serverVersionExits = "+serverVersionExits+" mMd5 = "+mMd5+" " +
  					"SystemNetUpdateActivity1.changeInfoEN = "+SystemNetUpdateActivity.changeInfoEN+
  					"SystemNetUpdateActivity1.changeInfoCH = "+SystemNetUpdateActivity.changeInfoCH);
  		}		
  //end added	    
    
 
    private boolean readConfig(String pathFile) {
        Log.d(TAG, "Updater.readConfig, inifile=" + pathFile);
        boolean flag = false;
        File file = new File(pathFile);
        IniFile ini = new BasicIniFile();
        IniFileReader reader = new IniFileReader(ini, file);
        try {
            reader.read();
            IniSection section = ini.getSection("MISC_SOFTWARE_UPGRADE");
            mUrl = section.getItem("url").getValue();
            Log.d("yesuo1",mUrl+"mUrl" );
            mBrand = section.getItem("brand").getValue();
            ChipPlatform = section.getItem("ChipPlatform").getValue();
            CUS = section.getItem("CUS").getValue();
            PANEL = section.getItem("PANEL").getValue();
            DeviceType = section.getItem("DeviceType").getValue();

            svnVer = SystemProperties.get("ro.build.svnversion");
            curVersion = svnVer;
            curVersionExits = svnVer;
            preUrl = mUrl;

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

    private void backkupCfg(int flag) {
        final String backup = "/system/bin/version-bak.ini";
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            byte[] buffer = new byte[1024];

			if (CFG_RECOVERY == flag) {
				fis = new FileInputStream(backup);
				fos = new FileOutputStream(CFG_FILE_PATH);
			} else {
				fis = new FileInputStream(CFG_FILE_PATH);
				fos = new FileOutputStream(backup);
			}

			for (int len = 0; true;) {
				len = fis.read(buffer);
				if (len > 0) {
					fos.write(buffer, 0, len);
				} else {
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
			} catch (Throwable t) {
			}
			
			try {
				if (null != fos) {
					fos.close();
					fos = null;
				}
			} catch (Throwable t) {
			}
		}
	}



    public synchronized boolean checkVersion() {
        if (STATE_REQUEST_VER == mServiceBinder.getmState()
                || STATE_DOWNLOADING == mServiceBinder.getmState()) {
            return false;
        }
        mServiceBinder.setmState(STATE_NOTHING);

        if (!readConfig(CFG_FILE_PATH)) {
            mServiceBinder.setmState(STATE_READ_CFG_ERR);
            backkupCfg(CFG_RECOVERY);
            return false;
        } else {
            backkupCfg(CFG_BACKUP);
        }

        if (mSystemMac.isEmpty()) {
            mServiceBinder.setmState(STATE_READ_MAC_ERR);
            return false;
        }

        if (mHttp != null) {
            if (!mHttp.isFinish()) {
                return true;
            }
            mHttp = null;
        }
    	//String req = mUrl+ChipPlatform+"/"+CUS+"/"+PANEL;
 
    	//String req = mUrl+ChipPlatform+"/"+CUS+"/"+PANEL+"/"+DeviceType;
		String req = mUrl+ChipPlatform+"/"+CUS+"/"+PANEL;
        mHttp = new HttpThread(req, null, TASK_CHECK_VERSION);
        mHttp.start();
        mServiceBinder.setmState(STATE_REQUEST_VER);
        return true;
    }

    public class HttpThread extends Thread {
        private int mPos = 0; 

        private int mLength = 0; 

        private int mPackageSize = 0;

        private int mTaskType = TASK_CHECK_VERSION; 

        private boolean mFinish = false;

        private String mUrl = "";

        private String mName = ""; 

        HttpURLConnection conn = null;

		public HttpThread(String url, String name, int type) {
			mPos = 0;
			mLength = 0;
			mTaskType = type;
			mFinish = false;
			mUrl = url;
			mName = name;
		}

		public synchronized int getDownPos() {
			return mPos;
		}

		public synchronized int getLength() {
			return mLength;
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
			//if(getServerMACInfor() == 0){
				download();
			//}
		}
		
//jim.lei added in 0822
  		public int getServerMACInfor(){
    		String macJSONPath = preUrl+"mac.js";
  			Log.d(TAG,"getServerMACInfor()->preUrl = "+preUrl);
    		//String macJSONPath = mUrl+"/"+ChipPlatform+"/"+CUS+"/"+"mac.js";
    		int macRet = 0;
    		Log.d(TAG,"getServerMACInfor()->macJSONPath(1) = "+macJSONPath);
    		macJSONPath = macJSONPath.replaceAll(" ", "_");
    		try {
    			Log.d(TAG,"getServerMACInfor()->macJSONPath(2) = "+macJSONPath);
    			macRet = GetNewVersionCode.getServerMACJSON(macJSONPath,mSystemMac);
    			Log.d(TAG,"getServerMACInfor()->macRet = "+macRet);
    			if(macRet == -1){
    				mServiceBinder.setmState(STATE_MAC_CHECK_ERR);
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    		return macRet;
		}
//end added		

  		public void getServerVersionInfor(){
    		String VerJSONPath = mUrl+"/"+"version.json";
    		//String VerJSONPath = mUrl+"/"+ChipPlatform+"/"+CUS+"/"+"version.js";
    		Log.d(TAG,"getServerVersionInfor()->VerJSONPath(1) = "+VerJSONPath);
    		VerJSONPath = VerJSONPath.replaceAll(" ", "_");
    		try {
    			Log.d(TAG,"getServerVersionInfor()->VerJSONPath(2) = "+VerJSONPath);
    			JSONObject jsonObj = GetNewVersionCode.getVersionJSON(VerJSONPath);
    			Log.d(TAG,"getServerVersionInfor()->jsonObj = "+jsonObj);
				if(jsonObj != null){
		
					/*mInfor.setUrl(jsonObj.get("url").toString());
					mInfor.setmBrand(jsonObj.get("brand").toString());
					mInfor.setChipPlatform(jsonObj.get("ChipPlatform").toString());
					mInfor.setCUS(jsonObj.get("CUS").toString());
					mInfor.setPANEL(jsonObj.get("PANEL").toString());
					mInfor.setDeviceType(jsonObj.get("DeviceType").toString());
					mInfor.setsvnVer(jsonObj.get("svnVer").toString());	
					mInfor.setMd(jsonObj.get("md5").toString());
					mInfor.setChangeInfoEN(jsonObj.get("changeInfoEN").toString());
					mInfor.setChangeInfoCH(jsonObj.get("changeInfoCH").toString());
					*/
					mInfor.setUrl(jsonObj.getString("url"));
					mInfor.setmBrand(jsonObj.getString("brand"));
					mInfor.setChipPlatform(jsonObj.getString("ChipPlatform"));
					mInfor.setCUS(jsonObj.getString("CUS"));
					mInfor.setPANEL(jsonObj.getString("PANEL"));
					mInfor.setDeviceType(jsonObj.getString("DeviceType"));
					mInfor.setsvnVer(jsonObj.getString("svnVer"));	
					mInfor.setMd(jsonObj.getString("md5"));
					mInfor.setChangeInfoEN(jsonObj.getString("changeInfoEN"));
					mInfor.setChangeInfoCH(jsonObj.getString("changeInfoCH"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}		
		
  	    public String getFileName(HttpURLConnection conn) {
  	        String filename = mDownloadUrl.substring(mDownloadUrl.lastIndexOf('/') + 1);
  	      Log.d(TAG,"getFileName  xy.huang filename :"+filename);
  	        if(filename==null || "".equals(filename.trim())){
  	            for (int i = 0;; i++) {
  	                String mine = conn.getHeaderField(i);
  	                if (mine == null) break;               
  	                if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
  	                    Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
  	                    if(m.find()) return m.group(1);
  	                }
  	            }
  	            filename = UUID.randomUUID()+ ".tmp";
  	        }
  	        return filename;
  	    }

  		
        private void download() {
            Log.d(TAG, "download.SystemNetUpdateActivity1.check(1):"+ SystemNetUpdateActivity.check);
			File file = null;
			InputStream input = null;
			OutputStream output = null;
			//xy.huang modify 2015-09-21 start
			//mDownloadUrl = mUrl+"/"+DeviceType+PANEL+".ota.zip"; 
			//xy.huang modify 2015-09-21 end
			mDownloadUrl = mUrl+"/"+DeviceType + ".zip";
			//mDownloadUrl = "http://192.168.1.109/sauna/resources/ota/MstarUpgrade.bin"; 
			Log.d(TAG, "download.SystemNetUpdateActivity1.mDownloadUrl:"+ mDownloadUrl);
			try {
				
	            fileService = new FileService(SystemNetUpdateActivity.activityContext);
	            URL url = new URL(mDownloadUrl);
	            threads = new DownloadThread[DOWNLOAD_THREAD_NUM];                    
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setConnectTimeout(5*1000);
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
	            conn.setRequestProperty("Accept-Language", "zh-CN");
	            conn.setRequestProperty("Referer", mDownloadUrl); 
	            conn.setRequestProperty("Charset", "UTF-8");
	            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
	            conn.setRequestProperty("Connection", "Keep-Alive");
	            conn.connect();
	            FileDownloader.printResponseHeader(conn);
	            Log.e("chend00","conn.getResponseCode() = "+ conn.getResponseCode());
	            if (conn.getResponseCode()==200) {
	                fileSize = conn.getContentLength();
	                Log.d(TAG,"logdata  xy.huang fileSize :"+fileSize);
	                if (fileSize <= 0) {
	                	Log.d(TAG,"logdata  fileSize <= 0  = "+fileSize);
	                	mServiceBinder.setmState(STATE_VER_CHECK_ERR);
	                	//return ;
	                }
	                	
//	                	throw new RuntimeException("Unkown file size ");
	                String filename = getFileName(conn);
	                Map<Integer, Integer> logdata = fileService.getData(mDownloadUrl);       
	                Log.d(TAG,"logdata = "+ logdata+" filename = "+filename);
	                if(logdata.size()>0){
	                    for(Map.Entry<Integer, Integer> entry : logdata.entrySet())
	                        data.put(entry.getKey(), entry.getValue());
	                }
	                Log.d(TAG,"data.size() = "+ data.size()+" threads.length = "+threads.length);
	                if(data.size()==threads.length){
	                    for (int i = 0; i < threads.length; i++) {
	                        conLength += data.get(i+1);
	                    }
	              
	                }
	        
	                block = (fileSize % threads.length)==0? fileSize / threads.length : fileSize / threads.length + 1;
	                Log.d(TAG,"block = "+ block);
	            }else{
	                //throw new RuntimeException("server no response ");
	            	mServiceBinder.setmState(STATE_VER_IS_NEWEST);	  
	            }

//				URL url = new URL(mDownloadUrl);
//				if(conn != null ){
//					conn.disconnect();
//				}
//				conn = (HttpURLConnection)url.openConnection();				
//				conn.setConnectTimeout(15*1000); // 15 seconds
//				conn.setReadTimeout(180*1000); //  180 seconds
//				conn.connect();				
//				input = conn.getInputStream();
//				synchronized (this) {
//					mLength = conn.getContentLength();
//					conLength = mLength;
//					Log.d(TAG, "mLength=" + mLength);
//				}		
				
				
//jim.lei added in 1218
				if((checkFileExists() == (long)conLength)&&(conLength != 0)){
				//if((checkFileExists() == (long)fileSize)&&(conLength == 0)){	
					checkStorage(mPackageSize);
					if(checkFileMD5Exists()){						
						mServiceBinder.setmState(STATE_FILE_EXISTS);
						return;
					}else{
						mServiceBinder.setmState(STATE_FILE_EXISTS_MD5_CHECK_ERR);
						return;
					}
				}
//end added				
				file = createFile();
				if (null != file) {
					output = new FileOutputStream(file);
				}
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
				finallyMethod(input, output, null);
			} // finally		

			httpGetPackageSize();
            synchronized (this) {
                Log.d(TAG, "synchronized...");
                mFinish = true;
                if (mTaskType == TASK_CHECK_VERSION) {
                    Log.d(TAG, "mTaskType == TASK_CHECK_VERSION...");
                    //if (mLength > 0 && mPos == mLength) {
                    //if (mLength > 0) {
                    if (fileSize > 0) { 	
                        Log.d(TAG, "fileSize > 0 && mPos == mLength...");
                        Log.d(TAG, "fileSize > 0 && mPos == mLength.mPos:" + mPos);
                        Log.d(TAG, "fileSize > 0 && mPos == mLength.mLength:" + fileSize);
             
                        //deleteUpgradeTempFile();
                        if (checkStorage(mPackageSize)) {
                        } else {
                            Log.d(TAG, "mState = STATE_NOT_ENOUGH_STORAGE...");
                            mServiceBinder.setmState(STATE_NOT_ENOUGH_STORAGE);
                        }
                    } else {
                        mServiceBinder.setmState(STATE_RECIECE_VER_ERR);
                    }
                }
            }
        }// download

        public void finallyMethod(InputStream input, OutputStream output, String flag) {
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

            try {
                if (null != output) {
                    output.flush();
                    output.close();
                }
            } catch (Throwable t) {
                Log.d(TAG, "output...");
            }
            if ("NetUpdateActivity".equals(flag)) {
                mServiceBinder.setmState(STATE_READ_MAC_ERR);
            }
        }

        private long checkFileExists(){
            File file = null;
            File otafile = null;
            long length = 0;
            String otaSetting = "/cache/Setttings_ota.txt";
            String pathName = "";
            String path ="/cache/"+Updater.OTA_NAME;
            otafile = new File(otaSetting);
            if(otafile.exists()){
            	pathName = SystemNetUpdateActivity.readFileByLines("/cache/Setttings_ota.txt");
            }else{
            	pathName = path;
            }
            Log.d(TAG, "checkFileExists()->pathName=" + pathName);
            file = new File(pathName);
            if (file.exists()) {
            	Log.d(TAG, "checkFileExists()->file.exists");
            	length = file.length();
            }
            Log.d(TAG,"checkFileExists()->length = "+length);
			return length;
        }
        
        
        
        private boolean checkFileMD5Exists(){
            File file = null;
            File otafile = null;
            long length = 0;
            String otaSetting = "/cache/Setttings_ota.txt";
            String pathName = "";
            otafile = new File(otaSetting);
            checkExitsVersionStatus();
            if(otafile.exists()){
            	pathName = SystemNetUpdateActivity.readFileByLines("/cache/Setttings_ota.txt");
            }else{
            	pathName = "/cache/"+Updater.OTA_NAME;
            	//pathName = "/cache/ota.zip.tmp";
            }
            String md5 = MD5.md5sum(pathName);
            Log.d(TAG, "checkFile, md5=" + (md5 != null ? md5 : "md5 == null"));
            Log.d(TAG, "checkFile, MD5=" + (mMd5 != null ? mMd5 : "mMd5 == null"));
            if ((mMd5==null||mMd5.equals(""))&& mInfor!= null)
            	mMd5 = mInfor.getMd();
            if (mMd5==null||mMd5.equals("") || !mMd5.equalsIgnoreCase(md5)) {
            	Log.d(TAG, "checkFile, return false");
                return false;
            }
            Log.d(TAG, "checkFile, return true");
            return true;
        }
        
     
        private File createFile() throws IOException {
            File file = null;

            if (null != mName && !mName.isEmpty()) {
                String pathName = mServiceBinder.getmSavePath() + mName;
//                if (!pathName.endsWith(TMP_SUFFIX)) {
//                    pathName = pathName + TMP_SUFFIX;
//                }
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
			}
			return file;
		}

      
        private int httpGetPackageSize() {
            synchronized (this) {
                mPackageSize = 0;
                if (mTaskType != TASK_CHECK_VERSION)
                    return 0;
            
                int ret = checkVersionStatus();
                switch (ret) {
                    case 0:
                        mServiceBinder.setmState(STATE_RECIECE_VER_OK);
                        break;
                    case -1:
                        mServiceBinder.setmState(STATE_VER_IS_NEWEST);
                        break;
                    case -2:
                        mServiceBinder.setmState(STATE_VER_CHECK_ERR);
                        break;
                    default:
                        mServiceBinder.setmState(STATE_NO_PACKAGE);
                        break;
                }
                if (ret != 0) {
                    return 0;
                }
            }

			int length = 0;
			HttpURLConnection conn = null;
			try {
				URL url = null;
				synchronized (this) {
					url = new URL(mDownloadUrl);
				}
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.connect();
				length = conn.getContentLength();
				synchronized (this) {
					mPackageSize = length;
				}
				Log.d(TAG, "package size =" + length);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != conn) {
						conn.disconnect();
						conn = null;
					}
				} catch (Throwable t) {
				}
			}
            return length;
        }

        private synchronized boolean checkStorage(long size) {

            Log.d(TAG, "checkStorage.size:" + size);
            long cachefreesize = getCacheFreeSize();
            Log.d(TAG, "checkStorage.cachefreesize=" + cachefreesize);
            if (size < cachefreesize) {
                Log.d(TAG, "checkStorage.size < cachefreesize......");
                mServiceBinder.setmSavePath(CACHE_PATH);
                writeStringToFile("cache", "/cache/Setttings_storage.txt");
                writeStringToFile("/cache/"+Updater.OTA_NAME, "/cache/Setttings_ota.txt");
                return true;
            }

            String path = Environment.getExternalStorageDirectory().getPath();
            long sdcardfreesize = getSdCardFreeSize();
            Log.d(TAG, "checkStorage.sdcardfreesize=" + sdcardfreesize);

            if (null != path && path.length() > 0 && size < sdcardfreesize) {
                Log.d(TAG, "checkStorage. size < sdcardfreesize......");
           
                if (!path.endsWith("/")) {
                    path += "/";
                }
                mServiceBinder.setmSavePath(path);
                writeStringToFile("sdcard", "/cache/Setttings_storage.txt");
                writeStringToFile("/sdcard/"+Updater.OTA_NAME, "/cache/Setttings_ota.txt");
                return true;
            }

            return false;
        }

    }

    synchronized static private long getCacheFreeSize() {

        StatFs sf = new StatFs("/cache");


        final int tmpsize = 1024 * 1024 * 6;

        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        long size = availCount * blockSize;
        if (size > tmpsize) {
            size -= tmpsize;
        } else {
            size = 0;
        }
        return size;
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

	public synchronized void deleteUpgradeTempFile() {

		String cachePath1 = CACHE_PATH + OTA_NAME;
//		String cachePath1 = CACHE_PATH + OTA_NAME + TMP_SUFFIX;
		Log.d(TAG, "deleteUpgradeTempFile().cachePath1:" + cachePath1);
		deletefile(cachePath1);
		

		String cachePath2 = CACHE_PATH + OTA_NAME;
		Log.d(TAG, "deleteUpgradeTempFile().cachePath2:" + cachePath2);
		deletefile(cachePath2);
		
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		if (sdcardPath != null && sdcardPath.length() > 0){
			if (!sdcardPath.endsWith("/")) {
	
				//String sdcardPath1 = sdcardPath +  "/" + OTA_NAME + TMP_SUFFIX;
				String sdcardPath1 = sdcardPath +  "/" + OTA_NAME;
				Log.d(TAG, "deleteUpgradeTempFile().sdcardPath1:" + sdcardPath1);
				deletefile(sdcardPath1);
				

				String sdcardPath2  = sdcardPath + "/" + OTA_NAME;
				Log.d(TAG, "deleteUpgradeTempFile().sdcardPath2:" + sdcardPath2);
				deletefile(sdcardPath2);
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

    synchronized static private long getSdCardFreeSize() {
        File file = Environment.getExternalStorageDirectory();
        if (null == file) {
            return 0;
        }

        StatFs sf = new StatFs(file.getPath());
        if (null == sf) {
            return 0;
        }
        if (!CheckSDCard.hasStorage()) {
            return 0;
        }

        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();

        return availCount * blockSize;
    }

}
