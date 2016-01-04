package com.gomtel.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


import java.lang.reflect.Field;

import com.mtk.btnotification.R;

public class DialogHelper
{
  public static void closeDialogByClickButton(DialogInterface paramDialogInterface, boolean paramBoolean)
  {
    try
    {
      Field localField = paramDialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
      localField.setAccessible(true);
      localField.set(paramDialogInterface, Boolean.valueOf(paramBoolean));
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void dismissDialog(Dialog paramDialog)
  {
    if (paramDialog == null)
      return;
    paramDialog.dismiss();
  }

  public static void hideDialog(Dialog paramDialog)
  {
    if (paramDialog == null)
      return;
    paramDialog.hide();
  }

  public static void showAlertDialog(Context paramContext, String paramString1, String paramString2, OnClickListener paramOnClickListener)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    localBuilder.setPositiveButton(R.string.ok, paramOnClickListener);
    localBuilder.show();
  }

  public static void showAlertDialog_2(Context paramContext, String paramString1, String paramString2, OnClickListener paramOnClickListener1, OnClickListener paramOnClickListener2)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    localBuilder.setNegativeButton(R.string.cancel, paramOnClickListener2);
    localBuilder.setPositiveButton(R.string.commit, paramOnClickListener1);
    localBuilder.show();
  }

  public static ProgressDialog showProgressDialog(Context paramContext, String paramString)
  {
    ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setProgressStyle(0);
    localProgressDialog.setMessage(paramString);
    return localProgressDialog;
  }

  public static ProgressDialog showSelectDialog(Context paramContext, String paramString)
  {
    ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setProgressStyle(0);
    localProgressDialog.setMessage(paramString);
    return localProgressDialog;
  }
}

/* Location:           D:\DownloadSoftware\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.gzgamut.max.helper.DialogHelper
 * JD-Core Version:    0.5.4
 */