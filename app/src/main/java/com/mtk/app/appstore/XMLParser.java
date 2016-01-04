
package com.mtk.app.appstore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.TextUtils;
import com.mtk.app.appstore.RemoteAppInfo.VxpInfo;

import android.util.Log;

public class XMLParser {

    private static final String TAG = "AppManager/XMLParser";

    // Never instantiate this class.
    private XMLParser() {
    }

    // parse server AppList xml
    public static void parseAppList(InputStream intput) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(intput);

            Element root = dom.getDocumentElement();
            NodeList items = root.getChildNodes();

            if (items.getLength() != 0) {
                // parse AppList abs_path
                // absPath =
                // ((Node)(root.getElementsByTagName("abs_path").item(0))).getFirstChild().getNodeValue();
                String absPath = findNodeValue(items, "abs_path");
                AppStoreManager.getInstance().setAbsPath(absPath);
                Log.d(TAG, "[parseAppList] findAbsPath = " + absPath);

                for (int i = 0; i < items.getLength(); i++) {
                    Node node = (Node) items.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element childNode = (Element) node;
                        if ("app".equals(childNode.getNodeName())) {
                            NodeList appNodes = node.getChildNodes();
                            RemoteAppInfo appInfo = parseAppInfo(appNodes);
                            if (appInfo.isAvailable()) {
                                AppStoreManager.getInstance().addAppInfo(appInfo);
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "[parseAppList] FileNotFoundException: " + e.getMessage());
        } catch (SAXException e) {
            Log.d(TAG, "[parseAppList] SAXException: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "[parseAppList] IOException: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "[parseAppList] ParserConfigurationException: " + e.getMessage());
        }
    }

    private static String findNodeValue(NodeList items, String tag) {
        for (int i = 0; i < items.getLength(); i++) {
            Node node = (Node) items.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childNode = (Element) node;
                if (tag != null && tag.equals(childNode.getNodeName())) {
                    return childNode.getFirstChild().getNodeValue();
                }
            }
        }
        return null;
    }

    private static RemoteAppInfo parseAppInfo(NodeList nodelist) {
        RemoteAppInfo appInfo = new RemoteAppInfo();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = (Node) nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childNode = (Element) node;
                if ("recevier_id".equals(childNode.getNodeName())) {
                    String recieverID = childNode.getFirstChild().getNodeValue();
                    appInfo.setReceiverID(recieverID);
                } else if ("category".equals(childNode.getNodeName())) {
                    String category = childNode.getFirstChild().getNodeValue();
                    if (category.equals("local")) {
                        appInfo.setAppCategory(RemoteAppInfo.SMARTWATCH_APP_CATEGORY_LOCAL);
                    } else if (category.equals("thirdparty")) {
                        appInfo.setAppCategory(RemoteAppInfo.SMARTWATCH_APP_CATEGORY_THIRDPARTY);
                    }
                } else if ("path".equals(childNode.getNodeName())) {
                    String appPath = childNode.getFirstChild().getNodeValue();
                    appInfo.setAppPath(appPath);
                } else if ("appname".equals(childNode.getNodeName())) {
                    String appname = childNode.getFirstChild().getNodeValue();
                    appInfo.setAppName(appname);
                } else if ("vxp".equals(childNode.getNodeName())) {
                    NodeList vxpNodeList = childNode.getChildNodes();
                    String number = childNode.getAttribute("num");
                    if (number != null) {
                        appInfo.setVxpNum(Integer.parseInt(number));
                    } else {
                        appInfo.setVxpNum(0);
                    }
                    for (int j = 0; j < vxpNodeList.getLength(); j++) {
                        Node vxpNode = vxpNodeList.item(j);
                        if (vxpNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element vxpElement = (Element) vxpNode;
                            if ("vxpname".equals(vxpElement.getNodeName())) {
                                int platform_type = 0;
                                if (vxpElement.getAttribute("platform").equals("normal")) {
                                    platform_type = RemoteAppInfo.SMARTWATCH_VXP_TYPE_NORMAL;
                                } else {
                                    platform_type = RemoteAppInfo.SMARTWATCH_VXP_TYPE_TINY;
                                }
                                String vxpURL = AppStoreManager.getInstance().getAbsPath()
                                        + appInfo.getAppPath() + vxpElement.getAttribute("path");
                                VxpInfo vxpInfo = appInfo.new VxpInfo(platform_type, vxpElement
                                        .getFirstChild().getNodeValue(), vxpURL);
                                appInfo.addVxpInfo(vxpInfo);
                            }
                        }
                    }
                } else if ("iconname".equals(childNode.getNodeName())) {
                    String iconName = childNode.getFirstChild().getNodeValue();
                    appInfo.setIconName(iconName);
                    String iconURL = AppStoreManager.getInstance().getAbsPath()
                            + appInfo.getAppPath() + childNode.getAttribute("path");
                    appInfo.setIconURL(iconURL);
                } else if ("apk_url".equals(childNode.getNodeName())) {
                    String apkURL = AppStoreManager.getInstance().getAbsPath()
                            + appInfo.getAppPath() + childNode.getFirstChild().getNodeValue();
                    appInfo.setApkURL(apkURL);
                } else if ("apk_package".equals(childNode.getNodeName())) {
                    String apkPackage = childNode.getFirstChild().getNodeValue();
                    appInfo.setApkPackageName(apkPackage);
                } else if ("provider".equals(childNode.getNodeName())) {
                    String provider = childNode.getFirstChild().getNodeValue();
                    appInfo.setProvider(provider);
                } else if ("version".equals(childNode.getNodeName())) {
                    String version = childNode.getFirstChild().getNodeValue();
                    appInfo.setVersion(version);
                } else if ("date".equals(childNode.getNodeName())) {
                    String release_date = childNode.getFirstChild().getNodeValue();
                    appInfo.setReleaseDate(release_date);
                } else if ("size".equals(childNode.getNodeName())) {
                    String appSize = childNode.getFirstChild().getNodeValue();
                    appInfo.setAppSize(appSize);
                } else if ("introduction".equals(childNode.getNodeName())) {
                    String introduction = childNode.getFirstChild().getNodeValue();
                    appInfo.setIntroduction(introduction);
                } else if ("sample_image".equals(childNode.getNodeName())) {
                    String sampleName = childNode.getFirstChild().getNodeValue();
                    appInfo.setSampleName(sampleName);
                    String sampleURL = AppStoreManager.getInstance().getAbsPath()
                            + appInfo.getAppPath() + childNode.getAttribute("path");
                    appInfo.setSampleURL(sampleURL);
                }
            }
        }
        appInfo.printInfo();
        return appInfo;
    }

    // write AppManager appconfig xml
    public static void writeAppConfigXml(RemoteAppInfo appInfo) {
        String xmlFilePath = FileUtils.getAppConfigFile(appInfo.getReceiverID());
        Log.d(TAG, "[writeAppConfigXml] createAppConfigFile = " + xmlFilePath);
        File xmlFile = new File(xmlFilePath);
        if (xmlFile != null && xmlFile.isFile() && xmlFile.exists() && xmlFile.length() > 0) {
            Log.d(TAG, "[writeAppConfigXml] old exist file = " + xmlFilePath);
            if (xmlFile.delete()) {
                Log.d(TAG, "[writeAppConfigXml] delete successfully");
            } else {
                Log.d(TAG, "[writeAppConfigXml] delete fail");
            }
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("appconfig");
            doc.appendChild(root);

            Element name = doc.createElement("category");
            root.appendChild(name);
            String category = appInfo.getAppCategory() == RemoteAppInfo.SMARTWATCH_APP_CATEGORY_LOCAL ? "local"
                    : "thirdparty";
            Node textName = doc.createTextNode(category);
            name.appendChild(textName);

            Element recevier_id = doc.createElement("recevier_id");
            root.appendChild(recevier_id);
            Node textRecevierID = doc.createTextNode(appInfo.getReceiverID());
            recevier_id.appendChild(textRecevierID);

            Element appName = doc.createElement("appname");
            root.appendChild(appName);
            Node textAppName = doc.createTextNode(appInfo.getAppName());
            appName.appendChild(textAppName);

            // vxp list
            Element vxp = doc.createElement("vxp");
            root.appendChild(vxp);
            vxp.setAttribute("num", String.valueOf(appInfo.getVxpNum()));

            for (int i = 0; i < appInfo.getVxpNum(); i++) {
                Element vxpName = doc.createElement("vxpname");
                vxp.appendChild(vxpName);
                String vxpType = appInfo.getVxpType(i) == RemoteAppInfo.SMARTWATCH_VXP_TYPE_NORMAL ? "normal"
                        : "tiny";
                vxpName.setAttribute("platform", vxpType);
                Node textVxpName = doc.createTextNode(appInfo.getVxpName(i));
                vxpName.appendChild(textVxpName);
            }

            Element packageName = doc.createElement("package");
            root.appendChild(packageName);
            Node textpackageName = doc.createTextNode(appInfo.getApkPackageName());
            packageName.appendChild(textpackageName);

            Element iconName = doc.createElement("iconname");
            String icon = appInfo.getIconName();
            if (icon.indexOf('.') != -1) {
                icon = icon.substring(0, icon.indexOf('.'));
            }
            root.appendChild(iconName);
            Node textIconName = doc.createTextNode(icon);
            iconName.appendChild(textIconName);

            Element downloadURL = doc.createElement("download_url");
            root.appendChild(downloadURL);
            Node textDownloadURL = doc.createTextNode(appInfo.getApkURL());
            downloadURL.appendChild(textDownloadURL);

            Element version = doc.createElement("version");
            root.appendChild(version);
            Node textVersion = doc.createTextNode(appInfo.getVersion());
            version.appendChild(textVersion);

            // write xml file
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();

            Properties properties = t.getOutputProperties();
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperties(properties);

            DOMSource source = new DOMSource(doc);
            StreamResult re = new StreamResult(xmlFilePath);
            t.transform(source, re);
            Log.d(TAG, "[writeAppConfigXml] successfully ");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
