
package com.mtk.bluetoothle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.mtk.btnotification.R;

public class FitnessHelper {

    private static final String TAG = "[wearable][Fit]FitnessHelper";

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    public static final int REQUEST_OAUTH = 1;
    public static final String AUTH_PENDING = "auth_state_pending";
    public boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private Activity mActivity;
    private static final String PRE_DATA = "PRE_TIME";
    private static final String PRE_UPLOAD_STEP = "PRE_STEP_TIME";
    private long mPreUploadStep = 0;
    private int mPreStepCount = 0;
    private int mPreCalories = 0;
    private int mPreDistance = 0;

    DataSource mStepDataSource;
    DataSource mCalDataSource;
    DataSource mDistanceDataSource;
    DataSource mSleepDataSource;
    DataSource mHRDataSource;

    private static FitnessHelper sInstance = null;

    public static FitnessHelper getInstance() {
        if (sInstance == null) {
            sInstance = new FitnessHelper();
        }
        return sInstance;
    }

    private FitnessHelper() {
    }

    public void initFitnessConnection(final Activity activity) {
        Log.d(TAG, "[initFitnessConnection] activity=" + activity);
        mActivity = activity;
        if (LeProfileUtils.isFitnessAvailable()) {
            Log.d(TAG, "[initFitnessConnection] create mClient " + mClient);
            if (mClient == null) {
                mClient = new GoogleApiClient.Builder(activity)
                        .addApi(Fitness.API)
                        .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                        .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                        .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        .useDefaultAccount()
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.d(TAG, "GoogleApiClient connect successfully");

                                mStepDataSource = new DataSource.Builder()
                                        .setAppPackageName(mActivity)
                                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                        .setName("MTK FIT - STEP COUNT")
                                        .setType(DataSource.TYPE_RAW).build();

                                mCalDataSource = new DataSource.Builder()
                                        .setAppPackageName(mActivity)
                                        .setDataType(DataType.TYPE_CALORIES_CONSUMED)
                                        .setName("MTK FIT - CALORIES").setType(DataSource.TYPE_RAW)
                                        .build();

                                mDistanceDataSource = new DataSource.Builder()
                                        .setAppPackageName(mActivity)
                                        .setDataType(DataType.TYPE_DISTANCE_DELTA)
                                        .setName("MTK FIT - DISTANCE").setType(DataSource.TYPE_RAW)
                                        .build();

                                mSleepDataSource = new DataSource.Builder()
                                        .setAppPackageName(mActivity)
                                        .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                                        .setName("MTK FIT - SLEEP").setType(DataSource.TYPE_RAW)
                                        .build();

                                mHRDataSource = new DataSource.Builder()
                                        .setAppPackageName(mActivity)
                                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                                        .setName("MTK FIT - HEART RATE")
                                        .setType(DataSource.TYPE_RAW).build();

                                readPreUploadTime();
                                readDataFromFitness();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and
                                // react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.d(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.d(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        })
                        .addOnConnectionFailedListener(
                                new GoogleApiClient.OnConnectionFailedListener() {
                                    // Called whenever the API client fails to connect.
                                    @Override
                                    public void onConnectionFailed(ConnectionResult result) {
                                        Log.d(TAG, "Connection failed. Cause: " + result.toString()
                                                + " ErrorCode=" + result.getErrorCode());
                                        if (!result.hasResolution()) {
                                            // Show the localized error dialog
                                            try {
                                                GooglePlayServicesUtil.getErrorDialog(
                                                        result.getErrorCode(), activity, 0).show();
                                            } catch (Exception e) {
                                                Log.e(TAG, "onConnectionFailed getErrorDialog Exception: " + e);
                                            }
                                            return;
                                        }
                                        // The failure has a resolution. Resolve it.
                                        // Called typically when the app is not yet authorized, and an authorization
                                        // dialog is displayed to the user.
                                        if (!authInProgress) {
                                            try {
                                                Log.d(TAG,
                                                        "Attempting to resolve failed connection");
                                                authInProgress = true;
                                                result.startResolutionForResult(activity,
                                                        REQUEST_OAUTH);

                                                if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED) {
                                                    Toast.makeText(mActivity,
                                                            R.string.sign_in_google,
                                                            Toast.LENGTH_LONG).show();
                                                } else if (result.getErrorCode() == 5000) {
                                                    Toast.makeText(mActivity, R.string.accept_fit,
                                                            Toast.LENGTH_LONG).show();
                                                }

                                            } catch (IntentSender.SendIntentException e) {
                                                Log.e(TAG, "Exception while starting resolution activity", e);
                                            } catch (Exception e) {
                                                Log.e(TAG, "startResolutionForResult Exception: " + e);
                                            }
                                        }
                                    }
                                }).build();
            }
        }
    }

    public void connect() {
        Log.d(TAG, "connect start");
        if (LeProfileUtils.isFitnessAvailable()) {
            if (mClient == null && mActivity != null) {
                initFitnessConnection(mActivity);
            }
            if (mClient != null) {
                Log.d(TAG, "[connect] connecting=" + mClient.isConnecting()
                        + " isConnected=" + mClient.isConnected());
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    Log.d(TAG, "mClient.connect start");
                    mClient.connect();
                }
            }
        }
    }

    public void disconnect() {
        Log.d(TAG, "disconnect start");
        if (LeProfileUtils.isFitnessAvailable()) {
            if (mClient != null && mClient.isConnected()) {
                Log.d(TAG, "mClient.disconnect start");
                mClient.disconnect();
            }
        }
    }

    public void uploadStepData(int stepCount, int calories, int distance) {
        Log.d(TAG, "uploadStepData start");
        if (LeProfileUtils.isFitnessAvailable()) {
            if (mClient != null && mClient.isConnected()) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();

                Log.d(TAG, "uploadStepData start mPreUploadStep=" + mPreUploadStep
                        + " endTime=" + endTime
                        + " mPreStepCount=" + mPreStepCount+ " stepCount=" + stepCount
                        + " mPreCalories=" + mPreCalories+ " calories=" + calories
                        + " mPreDistance=" + mPreDistance+ " distance=" + distance);

                insertStepCount(endTime, stepCount - mPreStepCount);
                insertCal(endTime, calories - mPreCalories);
                insertDistance(endTime, distance - mPreDistance);
                updatePreUploadTime(endTime);
                updatePreStepData(stepCount, calories, distance);
            }
        }
    }

    public void uploadSleepData(long startTime, long endTime, int sleepMode) {
        Log.d(TAG, "uploadSleepData start");
        if (LeProfileUtils.isFitnessAvailable()) {
            if (mClient != null && mClient.isConnected()) {
                insertSleep(startTime, endTime, sleepMode);
            }
        }
    }

    public void uploadHRData(int bmp) {
        Log.d(TAG, "uploadHR start");
        if (LeProfileUtils.isFitnessAvailable()) {
            if (mClient != null && mClient.isConnected()) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long time = cal.getTimeInMillis();
                insertHeartRate(time, bmp);
            }
        }
    }

    private void insertStepCount(long endTime, int addStepCount) {
        Log.d(TAG, "insertStepCount begin endTime=" + endTime + " step=" + addStepCount);
        if (addStepCount <= 0) {
            return;
        }
        DataSet dataSet = DataSet.create(mStepDataSource);
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(mPreUploadStep, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(addStepCount);
        dataSet.add(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status != null && status.isSuccess()) {
                            Log.d(TAG, "insertStepCount done");
                        } else {
                            Log.d(TAG, "insertStepCount fail");
                        }
                    }
                });
    }

    private void insertCal(long endTime, int addCalories) {
        Log.d(TAG, "insertCal begin endTime=" + endTime + " calories=" + addCalories);
        if (addCalories <= 0) {
            return;
        }
        DataSet dataSet = DataSet.create(mCalDataSource);
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(mPreUploadStep, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_CALORIES).setFloat(((float)addCalories)/1000);
        dataSet.add(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status != null && status.isSuccess()) {
                            Log.d(TAG, "insertCal done");
                        } else {
                            Log.d(TAG, "insertCal fail");
                        }
                    }
                });

    }

    private void insertDistance(long endTime, int addDistance) {
        Log.d(TAG, "insertDistance begin endTime=" + endTime + " distance=" + addDistance);
        if (addDistance <= 0) {
            return;
        }
        DataSet dataSet = DataSet.create(mDistanceDataSource);
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(mPreUploadStep, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_DISTANCE).setFloat(addDistance);
        dataSet.add(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status != null && status.isSuccess()) {
                            Log.d(TAG, "insertDistance done");
                        } else {
                            Log.d(TAG, "insertDistance fail");
                        }
                    }
                });
    }

    private void insertSleep(long startTime, long endTime, int sleepMode) {
        Log.d(TAG, "insertSleep begin startTime=" + startTime + " endTime" + endTime + " sleepMode=" + sleepMode);
        if (startTime >= endTime || sleepMode < 1 || sleepMode > 2) {
            return;
        }
        DataSet dataSet = DataSet.create(mSleepDataSource);
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.SLEEP);
        dataSet.add(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status != null && status.isSuccess()) {
                            Log.d(TAG, "insertSleep done");
                        } else {
                            Log.d(TAG, "insertSleep fail");
                        }
                    }
                });
    }

    private void insertHeartRate(long time, int bpm) {
        Log.d(TAG, "insertHeartRate begin time=" + time + " bpm=" + bpm);
        if (bpm <= 0) {
            return;
        }
        DataSet dataSet = DataSet.create(mHRDataSource);
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(0, time, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_BPM).setFloat(bpm);
        dataSet.add(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status != null && status.isSuccess()) {
                            Log.d(TAG, "insertHeartRate done");
                        } else {
                            Log.d(TAG, "insertHeartRate fail");
                        }
                    }
                });
    }

    /// M: Manage last insert time.@{
    private void updatePreUploadTime(long time) {
        mPreUploadStep = time;
        SharedPreferences prefs = mActivity.getSharedPreferences(PRE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PRE_UPLOAD_STEP, time);
        editor.commit();
    }

    private void readPreUploadTime() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        SharedPreferences prefs = mActivity.getSharedPreferences(PRE_DATA, Context.MODE_PRIVATE);
        mPreUploadStep = prefs.getLong(PRE_UPLOAD_STEP, endTime);
    }
    /// @}

    /// M: Manage last received data. @{
    private void updatePreStepData(int preStepCount, int preCalories, int preDistance) {
        mPreStepCount = preStepCount;
        mPreCalories = preCalories;
        mPreDistance = preDistance;
    }

    private void readDataFromFitness() {
        mPreStepCount = 0;
        mPreCalories = 0;
        mPreDistance = 0;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Log.d(TAG, "[readDataFromFitness] Range Start: " + dateFormat.format(startTime));
        Log.d(TAG, "[readDataFromFitness] Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).read(mStepDataSource)
                .enableServerQueries()
                .build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[readDataFromFitness] stepCount getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                Log.d(TAG, "[readDataFromFitness] stepCount dataSet.getDataPoints: "
                                        + dataSet.getDataPoints().size());
                                Log.d(TAG, "[readDataFromFitness] stepCount DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        Log.d(TAG, "[readDataFromFitness] Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                                        + " End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + dp.getValue(field).asInt());
                                        mPreStepCount += dp.getValue(field).asInt();
                                    }
                                }
                            }
                            Log.d(TAG, "[readDataFromFitness] mPreStepCount=" + mPreStepCount);
                        }
                    }
                });

        readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).read(mCalDataSource)
                .enableServerQueries()
                .build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[readDataFromFitness] Calories getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                Log.d(TAG, "[readDataFromFitness] Calories dataSet.getDataPoints: "
                                        + dataSet.getDataPoints().size());
                                Log.d(TAG, "[readDataFromFitness] Calories DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        Log.d(TAG, "[readDataFromFitness] kcal=" + dp.getValue(field).asFloat());
                                        mPreCalories += (dp.getValue(field).asFloat() * 1000);
                                    }
                                }
                            }
                            Log.d(TAG, "[readDataFromFitness] mPreCalories=" + mPreCalories);
                        }
                    }
                });

        readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).read(mDistanceDataSource)
                .enableServerQueries()
                .build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[readDataFromFitness] Distance getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                Log.d(TAG, "[readDataFromFitness] Distance dataSet.getDataPoints: "
                                        + dataSet.getDataPoints().size());
                                Log.d(TAG, "[readDataFromFitness] Distance DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        Log.d(TAG, "[readDataFromFitness] distance=" + dp.getValue(field).asFloat());
                                        mPreDistance += dp.getValue(field).asFloat();
                                    }
                                }
                            }
                            Log.d(TAG, "[readDataFromFitness] mPreDistance=" + mPreDistance);
                        }
                    }
                });

        readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).read(mSleepDataSource)
                .enableServerQueries()
                .build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[readDataFromFitness] Distance getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                Log.d(TAG, "[readDataFromFitness] Distance dataSet.getDataPoints: "
                                        + dataSet.getDataPoints().size());
                                Log.d(TAG, "[readDataFromFitness] Distance DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        Log.d(TAG, "[readDataFromFitness] Start: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                        Log.d(TAG, "[readDataFromFitness] End: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                        Log.d(TAG, "[readDataFromFitness] activity = " + dp.getValue(field).asActivity());
                                    }
                                }
                            }

                        }
                    }
                });

        readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .read(mHRDataSource).bucketByTime(1, TimeUnit.MINUTES).build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[readDataFromFitness] HEART_RATE getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                float num = dataSet.getDataPoints().size();
                                float min = 65536;
                                float max = 0;
                                long total = 0;
                                Log.d(TAG, "[readDataFromFitness] HEART_RATE dataSet.getDataPoints: " + num);
                                Log.d(TAG, "[readDataFromFitness] HEART_RATE DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        float bpm = dp.getValue(field).asFloat();
                                        Log.d(TAG, "[readDataFromFitness] time: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " --- " + bpm);
                                        total += bpm;
                                        if (bpm > max) {
                                            max = bpm;
                                        }
                                        if (bpm < min) {
                                            min = bpm;
                                        }
                                    }
                                }
                                Log.d(TAG, "[readDataFromFitness] HEART_RATE min:" + min + " max:" + max);
                                if (total > 0 && num > 0) {
                                    Log.d(TAG, "[readDataFromFitness] average: " +  total/num);
                                }
                            }

                        }
                    }
                });
    }
    /// @}

    /// write stepCount data
    public void writeStepCount(final FileOutputStream outputStream) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Log.d(TAG, "[writeStepCount] Range Start: " + dateFormat.format(startTime));
        Log.d(TAG, "[writeStepCount] Range End: " + dateFormat.format(endTime));

        String stepCountHeader = "StepCount: from " + dateFormat.format(startTime) + " to " + dateFormat.format(endTime);
        try {
            outputStream.write(stepCountHeader.getBytes());
        } catch (IOException e) {
            Log.d(TAG, "[writeStepCount] IOException");
        }

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).read(mStepDataSource)
                .enableServerQueries()
                .build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[writeStepCount] stepCount getDataSets: "
                                + reuslt.getDataSets().size());
                        int total = 0;
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                Log.d(TAG, "[writeStepCount] stepCount dataSet.getDataPoints: "
                                        + dataSet.getDataPoints().size());
                                Log.d(TAG, "[writeStepCount] stepCount DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        String str = "\n" + "     TimeStamp: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                                                + " -> " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                                                + "\n" + "     StepCount: " + dp.getValue(field).asInt();
                                        total += dp.getValue(field).asInt();
                                        try {
                                            outputStream.write(str.getBytes());
                                        } catch (IOException e) {
                                            Log.d(TAG, "[writeStepCount] IOException");
                                        }
                                    }
                                }
                            }
                            try {
                                String totalString = "\n" + "StepCount: " + String.valueOf(total) + "\n\n\n\n\n";
                                outputStream.write(totalString.getBytes());
                            } catch (IOException e) {
                                Log.d(TAG, "[writeStepCount] IOException");
                            }
                        }
                    }
                });
    }
    
    /// write HeartRate data
    public void writeHeartRate(final FileOutputStream outputStream) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Log.d(TAG, "[writeHeartRate] Range Start: " + dateFormat.format(startTime));
        Log.d(TAG, "[writeHeartRate] Range End: " + dateFormat.format(endTime));

        String header = "\n" + "HeartRate: from " + dateFormat.format(startTime) + " to " + dateFormat.format(endTime);
        try {
            outputStream.write(header.getBytes());
        } catch (IOException e) {
            Log.d(TAG, "[writeHeartRate] IOException");
        }

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).enableServerQueries()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .read(mHRDataSource).bucketByTime(1, TimeUnit.MINUTES).build();
        Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult reuslt) {
                        Log.d(TAG, "[writeHeartRate] HEART_RATE getDataSets: "
                                + reuslt.getDataSets().size());
                        if (reuslt.getDataSets().size() > 0) {
                            for (DataSet dataSet : reuslt.getDataSets()) {
                                float num = dataSet.getDataPoints().size();
                                Log.d(TAG, "[writeHeartRate] HEART_RATE dataSet.getDataPoints: " + num);
                                Log.d(TAG, "[writeHeartRate] HEART_RATE DataType: "
                                        + dataSet.getDataType().getName());
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        float bpm = dp.getValue(field).asFloat();
                                        String str = "\n" + "     TimeStamp: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                                                + "\n" + "     HeartRate: " + bpm;
                                        try {
                                            outputStream.write(str.getBytes());
                                        } catch (IOException e) {
                                            Log.d(TAG, "[writeHeartRate] IOException");
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
