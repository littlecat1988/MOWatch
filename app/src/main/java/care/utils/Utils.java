package care.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.gsm.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import care.application.XcmApplication;

public class Utils {

    private Utils() {
    }

    public static void makeToast(Context ctx, int resId) {
        makeToast(ctx, ctx.getString(resId));
    }

    public static void makeToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getImageByPath(File image, long limitsize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        long len = image.length();
        if (len > limitsize) {
            opts.inSampleSize = (int) Math.sqrt(len / limitsize);
        }
        return BitmapFactory.decodeFile(image.getAbsolutePath(), opts);
    }

    public static boolean isSDAvailable() {
        String ExternalStorageState = Environment.getExternalStorageState();
        if (ExternalStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINESE);
        return sdf.format(System.currentTimeMillis());
    }


    public static boolean isWifiEnabled(Context ctx) {
        ConnectivityManager man = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return man.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static boolean isNetworkEnable(Context ctx) {
        ConnectivityManager man = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = man.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isAvailableEmail(String strEmail) {
        Pattern p = Pattern
                .compile("^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    // 检查手机号码是否正确
    public static boolean isPhoneNumber(String phoneNumber) {
        String strPattern = "\\d{11}";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    // public static String md5(String s) {
    // char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
    // 'B', 'C', 'D', 'E', 'F' };
    // try {
    // MessageDigest md5 = MessageDigest.getInstance("sha");
    // byte[] bytes = md5.digest(s.getBytes("utf-8"));
    // int count = bytes.length;
    // StringBuilder builder = new StringBuilder(count * 2);
    // for (int i = 0; i < count; i++) {
    // builder.append(hex[bytes[i] >> 4 & 0xf]);
    // builder.append(hex[bytes[i] & 0xf]);
    // }
    // return builder.toString();
    // } catch (NoSuchAlgorithmException e) {
    // } catch (UnsupportedEncodingException e) {
    // }
    // return null;
    // }

    public static Object getDirectary(Context ctx, int directoryRoot) {
        // TODO Auto-generated method stub
        return null;
    }

    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSHAValue(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("SHA");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

//	// 加密
//	public static String Encrypt(String sSrc) throws Exception {
//		
//		String sKey = Constants.SERVICE_KEY;
//		String ivkey =  Constants.SERVICE_IV;
//		if (sKey == null) {
//			System.out.print("Key为空null");
//			return null;
//		}
//		// 判断Key是否为16位
//		if (sKey.length() != 16) {
//			System.out.print("Key长度不是16位");
//			return null;
//		}
//		byte[] raw = sKey.getBytes();
//		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
//		IvParameterSpec iv = new IvParameterSpec(ivkey.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
//		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
//
//		byte2hex(encrypted);
//
//		return byte2hex(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
//	}

    // /** 字节数组转成16进制字符串 **/
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString(); // 默认小写
        // return sb.toString().toUpperCase(); // 转成大写
    }


    public static byte[] readBytes(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readString(InputStream is) {
        return new String(readBytes(is));
    }


    public static JSONObject getJSON(String sb) throws JSONException {
        return new JSONObject(sb);
    }

    public static String GetService(JSONObject jsons, String request) {
        URL url;
        try {
            url = new URL(Constants.SERVICE + request);

            String belongProject = XcmApplication.getInstance().gettMetaData();
            jsons.put("belong_project",belongProject);

            String content = jsons.toString();
            Log.i("lk",content+"");
            // 现在呢我们已经封装好了数据,接着呢我们要把封装好的数据传递过去
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 设置允许输出
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            // 设置User-Agent: Fiddler
            conn.setRequestProperty("ser-Agent", "Fiddler");
            // 设置contentType
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(content.getBytes());
            os.close();
            // 服务器返回的响应码
            int code = conn.getResponseCode();
            System.out.println("cc = " + code);
            if (code == 200) {
                // 等于200了,下面呢我们就可以获取服务器的数据了
                InputStream is = conn.getInputStream();
                String json = Utils.readString(is);
                // text.setText("成功：" + json.toString());
                return json.toString();

            } else {
                InputStream ns = conn.getInputStream();
                // text.setText("失败：" + ns.toString());
                return "0";

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "-1";
    }

    public static HashMap<String, String> getJSONParserResult(String JSONString)
            throws JSONException {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (JSONString != null) {
            JSONObject result = new JSONObject(JSONString);
            Iterator<String> keys = result.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = result.getString(key);
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }


    /**
     * 获取当前时间
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);


        return str;
    }

    /**
     * 获取一个月以前的时间
     */
    public static String getOldTime() {

        long start_time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int yigeyue = 30 * 24 * 60 * 60 * 1000;
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(start_time + yigeyue))));


        return sd;
    }

    public static String bitmaptoString(int bitmapQuality, ImageView img) {

        img.setDrawingCacheEnabled(true);
        Bitmap bmp = img.getDrawingCache();
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        img.setDrawingCacheEnabled(false);
        return string;

    }

    /**
     * 　　* 将base64转换成bitmap图片 　　* 　　* @param string base64字符串 　　* @return bitmap
     *
     */
    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static String GetVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;

            return "V" + version;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int GetVersionNumber(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void SendSms(String content, String phone) {

        SmsManager smsManager = SmsManager.getDefault();
        List<String> texts = smsManager.divideMessage(content);
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            smsManager.sendTextMessage(phone, null, content, null, null);
        }

    }

    public static String GetUrlAppName(String url) {

        String url2[] = {};
        url2 = url.split("/");
        return url2[url2.length - 1];

    }

    public static int AnalyticalQinqingLength(String date) {
        String[] result = date.split(";");
        return result.length;
    }

    public static String AnalyticalQinqingDate(int type, int code,
                                               String olddate) {
        String result = "";
        String[] date = olddate.split(";");
        String dd[] = date[code].split(",");
        result = dd[type];

        return result;
    }

    public static boolean GuoLvShiQu(String suzu, String str) {

        boolean result = true;
        if (suzu.equals("") || suzu.equals(null)) {
            result = true;
        } else {
            String number[] = suzu.split(",");

            for (int i = 0; i < number.length; i++) {

                if (number[i].equals(str)) {

                    result = false;
                    break;
                }
            }

        }


        return result;

    }

}
