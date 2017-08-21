package tufer.com.menutest.UIActivity.network;



import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import tufer.com.menutest.R;
import tufer.com.menutest.Util.SPUtils;

public class MacAddressSettingsHolder {
	private NetworkSettingsActivity mActivity;
	private LinearLayout mMacAddressSettingsRootLayout;
	private TextView mWiredTextView;
	private TextView mWirelessTextView;

	public MacAddressSettingsHolder(NetworkSettingsActivity activity) {
		mActivity = activity;

		findView();
		setListeners();
	}

	private void findView() {
		mMacAddressSettingsRootLayout = (LinearLayout) getViewById(R.id.mac_address_ll);
		mWiredTextView = (TextView) getViewById(R.id.wired_mac_address_tv);
		
		String wiredText = (String) SPUtils.get(mActivity, "EthernetMACAddress", mActivity.getString(R.string.please_open_wired));
		mWiredTextView.setText(wiredText);
		mWirelessTextView = (TextView) getViewById(R.id.wireless_mac_address_tv);
		mWirelessTextView.setText(mActivity.getString(R.string.mac_wireless_address)+" "+getWifiMac(mActivity));
	}
	
	public static String getWifiMac(Context context){
	       WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	       WifiInfo info = wifi.getConnectionInfo();
	       String mac = info.getMacAddress();
	       Log.e("","wifi mac : " + mac);
	       return mac ;
	    }

	private void setListeners() {
		// TODO Auto-generated method stub

	}

	public void setVisible(boolean visible) {
		if (visible) {
			mMacAddressSettingsRootLayout.setVisibility(View.VISIBLE);
		} else {
			mMacAddressSettingsRootLayout.setVisibility(View.GONE);
		}
	}

	private View getViewById(int id) {
		return mActivity.findViewById(id);
	}

	public void refreshWiredNetworkStatus(String string) {
		if (string.contains(":")) {
			String str = mActivity.getString(R.string.mac_wired_address) +" "+ string.split("\n")[2].split(":  ")[1];
			mWiredTextView.setText(str);
			SPUtils.put(mActivity, "EthernetMACAddress", str);
		}

	}

}
