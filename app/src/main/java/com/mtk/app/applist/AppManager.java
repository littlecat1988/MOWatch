
package com.mtk.app.applist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mtk.app.appstore.AppStoreManager;
import com.mtk.app.appstore.RemoteAppInfo;

import android.os.Environment;
import android.util.Log;

public class AppManager {
    private static final String HIDE_APP_DIGITAL_CLOCK = "Digital Clock";
    private static final String HIDE_APP_CODOON = "Codoon";

    public static final String APP_MANAGER_CONFIG_FILE_NAME = ".xml";

    public static final String APP_MANAGER_VXP_FILE_NAME = ".vxp";

    public static final String APP_MANAGER_FILE_DIR = "/appmanager/";

    public static final String BT_SMART_WATCH_RIGSTER_APP_ACTION = "com.mtk.smartwatch.rigster.app";

    private ArrayList<AppInfo> mAppList;

    private HashMap<String, AppInfo> mVxpAppMap = new HashMap<String, AppInfo>();

    private static AppManager mInstance;

    public static String getFilepath(String filename) {
        String filePath = (APP_MANAGER_FILE_DIR + "/" + filename);
        return filePath;
    }

    private AppManager() {
        mAppList = new ArrayList<AppInfo>();
    }

    public int getApplength() {
        return mAppList.size();
    }

    public AppInfo getAppInfo(int index) {
        return mAppList.get(index);
    }

    public void addAppInfo(AppInfo appInfo) {
        mAppList.add(appInfo);
    }

    public void removeAppInfo(AppInfo appInfo) {
        mAppList.remove(appInfo);
    }

    public AppInfo getAppInfoByVxp(String vxpName) {
        return mVxpAppMap.get(vxpName);
    }

    public void initVxpMap() {
        mVxpAppMap.clear();
        for (int i = 0; i < getApplength(); i++) {
            AppInfo appInfo = getAppInfo(i);
            for (int j = 0; j < appInfo.getVxpNum(); j++) {
                mVxpAppMap.put(appInfo.getVxpName(j), appInfo);
            }
        }
    }

    public void refreshAppInfo() {
        mAppList.clear();
        String fileRoot = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileRoot = Environment.getExternalStorageDirectory() + APP_MANAGER_FILE_DIR;
        } else {
            fileRoot = Environment.getRootDirectory() + APP_MANAGER_FILE_DIR;
        }
        File dir = new File(fileRoot);
        readAppInfo(dir);
        initVxpMap();
    }

    public static AppManager getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new AppManager();
        }
        return mInstance;
    }

    private void readAppInfo(File dir) {
        File[] appFiles = dir.listFiles();
        if (appFiles == null) {
            return;
        }
        for (int i = 0; i < appFiles.length; i++) {
            if (appFiles[i].getName().endsWith(".xml")) {
                try {
                	Log.e("gomtel","fileName= "+appFiles[i].getName());
                    AppInfo appInfo = new AppInfo();
                    FileInputStream fis = new FileInputStream(dir + "/" + appFiles[i].getName());
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document dom = builder.parse(fis);

                    Element root = dom.getDocumentElement();
                    NodeList items = root.getChildNodes();

                    if (items.getLength() != 0) {
                        appInfo.SetAppInfo(items);

                        String vxpName = appInfo.getReceiverId();
                        String iconName = appInfo.getIconName();
                        for (int j = 0; j < appFiles.length; j++) {
                            for (int k = 0; k < appInfo.getVxpNum(); k++) {
                                if (vxpName != null
                                        && appFiles[j].getName().equals(appInfo.getVxpName(k))) {
                                    appInfo.setVxpPath(appFiles[j].getPath(), k);

                                }
                            }
                            if (iconName != null && appFiles[j].getName().equals(iconName + ".png")) {
                                appInfo.setIconPath(appFiles[j].getPath());
                            }
                        }
                        if (appInfo.getVxpPath(0) != null) {
                        	if(!appInfo.getAppName().equals(HIDE_APP_DIGITAL_CLOCK) && !appInfo.getAppName().equals(HIDE_APP_CODOON)){
                        		mAppList.add(appInfo);
//                        		Log.e("AppManager","appInfo= "+appInfo.getIconName()+"   "+appInfo.getAppName());
            				}
//                        	mAppList.add(appInfo);//modified by lixiang for hide some apps 20150603
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
