package tufer.com.menutest.UIActivity.about;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


import com.mstar.android.tv.TvFactoryManager;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.Tools;


public class DeviceInfoSettings extends Activity {

    private static final int LEGAL_INFO = 6;

    private ListView aboutListView;

    private AboutAdapter mAboutAdapter;

    private String freeMemory;

    private String totalMemory;
    
    private String boardModel = "Mstar TV";

    private String customerName = "ADA828";

    private String[] content;

    private Handler handler;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getTVModelandCustomerName();

        handler = new Handler();

        freeMemory = Tools.getAvailMemory(this);
        totalMemory = Tools.getTotalMemory(this);

        if (freeMemory == null || totalMemory == null) {
            freeMemory = Tools.getAvailMemory(DeviceInfoSettings.this);
            totalMemory = Tools.getTotalMemory(DeviceInfoSettings.this);
        }

        setContentView(R.layout.about);
        content = new String[] {
                getTVModelNumber(), getTVSystemVersion(),getKernelVersion(), 
                getSystemBuildVersion(),getTVMemoryInfo()
        };
        
        findViews();
        registerListeners();
    }

    private void findViews() {
        aboutListView = (ListView) findViewById(R.id.about_list_select);
        aboutListView.setDividerHeight(0);

        mAboutAdapter = new AboutAdapter(this, new String[] {
                getResources().getString(R.string.model_number),
                getResources().getString(R.string.system_version),
                getResources().getString(R.string.kernelVersion),
                getResources().getString(R.string.buildVersion),
                getResources().getString(R.string.memory_infor),                
        }, content);
        aboutListView.setAdapter(mAboutAdapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                aboutListView.requestFocusFromTouch();
                aboutListView.setSelection(0);
            }
        }, 100);
    }

    private void registerListeners() {
        aboutListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.requestFocusFromTouch();
                if (position == LEGAL_INFO) {
                    //Tools.intentForward(DeviceInfoSettings.this, LegalInfoActivity.class);
                }
            }
        });
    }
    
    private void getTVModelandCustomerName() {
        String boardType = TvFactoryManager.getInstance().getBoardType();
        String[] array = boardType.split(":");
        if((array.length!=0)&&(array.length==3)){
         	 boardModel = array[0];
         	 customerName = array[1];
        }else{
           //boardModel = "MSD638";
         	 boardModel = "ADA";
        }
    }

    private String getTVModelNumber() {
    	   return "Mstar TV";
         //return boardModel; 
        //return Build.DISPLAY; 
    }

    private String getTVSystemVersion() {
        return Build.VERSION.RELEASE;
    }
    
    private static String getKernelVersion() {
		     String kernelVersion = "";
		     InputStream inputStream = null;
		     try {
			        inputStream = new FileInputStream("/proc/version");
		     } catch (FileNotFoundException e) {
			       e.printStackTrace();
			      return kernelVersion;
		     }
		     BufferedReader bufferedReader = new BufferedReader(
				              new InputStreamReader(inputStream), 8 * 1024);
		     String info = "";
		     String line = "";
		     try {
			        while ((line = bufferedReader.readLine()) != null) {
				             info += line;
			        }
		     } catch (IOException e) {
			       e.printStackTrace();
		     } finally {
			      try {
				          bufferedReader.close();
				          inputStream.close();
		             } catch (IOException e) {
				           e.printStackTrace();
			           }
		     }

		    try {
			       if (info != "") {
				        final String keyword = "version ";
				        int index = info.indexOf(keyword);
				        line = info.substring(index + keyword.length());
				        index = line.indexOf(" ");
				        kernelVersion = line.substring(0, index);
			       }
		    } catch (IndexOutOfBoundsException e) {
			    e.printStackTrace();
		    }

		   return kernelVersion;
	  }
    
    private String getSystemBuildVersion(){
         String mBuildTime = null;
         String mBuildVersion = null;
         String panelName = TvFactoryManager.getInstance().getPanelType();
     	   mBuildTime = new String(SystemProperties.get("ro.build.date")); 
     	   String[] array = mBuildTime.split(" "); 
		   //log.i("chend","array.length" + array.length);
		   Log.e("chend", "array.length" + array.length);
		  // for(int i=0,i<7,i++){
		    Log.e("chend", "array.length" + array[0]);
			Log.e("chend", "array.length" + array[1]);
			Log.e("chend", "array.length" + array[2]);
			Log.e("chend", "array.length" + array[3]);
			Log.e("chend", "array.length" + array[4]);
			Log.e("chend", "array.length" + array[5]);
		   //mBuildTime = new String(SystemProperties.get("ro.build.date"));  
     	   //String[] array = mBuildTime.split(" ");
     	   if(array.length>=6){
     	   	 int spaceNum= 0;
     	  	 for(int i=0; i<(array.length-spaceNum); i++){
     	  		  if("".equals(array[i])){
     	  		  	 for(int j=i;j<array.length;j++){
     	  		  	 	  if(j==(array.length -1)){
     	  		  	 	  	array[j] =""; 
     	  		  	 	  }else{
     	  		  	 	    array[j]=array[j+1];
     	  		  	 	  }
     	  		  	 }
     	  		  	spaceNum =spaceNum+1;
     	  		  }
     	  	 }
     	   }     	  
     	   if((mBuildTime !=null)&&(array.length!=0)&&(array.length>=6)){

			//}
     	  // if((mBuildTime !=null)&&(array.length!=0)&&(array.length==6)){
     	   //  mBuildVersion = boardModel+" "+customerName+" "+panelName+" "+
     	    // 	                 array[5]+array[1]+array[2]+" "+array[3];
			  mBuildVersion = SystemProperties.get("ro.ada.board_version");
     	   }else{
     	   	  mBuildVersion = TvFactoryManager.getInstance().getCompileTime().substring(0, 10)+" "+
     	   	                      TvFactoryManager.getInstance().getCompileTime().substring(11);
     	   } 
     	   return  mBuildVersion; 	 	        	   
    }

    private String getTVMemoryInfo() {
        return freeMemory + "/" + totalMemory;
    }

//    private String getTVDiskInfo() {
//        long freeSize = new Storage().getDiskStorage().mFreeStrorage;
//        long totalSize = new Storage().getDiskStorage().mTotalStorage;
//
//        return Tools.sizeToM(freeSize) + "/" + Tools.sizeToM(totalSize);
//    }

}
