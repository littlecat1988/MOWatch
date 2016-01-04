
package com.mtk.app.applist;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.text.TextUtils;
import android.util.Log;

public class AppInfo {

    private static final String TAG = "AppInfo";

    public static final int SMARTWATCH_APP_CATEGORY_LOCAL = 1;

    public static final int SMARTWATCH_APP_CATEGORY_THIRDPARTY = 2;

    public static final int SMARTWATCH_VXP_TYPE_INSTALL = 1;

    public static final int SMARTWATCH_VXP_TYPE_PUSHONLY = 2;

    private int mAppCategory;

    private int mVxpNum;

    private String mNormalVxpName = "";

    private boolean mIsInLocal = true;

    private boolean mIsPreInstalled = false;

    private int mTotalVxpSize;

    private String mReceiverid;

    private String mPackageName;

    private String mDownloadUrl;

    private ArrayList<VxpInfo> mVxpInfo;

    // private HashMap<Integer, String>mVxpName;
    // private String mVxpPath;
    // private String mVxpName;
    private String mAppName;

    private int mVersionNumber;

    private String mIconName;

    private String mIconPath;

    private class VxpInfo {
        private int vxpType;

        private String vxpName;

        private String vxpPath;

        private int vxpSize;

        private int vxpVersion;

        public VxpInfo(int type, String name, String vxpPath, int version) {
            vxpType = type;
            vxpName = name;
            this.vxpPath = vxpPath;
            vxpVersion = version;
            if (vxpType == SMARTWATCH_VXP_TYPE_INSTALL) {
                mNormalVxpName = vxpName;
            }
        }

        public int getType() {
            return vxpType;
        }

        public int getVxpVersion() {
            return vxpVersion;
        }

        public String getVxpName() {
            return vxpName;
        }

        public String getVxpPath() {
            return vxpPath;
        }

        public void setVxpPath(String path) {
            vxpPath = path;
        }

        public void setVxpSize(int size) {
            vxpSize = size;
        }

        public int getVxpSize() {
            return vxpSize;
        }
    };

    public AppInfo() {
        mAppCategory = 0;
        mReceiverid = null;
        mPackageName = null;
        mDownloadUrl = null;
        /*
         * mVxpPath = null; mIconName = null;
         */
        mVxpInfo = new ArrayList<AppInfo.VxpInfo>();

    }

    public AppInfo(String appName, String normalvxp, boolean inLocal) {
        mAppName = appName;
        mReceiverid = appName;
        mIsInLocal = inLocal;
        mNormalVxpName = normalvxp;
    }

    public int getTotalVxpSize() {
        return mTotalVxpSize;
    }

    public String getReceiverId() {
        return mReceiverid;
    }

    public int getAppCategory() {
        return mAppCategory;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public int getVersionNumber() {
        return mVersionNumber;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public String getVxpPath(int index) {
        return mVxpInfo.get(index).getVxpPath();
    }

    public int getVxpNum() {
        return mVxpNum;
    }

    public String getIconName() {
        return mIconName;
    }

    public String getIconPath() {
        return mIconPath;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getVxpName(int index) {
        return mVxpInfo.get(index).getVxpName();
    }

    public boolean isInLocal() {
        return mIsInLocal;
    }

    public boolean isPreInstalled() {
        return mIsPreInstalled;
    }

    public void setPreInstalled(boolean isPreInstalled) {
        mIsPreInstalled = isPreInstalled;
    }

    public String getNormalVxpName() {
        return mNormalVxpName;
    }

    public int getVxpType(int index) {
        return mVxpInfo.get(index).getType();
    }

    public int getVxpVersion(int index) {
        return mVxpInfo.get(index).getVxpVersion();
    }

    public void setVxpPath(String vxpPath, int index) {
        // mVxpPath.add(vxpPath);
        mVxpInfo.get(index).setVxpPath(vxpPath);
    }

    public void setIconPath(String iconPath) {
        mIconPath = iconPath;
    }

    public void setVxpSize(int index, int size) {
        mVxpInfo.get(index).setVxpSize(size);
        mTotalVxpSize = 0;
        for (int i = 0; i < mVxpNum; i++) {
            mTotalVxpSize += mVxpInfo.get(i).getVxpSize();
        }
    }

    public void SetAppInfo(NodeList nodelist) {
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = (Node) nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childNode = (Element) node;
                if ("recevier_id".equals(childNode.getNodeName())) {
                    mReceiverid = childNode.getFirstChild().getNodeValue();
                    // mVxpName = mRecieverid + ".vxp";
                } else if ("vxp".equals(childNode.getNodeName())) {
                    NodeList vxpNodeList = childNode.getChildNodes();
                    String number = childNode.getAttribute("num");
                    if (number != null) {
                        mVxpNum = Integer.parseInt(number);
                    } else {
                        mVxpNum = 0;
                    }
                    for (int j = 0; j < vxpNodeList.getLength(); j++) {
                        Node vxpNode = vxpNodeList.item(j);
                        if (vxpNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element vxpElement = (Element) vxpNode;
                            if ("vxpname".equals(vxpElement.getNodeName())) {
                                int platform_type = 0;
                                int version = 0;
                                if (vxpElement.getAttribute("platform").equals("normal") || vxpElement.getAttribute("platform").equals("install")) {
                                    platform_type = SMARTWATCH_VXP_TYPE_INSTALL;
                                    if (!TextUtils.isEmpty(vxpElement.getAttribute("vxpversion"))) {
                                        version = Integer.valueOf(vxpElement
                                                .getAttribute("vxpversion"));
                                    }
                                } else {
                                    platform_type = SMARTWATCH_VXP_TYPE_PUSHONLY;
                                }
                                VxpInfo vxpInfo = new VxpInfo(platform_type, vxpElement
                                        .getFirstChild().getNodeValue(), null, version);
                                mVxpInfo.add(vxpInfo);
                                if (mVxpNum == 0) {
                                    mVxpNum++;
                                }
                            }
                        }
                    }

                } else if ("category".equals(childNode.getNodeName())) {
                    String category = childNode.getFirstChild().getNodeValue();
                    if (category.equals("local")) {
                        mAppCategory = AppInfo.SMARTWATCH_APP_CATEGORY_LOCAL;
                    } else if (category.equals("thirdparty")) {
                        mAppCategory = AppInfo.SMARTWATCH_APP_CATEGORY_THIRDPARTY;
                    }
                } else if ("package".equals(childNode.getNodeName())) {
                    mPackageName = childNode.getFirstChild().getNodeValue();
                } else if ("iconname".equals(childNode.getNodeName())) {
                    mIconName = childNode.getFirstChild().getNodeValue();
                } else if ("download_url".equals(childNode.getNodeName())) {
                    mDownloadUrl = childNode.getFirstChild().getNodeValue();
                } else if ("appname".equals(childNode.getNodeName())) {
                    mAppName = childNode.getFirstChild().getNodeValue();
                } else if ("version".equals(childNode.getNodeName())) {
                    String version = childNode.getFirstChild().getNodeValue();
                    try {
                        Log.d(TAG, "SetAppInfo version = " + version);
                        mVersionNumber = Integer.valueOf(version);
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "SetAppInfo NumberFormatException");
                    }
                }
            }
        }
    }
}
