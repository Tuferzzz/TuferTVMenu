package tufer.com.menutest.UIActivity.update;


import android.content.Context;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.util.Log;


import com.mstar.android.ethernet.EthernetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import tufer.com.menutest.Util.TVRootApp;

public class NetInfo {
	private String mIpAddr = "";
	private String mNetName = "";
	private String mMacAddr = "";
	private Context mContext = null;
	private ConnectivityManager mConnectivityManager;

	public NetInfo(Context context) {
		mContext = context;
	}

	public String getIpAddr() {
		return mIpAddr;
	}

	public String getNetName() {
		return mNetName;
	}

	public String getMac() {
		return mMacAddr;
		//return "AA:AA:AA:AA:AA:AA";
	}

	public boolean getNetInfo() {

		boolean r = false;
		r = isNetInterfaceAvailable("eth0");
		if (r) {
			getEthernetInfo();
			return r;
		}

		r = isNetInterfaceAvailable("eth1");
		if (r) {
			getEthernetInfo();
			return r;
		}

		r = isNetInterfaceAvailable("wlan0");
		if (r) {
			getWifiInfo();

			mMacAddr = getEthernetMAC();
			return r;
		}

		r = isNetInterfaceAvailable("wlan1");
		if (r) {
			getWifiInfo();

			mMacAddr = getEthernetMAC();
			return r;
		}

		return r;
	}


	//	private String getEthernetMAC() {
//		 EthernetManager mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
//
//		return  mEthernetManager.getMacAddress();
//	}
	public String getEthernetMAC()
	{
		mConnectivityManager = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo localNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
		if ((localNetworkInfo == null) || (localNetworkInfo.getType() != 9)) {
			return "";
		}
		if (localNetworkInfo.getType() == 1) {
			return null;
		}
		return localNetworkInfo.getExtraInfo();
	}

	private boolean getEthernetInfo() {
		//EthernetManager mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);

		mIpAddr = SystemProperties.get("dhcp.eth0.ipaddress");
		mNetName = SystemProperties.get("dhcp.eth0.gateway");
		mMacAddr =   getEthernetMAC();
		Log.d("Updater", "getEthernetInfo.mIpAddr=" + mIpAddr);
		Log.d("Updater", "getEthernetInfo.mNetName=" + mNetName);
		Log.d("Updater", "getEthernetInfo.mMacAddr=" + mMacAddr);
		return true;
	}

	private boolean getWifiInfo() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (null != wifiInfo) {
			long addr = wifiInfo.getIpAddress();
			mNetName = wifiInfo.getBSSID();
			mMacAddr = wifiInfo.getMacAddress();
			if (0 != addr) {
				// handle negative values whe first octet > 127
				if (addr < 0) {
					addr += 0x100000000L;
				}
				mIpAddr = String.format("%d.%d.%d.%d", addr & 0xFF, (addr >> 8) & 0xFF, (addr >> 16) & 0xFF,
						(addr >> 24) & 0xFF);
			}
		}

		if (null != wifiInfo && null != mNetName) {
			return true;
		}

		return false;

	}

	private boolean isNetInterfaceAvailable(String netif) {
		String netInterfaceStatusFile = "/sys/class/net/" + netif + "/carrier";
		boolean bStatus = isStatusAvailable(netInterfaceStatusFile);
		Log.d("Updater", "isNetInterfaceAvailable.bStatus=" + bStatus);
		return bStatus;
	}

	private boolean isStatusAvailable(String statusFile) {
		char st = readStatus(statusFile);
		if (st == '1') {
			return true;
		}
		return false;
	}

	private synchronized char readStatus(String filePath) {
		File file = new File(filePath);
		int tempChar = 0;
		if (file.exists()) {
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(file));
				try {
					tempChar = reader.read();
				} catch (IOException e) {
					Log.d("Updater", "readStatus.IOException=" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.d("Updater", "readStatus.FileNotFoundException=" + e.getLocalizedMessage());
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					Log.d("Updater", "readStatus.finally IOException=" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

		}
		return (char) tempChar;
	}

}