package tufer.com.menutest.UIActivity.update;

public class VersionInfor {

	
	private String ds;

	private String version;

	private String uds;

	private long size;

	private static String md;

	private int force;

	private String changeInfoEN;
	
	private String changeInfoCH;
	


	private String url;

	private String svnVer = ""; 

    private String mBrand = "";
  
    private String ChipPlatform = ""; 

    private String CUS = ""; 

    private String PANEL = ""; 
    
    private String DeviceType = ""; 
	
	public String getsvnVer() {
		return svnVer;
	}

	public void setsvnVer(String svnVer) {
		this.svnVer = svnVer;
	}
	
	public String getmBrand() {
		return mBrand;
	}

	public void setmBrand(String mBrand) {
		this.mBrand = mBrand;
	}
	
	public String getChipPlatform() {
		return ChipPlatform;
	}

	public void setChipPlatform(String ChipPlatform) {
		this.url = ChipPlatform;
	}
	
	public String getCUS() {
		return CUS;
	}

	public void setCUS(String CUS) {
		this.CUS = CUS;
	}
	
	public String getPANEL() {
		return PANEL;
	}

	public void setPANEL(String PANEL) {
		this.url = PANEL;
	}
    
	public String getDeviceType() {
		return DeviceType;
	}

	public void setDeviceType(String DeviceType) {
		this.DeviceType = DeviceType;
	}
	
	
	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public String getDs() {
		return ds;
	}

	public void setDs(String ds) {
		this.ds = ds;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUds() {
		return uds;
	}

	public void setUds(String uds) {
		this.uds = uds;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public static String getMd() {
		return md;
	}

	public static void setMd(String md5) {
		md = md5;
	}
	
	public String getChangeInfoEN() {
		return changeInfoEN;
	}

	public void setChangeInfoEN(String changeInfoEN) {
		this.changeInfoEN = changeInfoEN;
	}
	
	public String getChangeInfoCH() {
		return changeInfoCH;
	}

	public void setChangeInfoCH(String changeInfoCH) {
		this.changeInfoCH = changeInfoCH;
	}
	
}
