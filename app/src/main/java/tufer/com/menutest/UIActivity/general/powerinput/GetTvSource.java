package tufer.com.menutest.UIActivity.general.powerinput;

import android.app.Activity;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPipPopManager;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tv.TvS3DManager;
import java.util.ArrayList;

import tufer.com.menutest.R;

public class GetTvSource {

	private static final int[] mSourceListInvisible = {
			TvCommonManager.INPUT_SOURCE_STORAGE,
			TvCommonManager.INPUT_SOURCE_KTV,
			TvCommonManager.INPUT_SOURCE_JPEG,
			TvCommonManager.INPUT_SOURCE_DTV2,
			TvCommonManager.INPUT_SOURCE_STORAGE2 };
	private TvCommonManager mTvCommonmanager;
	private Activity mActivity;
	private static final int FUNCTION_DISABLED = 0;
	private ArrayList<InputSourceItem> mGalleryItemList = new ArrayList<InputSourceItem>();
	private int mTvSystem = 0;
//	private int[] mSourceListFlag = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	   private int[] mSourceListFlag = new int[TvCommonManager.INPUT_SOURCE_NUM];
	private int VGANumber = 0;
    private int HDMI2Number = 0;
    private int HDMI3Number = 0;
	   private TvPvrManager mTvPvrManager = null;
	   private TvS3DManager mTvS3DManager = null;
	    public TvPipPopManager mTvPipPopManager = null;
	    private boolean mInitialPosition = false;
	public GetTvSource(Activity activity) {
		mTvCommonmanager = TvCommonManager.getInstance();
		mActivity = activity;
		mTvSystem = mTvCommonmanager.getCurrentTvSystem();
	}

	public ArrayList<InputSourceItem> getSource() {
     
        mTvPipPopManager = TvPipPopManager.getInstance();
        mTvS3DManager = TvS3DManager.getInstance();
        mTvPvrManager = TvPvrManager.getInstance();
		String[] tmpData = null;
		int PreviewSourceFlag = 0;
		int focusPosition = 0;
		int currentSource = 0;
		boolean[] sourceStatusList;

		getInputSourcelist();
		if (VGANumber > 1) {
			// More VGA items has add
			PreviewSourceFlag = VGANumber - 1;
		}

		if (mTvSystem == TvCommonManager.TV_SYSTEM_ATSC) {
			tmpData = mActivity.getResources().getStringArray(
					R.array.str_arr_atsc_input_source_vals);
		} else if (TvCommonManager.TV_SYSTEM_ISDB == mTvSystem) {
			tmpData = mActivity.getResources().getStringArray(
					R.array.str_arr_idsb_input_source_vals);
		} else {
			tmpData = mActivity.getResources().getStringArray(
					R.array.str_arr_input_source_vals);
		}
		mGalleryItemList.clear();
		sourceStatusList = mTvCommonmanager.GetInputSourceStatus();
        /*
         * Always display the ATV/DTV icon due to source detection function
         * cannot support to get ATV/DTV status.
         */
		sourceStatusList[TvCommonManager.INPUT_SOURCE_ATV] = true;
		sourceStatusList[TvCommonManager.INPUT_SOURCE_DTV] = true;

		currentSource = mTvCommonmanager.getCurrentTvInputSource();

		if (VGANumber > 1)
			appendUserDefineItems_VGA(tmpData[0]);

		for (int subSource = 0; subSource < tmpData.length; subSource++) {
			if (TvCommonManager.INPUT_SOURCE_NUM <= subSource) {
				break;
			}
			if (mSourceListFlag[subSource] == 0) {
				continue;
			}

			if (mTvPipPopManager.isPipModeEnabled() == true) {
				if (subSource == mTvCommonmanager.getCurrentTvInputSource()) {
					mInitialPosition = true;
					continue;
				}
				if (mTvPipPopManager.getCurrentPipMode() == TvPipPopManager.E_PIP_MODE_PIP) {
					if (mTvPipPopManager.checkPipSupportOnSubSrc(subSource) == false) {
						continue;
					}
				}
				if (mTvPipPopManager.getCurrentPipMode() == TvPipPopManager.E_PIP_MODE_POP) {
					int formatType = mTvS3DManager.getCurrent3dType();
					if (formatType == TvS3DManager.THREE_DIMENSIONS_TYPE_DUALVIEW) {
					} else {
						if (mTvPipPopManager.checkPipSupportOnSubSrc(subSource) == false) {
							continue;
						}
					}
				}
				if (mTvPipPopManager.getCurrentPipMode() == TvPipPopManager.E_PIP_MODE_TRAVELING) {
					int curSubSource = mTvCommonmanager
							.getCurrentTvSubInputSource();

					if (curSubSource == subSource) {
						continue;
					} else if (mTvPipPopManager.checkTravelingModeSupport(
							subSource, curSubSource) == false) {
						continue;
					}
				}
			}
			InputSourceItem inputSourceItem = new InputSourceItem();
			if (subSource == TvCommonManager.INPUT_SOURCE_VGA && VGANumber > 1) {
				inputSourceItem.setInputSourceName(tmpData[subSource] + VGANumber);
			} else {
				inputSourceItem.setInputSourceName(tmpData[subSource]);
			}
			inputSourceItem.setPositon(subSource);
			if (subSource != currentSource) {
				if (mTvSystem == TvCommonManager.TV_SYSTEM_ATSC) {
					if (TvCommonManager.INPUT_SOURCE_ATV == mTvCommonmanager
							.getCurrentTvInputSource()) {
						if (subSource == TvCommonManager.INPUT_SOURCE_DTV) {
							focusPosition = PreviewSourceFlag;
						}
					}
				}
			}

			if (sourceStatusList != null) {
				inputSourceItem.setSignalFlag(sourceStatusList[subSource]);
				if (mTvSystem == TvCommonManager.TV_SYSTEM_ATSC) {
					if (sourceStatusList[TvCommonManager.INPUT_SOURCE_ATV] == true) {
                        /*
                         * For ATSC system, soure list icon "TV" is used for ATV
                         * and DTV.
                         */
						sourceStatusList[TvCommonManager.INPUT_SOURCE_DTV] = true;
					}
				}
			} else {
				inputSourceItem.setSignalFlag(false);
			}
			mGalleryItemList.add(inputSourceItem);
			PreviewSourceFlag++;
		}

		insertUserDefineHDMIItems(tmpData);

		return mGalleryItemList;
	}
	private void insertUserDefineHDMIItems(String[] itemNames) {
		String userHDMIName = "";
		InputSourceItem inputSourceItem = null;
		if (HDMI3Number > 1) {
			userHDMIName = "OPS";//getResources().getString(R.string.str_HDMI_4);
			inputSourceItem = new InputSourceItem();
			inputSourceItem.setInputSourceName(userHDMIName);
			inputSourceItem.setPositon(TvCommonManager.INPUT_SOURCE_HDMI3);
			inputSourceItem.setUserDefineItemType(EnumUserDefinedType.E_DEFINED_HDMI3);
			appendUserDefineItems(itemNames[TvCommonManager.INPUT_SOURCE_HDMI2], inputSourceItem);
			//mGalleryItemList.add(insertPosition2 + 1, inputSourceItem);
		}
		if (HDMI2Number > 1) {
			userHDMIName = mActivity.getResources().getString(R.string.str_HDMI_DP);
			inputSourceItem = new InputSourceItem();
			inputSourceItem.setInputSourceName(userHDMIName);
			inputSourceItem.setPositon(TvCommonManager.INPUT_SOURCE_HDMI2);
			inputSourceItem.setUserDefineItemType(EnumUserDefinedType.E_DEFINED_HDMI_DP);
			appendUserDefineItems(itemNames[TvCommonManager.INPUT_SOURCE_HDMI2], inputSourceItem);
			//mGalleryItemList.add(insertPosition1 + 1, inputSourceItem);
		}

	}
	private void appendUserDefineItems(String insertedTargetSrcName, InputSourceItem item) {
		String itemName;
		boolean isSrcExist = false;
		for (int i = 0; i < mGalleryItemList.size(); i++) {
			itemName = mGalleryItemList.get(i).getInputSourceName();
			if (itemName.equals(insertedTargetSrcName)) {
				isSrcExist = true;
				mGalleryItemList.add(i + 1, item);
				break;
			}
		}
		if (!isSrcExist) {
			mGalleryItemList.add(item);
		}
	}
	private void getInputSourcelist() {
		int[] sourceList;

		sourceList = mTvCommonmanager.getSourceList();
		if (sourceList != null) {
			for (int i = 0; (i < sourceList.length)
					&& (TvCommonManager.INPUT_SOURCE_NUM > i); i++) {
				if (i == TvCommonManager.INPUT_SOURCE_ATV
						&& mTvSystem == TvCommonManager.TV_SYSTEM_ATSC) {
					mSourceListFlag[i] = FUNCTION_DISABLED;
					continue;
				} else if (i == TvCommonManager.INPUT_SOURCE_VGA) {
					VGANumber = sourceList[i];
				} else if (i == TvCommonManager.INPUT_SOURCE_HDMI2) {
					HDMI2Number = sourceList[i];
				} else if (i == TvCommonManager.INPUT_SOURCE_HDMI3) {
					HDMI3Number = sourceList[i];
				}
				mSourceListFlag[i] = sourceList[i];
			}
		}
		for (int i = 0; (i < mSourceListInvisible.length) && (i < mSourceListFlag.length); i++) {
			if (TvCommonManager.INPUT_SOURCE_NUM > mSourceListInvisible[i]) {
				mSourceListFlag[mSourceListInvisible[i]] = FUNCTION_DISABLED;
			}
		}
	}
	private void appendUserDefineItems_VGA(String vgaName) {
		InputSourceItem inputSourceItem = null;
		if (VGANumber > 1) {
			for (int i = 0; i < (VGANumber - 1); i++) {
				inputSourceItem = new InputSourceItem();
				inputSourceItem.setInputSourceName(vgaName + (i + 1));
				inputSourceItem.setPositon(TvCommonManager.INPUT_SOURCE_VGA);
				inputSourceItem.setUserDefineItemType(EnumUserDefinedType.values()[i + 1]);
				mGalleryItemList.add(inputSourceItem);
			}
		}
	}

}
