package ru.goodibunakov.itravel;

import android.app.Service;
import android.content.Intent;
import android.database.SQLException;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by GooDi on 27.07.2017.
 */

public class MyDestinationService extends Service {

    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate Service");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand Service");
        toDestinationDB();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy Service");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void toDestinationDB() {
        new Thread(new Runnable() {
            public void run() {
                DataBaseDestinationHelper myDbHelper = new DataBaseDestinationHelper(MyDestinationService.this);

                try {
                    myDbHelper.createDataBase();
                } catch (IOException ioe) {
                    throw new Error("Unable to create database");
                }

                try {
                    myDbHelper.openDataBase();
                } catch (SQLException sqle) {
                    throw sqle;
                }
                myDbHelper.close();
                stopSelf();
            }
        }).start();
    }
}