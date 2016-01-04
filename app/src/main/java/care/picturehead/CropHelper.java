package care.picturehead;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

import care.application.XcmApplication;

public class CropHelper {

	public static final String TAG = "CropHelper";

    /**
     * request code of Activities or Fragments
     * You will have to change the values of the request codes below if they conflict with your own.
     */
    public static final int REQUEST_CROP = 127;
    public static final int REQUEST_CAMERA = 128;
    public static final int SELECET_A_PICTURE_AFTER_KIKAT = 129;
    
    public static String CROP_CACHE_FILE_NAME = "crop_cache_file.jpg";
    
    public static Uri buildUri(String fileName) {
    	CROP_CACHE_FILE_NAME = fileName;
    	File file = new File(XcmApplication.SDCARD+"/head_tmp");
    	if(!file.exists()){  //文件不存在创建
    		file.mkdirs();
    	}
        return Uri
                .fromFile(file)
                .buildUpon()
                .appendPath(CROP_CACHE_FILE_NAME)
                .build();
    }

	public static void handleResult(CropHandler handler, int requestCode,
			int resultCode, Intent data) {
		if (handler == null)
			return;

		if (resultCode == Activity.RESULT_CANCELED) {
			handler.onCropCancel(); // 撤销拍照
		} else if (resultCode == Activity.RESULT_OK) {
			Intent intent = null;
			CropParams cropParams = handler.getCropParams();
			Activity context = handler.getContext();

			if (cropParams == null) {
				handler.onCropFailed("CropHandler's params MUST NOT be null!");
				return;
			}
			switch (requestCode) {
			case REQUEST_CROP:
				Log.d(TAG, "Photo cropped!");
				// if(data.getExtras().getString("data") == null){
				// String filePath = data.getExtras().getString("filePath");
				// if (!TextUtils.isEmpty(filePath))
				// mPhoto = ImageUtils.decodeSampledBitmapFromFile(filePath,
				// 400, 600);
				// }
				 handler.onPhotoCropped(handler.getCropParams().uri);
				break;
			case REQUEST_CAMERA:
				intent = buildCropFromUriIntent(handler.getCropParams());
				if (context != null) {
					context.startActivityForResult(intent, REQUEST_CROP);
				} else {
					handler.onCropFailed("CropHandler's context MUST NOT be null!");
				}
				break;
			case SELECET_A_PICTURE_AFTER_KIKAT:
				if(null != data){
					String mAlbumPicturePath = getPath(context.getApplicationContext(),
							data.getData());
					Uri uri = Uri.fromFile(new File(mAlbumPicturePath));
					handler.getCropParams().setCropUri(uri);

					intent = buildCropFromUriIntent(handler.getCropParams());
					if(context != null){
						context.startActivityForResult(intent, REQUEST_CROP);
					}
				}
			break;
			}
		}
	}

	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			}else if(isDownloadsDocument(uri)){
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}else if (isMediaDocument(uri)){
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}else if ("content".equalsIgnoreCase(uri.getScheme())) {
				if (isGooglePhotosUri(uri))
					return uri.getLastPathSegment();

				return getDataColumn(context, uri, null, null);
			}else if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

    public static boolean clearCachedCropFile(Uri uri) {
        if (uri == null) return false;

        File file = new File(uri.getPath());
        if (file.exists()) {
            boolean result = file.delete();
            if (result)
                Log.i(TAG, "Cached crop file cleared.");
            else
                Log.e(TAG, "Failed to clear cached crop file.");
            return result;
        } else {
            Log.w(TAG, "Trying to clear cached crop file but it does not exist.");
        }
        return false;
    }

    public static Intent buildCropFromUriIntent(CropParams params) {
    	Intent dataIntent = new Intent();
    	dataIntent.setAction("com.android.camera.action.CROP");
        return buildCropIntent(dataIntent, params);
    }

    public static Intent buildCropFromGalleryIntent(CropParams params) {
    	Intent dataIntent = new Intent();
    	boolean flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    	if(flag){  //4.4版本或者以上
    		dataIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
    		dataIntent.addCategory(Intent.CATEGORY_OPENABLE);
    		dataIntent.setType("image/*");
    		return dataIntent;
    	}else{
    		dataIntent.setAction(Intent.ACTION_GET_CONTENT);
    		return buildCropIntent(dataIntent,params);
    	}	
    }

    public static Intent buildCaptureIntent(Uri uri) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        .putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }

	public static Intent buildCropIntent(Intent actionIntent, CropParams params) {
		actionIntent.setDataAndType(params.uri, params.type)
				// .setType(params.type)
				.putExtra("crop", params.crop)
				// 发送裁剪信号
				.putExtra("scale", params.scale)
				// 是否保留比例
				.putExtra("aspectX", params.aspectX)
				// X方向上的比例
				.putExtra("aspectY", params.aspectY)
				// Y方向上的比例
				.putExtra("outputX", params.outputX)
				// 裁剪区的宽
				.putExtra("outputY", params.outputY)
				// 裁剪区的高
				.putExtra("return-data", params.returnData)
				// 是否返回数据,魅族返回不了data
				.putExtra("outputFormat", params.outputFormat)
				.putExtra("noFaceDetection", params.noFaceDetection) // 关闭人脸检测
				.putExtra("scaleUpIfNeeded", params.scaleUpIfNeeded);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			params.setCropUri(Uri.fromFile(new File(XcmApplication.SDCARD + "/head",
					CROP_CACHE_FILE_NAME)));
		}

		return actionIntent.putExtra(MediaStore.EXTRA_OUTPUT, params.uri);
	}

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
