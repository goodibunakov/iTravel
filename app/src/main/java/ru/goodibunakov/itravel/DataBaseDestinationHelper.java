package ru.goodibunakov.itravel;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by GooDi on 27.07.2017.
 */

public class DataBaseDestinationHelper extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    private static String DB_PATH = "/data/data/ru.goodibunakov.itravel/databases/";
    private static String DB_NAME = "destination";
    private static final int DB_VERSION = 1; // версия базы данных
    private SQLiteDatabase destinationDatabase;
    private final Context context;

    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     *
     * @param context
     */
    public DataBaseDestinationHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //ничего не делать - база уже есть
        } else {
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     *
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase() {
        //       SQLiteDatabase checkDB = null;

        File dbFile = context.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     */
    private void copyDataBase() throws IOException {
        //Открываем локальную БД как входящий поток
        InputStream myInput = context.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        destinationDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (destinationDatabase != null)
            destinationDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return destinationDatabase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view


    public ArrayList<String> getAllCountries() {
        ArrayList<String> allCountries = new ArrayList<>();

        String countries = "SELECT DISTINCT country FROM destination";
        Cursor cursorCountry = getReadableDatabase().rawQuery(countries, null);

        cursorCountry.moveToFirst();
        while (!cursorCountry.isAfterLast()) {
            allCountries.add(cursorCountry.getString(cursorCountry.getColumnIndex("country")));
            cursorCountry.moveToNext();
        }
        cursorCountry.close();
        return allCountries;
    }

    public ArrayList<String> getNeededCities(String selectedCountry) {
        ArrayList<String> neededCities = new ArrayList<>();

        String cities = "SELECT city FROM destination WHERE country IN (\"" + selectedCountry + "\")";
        Cursor cursorCity = getReadableDatabase().rawQuery(cities, null);

        cursorCity.moveToFirst();
        while (!cursorCity.isAfterLast()) {
            neededCities.add(cursorCity.getString(cursorCity.getColumnIndex("city")));
            cursorCity.moveToNext();
        }
        cursorCity.close();
        return neededCities;
    }
}