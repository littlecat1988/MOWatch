package care.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Constants {

    public static int WIDTH = 0;
    public static int HEIGHT = 0;

    //
    public static String USERID = "0";
    public static String USERNICKNAME = "0";
    public static String USERHEADURL = "0";
    public static boolean IS_OPEN_NETWORK = false;    

    public static String DEVICEID = "0";
    public static String PHONE = "0";
    public static final String HOSTIP="220.231.193.48"; 
//  public static final String HOSTIP = "192.168.1.65";
    public static final int PORT =8080;

    //广播机制
    public static final String INTERFILTER = "org.care.message.service.INTERFILTER";
    public static final String GETDOWNLOADURL = "org.care.message.service.GETDOWNLOADURL";
    /**
     * activity存储类
     */
    public static final String LOGINACTIVITY = "login_activity";
    public static final String REGISTERACTIVITY = "register_activity";
    public static final String BAOBEIMANAGERACTIVITY = "baobei_manager_activity";
    public static final String ADDTOYACTIVITY = "add_activity";
    public static final String BAOBEIINFO_CHOSE_ACTIVITY = "BaoBeiInfoChooseActivity";
//  public static final String SERVICE = "http://192.168.8.25:9888/GXCareDevice" ;
//  public static final String SERVICE = "http://server.ghero.cn:8080/GXCareDevice";
//    public static final String SERVICE = "http://sd.g168.com:8080/GXCareDevice";
    public static final String SERVICE = "http://sd.g168.com:8080/GTSmartDevice";
    public static final String REGISTER = SERVICE + "/doRegister.do";
    public static final String LOGIN = SERVICE + "/doLogin.do";
    public static final String LISTEN = "/doListen.do";
    public static final String LOCATION = "/doLocation.do";
    public static final String FIND = "/doFindBaby.do";
    public static final String LOCUS = "/doQueryDeviceTrack.do";
    public static final String FENCE_EDIT = "/doModifyDeviceSafeArea.do";
    public static final String FENCE_DELETE = "/doDeleteDeviceSafeArea.do";
    public static final String FENCE_GET = "/doGetDeviceSafeArea.do";
    public static final String DOWNLOAD_DATA = "/doDownLoadDeviceData.do";
    public static final String VERIFYCODE = SERVICE + "/doVerifyDevice.do";
    public static final String ADDDEVICE = SERVICE + "/doAddDevice.do";
    public static final String QinQingAdd = SERVICE + "/doAddDeviceFamily.do";
    public static final String QinQingDelete = SERVICE + "/doDeleteDeviceFamily.do";
    public static final String QinQingMof = SERVICE + "/doModifyDeviceFamily.do";
    public static final String FEEDBACK = SERVICE + "/doFeedback.do";
    public static final String BABYSETTING = SERVICE + "/doAddDevice.do";
    public static final String UPDATEAPP = SERVICE + "/doUpdateApp.do";
    public static final String GETMESSAGE = SERVICE + "/doGetMsg.do";
    public static final String BABYCONTROL = "/doSetDeviceData.do";
    public static final String BABYSHARE = "/doShareDevice.do";
    public static final String BABYDELETE = "/doDeleteDevice.do";
    public static final String DOGETFALL = SERVICE + "/doGetFall.do";   //防脱落接口
    public static final String REMOTE = SERVICE + "/REMOTE.do";
    public static final String GET_LOWELECTRICITY = SERVICE + "/GET_LOWELECTRICITY.do"; //低电量接口
    public static final String SET_CLOCK = SERVICE + "/SET_CLOCK.do";
    public static final String SET_SLEEP = SERVICE + "/SET_SLEEP.do";
    public static final String SET_DISTURB = SERVICE + "/SET_DISTURB.do";
    public static final String GET_CLOCK = SERVICE + "/GET_CLOCK.do";
    public static final String GET_SLEEP = SERVICE + "/GET_SLEEP.do";
    public static final String GET_DISTURB = SERVICE + "/GET_DISTURB.do";

    public static final String PERSONETTING = SERVICE + "/doModifyUser.do";
    public static String userID, userPassword, userName, area, sex, phone, deviceIMEI, isAdministrator, deviceID = "0";
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String CHAT_VOICE_PATH = SD_CARD_PATH + "/care/Voice";
    public static final String HEADER_IMAGE_PATH = SD_CARD_PATH + "/care/Pic/myHeader";   //头像地址
    public static final String GROUP_HEADER_IMAGE_PATH = SD_CARD_PATH + "/care/Pic/groupmembers";
    public static final String BABYHEADER_IMAGE_NAME = "babyHeader.png";
    public static final String USERHEADER_IMAGE_NAME = "userHeader.png";
    public static final String GROUPHEADER_IMAGE_NAME = "groupHeader.png";
    public static boolean mOnHeartBeat = false;
    public static boolean mAppLogout = false;
    public static int mNoSee, mUnbind, mRefreshShowAllGroupsUI;

    //长连接接口
    public static final String USER_LOGIN_ADDRESS = "USER_LOGIN";
    public static final String MSG_SENDMSG = "MSG_SENDMSG";

    private static DisplayMetrics displayMetrics = new DisplayMetrics();
    
    public static String getCurrentTime(long date) {
        return String.valueOf(date);
    }

    /**
     *电话号码验证
     */
    public static boolean isMobileNO(String mobileNums) {
        String telRegex = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    public static boolean isCeshi(String mobileNums) {

        if (mobileNums.contains("ghero"))
            return true;
        else
            return false;
    }


    public static int getWinHeight(Activity activity) {
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    public static int getWinWidth(Activity activity) {
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static Timestamp getTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public static long timeDiff(String dateStr1, String dateStr2, String format) {
        long diff = 0;
        if ("0000-00-00 00:00".equals(dateStr2)) {
            diff = -1;
        } else {
            DateFormat df = new SimpleDateFormat(format);
            try {
                Date d1 = df.parse(dateStr1);
                Date d2 = df.parse(dateStr2);
                diff = d1.getTime() - d2.getTime();
            } catch (Exception e) {
                return -2;
            }
        }

        return diff;
    }

    public static byte[] getContent(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            return null;
        }

        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * fun name: getContent
     *
     * @param FilePathName
     * @return
     * @description get the file content(byte array) through the file
     */
    public static byte[] getContent(String FilePathName) {
        File file = new File(FilePathName);
        if (!file.exists()) {
            return null;
        }
        try {
            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                return null;
            }
            FileInputStream fi = new FileInputStream(file);
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length
                    - offset)) >= 0) {
                offset += numRead;
            }
            if (offset != buffer.length) {  

            }
            fi.close();
            return buffer;
        } catch (IOException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            StringBuffer sb = new StringBuffer();
            sb.append(result);
            return null;
        }
    }

    public static Bitmap bitmapDecode(String picPath) {
        try {
            FileDescriptor fd = new FileInputStream(picPath).getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            options.inSampleSize = 5;
            try {
                options.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeFile(picPath, options);
                return bmp == null ? null : bmp;
            } catch (OutOfMemoryError err) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
      
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;

            left = 0;
            top = 0;
            right = width;
            bottom = width;

            height = width;

            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;

            float clip = (width - height) / 2;

            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;

            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

/*		try{
			
		}catch (OutOfMemoryError e) {  
			while(bitmap == null) {  
				System.gc();  
				System.runFinalization();  
				bitmap = createBitmap(width, height, config); 
			}
		}*/


        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        // fill all the caves
        canvas.drawARGB(0, 0, 0, 0);

        // as follow,there is two ways to draw circle: drawRounRect/drawCircle
        // draw RoundRec;
        // first parameter: the graph show area
        // second parameter: Horizontal Round radius
        // third parameter: vertical Round radius
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // with Mode.SRC_IN, merge the bitmap and the draw circle
        canvas.drawBitmap(bitmap, src, dst, paint);

        return output;
    }

    public static Timestamp strToTimestamp(String time) {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        try {
            ts = Timestamp.valueOf(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ts;
    }

    public static String getNowTime(long time, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return df.format(c.getTime());
    }

    public static void DeleteFileContent(String FilePathName) {
        File f = new File(FilePathName);
        if (f.exists()) {
            f.delete();
        }
    }

    public static void CreateFileContent(String FilePathName) {
        File f = new File(FilePathName);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * 转码
     */
    public static String transUTF(String code) {
        String temp = "";
        try {
            temp = URLEncoder.encode(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 转码
     *
     * @param bitmap
     * @return
     */
    public static String getBitmapBaseCode(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
    }

    public static String getCurrentTimeLong() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.getTime().getTime());
    }

    /**
     * 获得当前版本信息
     */
    public static int getCurrentVersion(Context context) {
        int versionCode = 1;
        try {
            // 获取应用包信息
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = info.versionCode;
            // this.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getCurrentVersion_name(Context context) {
        String versionName = "";
        try {
            // 获取应用包信息
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
