package ru.goodibunakov.itravel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import safety.com.br.progressimageview.ProgressImageView;

public class CreateNewTripActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    ElegantNumberButton numberButton;
    private DatePickerDialog chooseDate;
    AlertDialog.Builder ad;
    private EditText dateFrom, dateTo;
    DataBaseGoodsHelper dataBaseGoodsHelper;
    AutoCompleteTextView autoCompleteTextViewCountry;
    AutoCompleteTextView autoCompleteTextViewCity;
    String chosenCountry;
    List<HashMap<String, String>> persons;
    RecyclerView personList;
    SharedPreferences sharedPreferences;
    ProgressImageView progressImageView;
    Button createTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_trip);

        //поддержка векторной графики
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //создание хранилища для горизонтального списка персон
        sharedPreferences = getSharedPreferences("preferencePersons", Context.MODE_PRIVATE);

        //лист для горизонтального списка персон
        persons = new ArrayList<>();

        //горизонтальный список персон
        personList = (RecyclerView) findViewById(R.id.person_list);
        personList.setLayoutManager(new LinearLayoutManager(CreateNewTripActivity.this, LinearLayoutManager.HORIZONTAL, false));

        //кнопка добавления участника
        numberButton = (ElegantNumberButton) findViewById(R.id.elegant_number_button);

        //кнопка "Создать поездку"
        createTrip = (Button) findViewById(R.id.btn_create_trip);

        //ключ для гугл мест  AIzaSyCofVMoBolQ2zFk-weJio8DCqJ8Vr2BkBc
        final PlaceAutocompleteFragment places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        places.setFilter(typeFilter);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng coordinates = place.getLatLng(); // Получаем координаты из объекта place
                Geocoder geocoder = new Geocoder(CreateNewTripActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(
                            coordinates.latitude,
                            coordinates.longitude,
                            1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    Log.i("code", address.getCountryCode());
                }

                Toast.makeText(getApplicationContext(), place.getAddress(), Toast.LENGTH_SHORT).show();
                Toast.makeText(CreateNewTripActivity.this, place.getLatLng().latitude + " " + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();
            }
        });


        dateFrom = (EditText) findViewById(R.id.date_from);
        dateFrom.setOnFocusChangeListener(this);
        dateTo = (EditText) findViewById(R.id.date_to);
        dateTo.setOnFocusChangeListener(this);

        dataBaseGoodsHelper = new DataBaseGoodsHelper(this);

        final RadioRealButton btnBus = (RadioRealButton) findViewById(R.id.transport_bus);
        final RadioRealButton btnCar = (RadioRealButton) findViewById(R.id.transport_car);
        final RadioRealButton btnPlain = (RadioRealButton) findViewById(R.id.transport_plain);
        final RadioRealButton btnShip = (RadioRealButton) findViewById(R.id.transport_ship);
        final RadioRealButton btnTrain = (RadioRealButton) findViewById(R.id.transport_train);
        final RadioRealButton btnNogami = (RadioRealButton) findViewById(R.id.transport_nogami);
        RadioRealButtonGroup group = (RadioRealButtonGroup) findViewById(R.id.transport_group);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                Toast.makeText(CreateNewTripActivity.this, "Clicked! Position: " + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        //обработка кнопки "Создать поездку"
        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetGoodsAsyncTask().execute();
            }
        });

        numberButton.animate();
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                // Добавляем нового участника
                if (oldValue < newValue) {
                    Intent newPerson = new Intent(CreateNewTripActivity.this, PersonEditActivity.class);
                    startActivityForResult(newPerson, 0);
                } else {
                    view.setNumber(String.valueOf(oldValue));
                    //Удаляем
//                    ad = new AlertDialog.Builder(CreateNewTripActivity.this);
//                    ad.setTitle(getResources().getString(R.string.title_delete_person));  // заголовок
//                    ad.setMessage(getResources().getString(R.string.dialog_aushure)); // сообщение
//                    ad.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int arg1) {
//                            Toast.makeText(CreateNewTripActivity.this, getResources().getString(R.string.person_delete),
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    ad.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int arg1) {
//                            Toast.makeText(CreateNewTripActivity.this, getResources().getString(R.string.person_ok), Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                    ad.setCancelable(false);
//                    ad.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_new_trip_activity, menu);
        return true;
    }

    void setCountryFlag(String countryCode) {
        switch (countryCode) {
            case "RU":
                //int flag = getResources().getIdentifier("flag_ru", "drawable", YourActivity.this.getPackageName());
                break;
            case "UA":
                //int flag = getResources().getIdentifier("R.id.flag_ru");
                break;
            default: //int flag = getResources().getIdentifier("R.id.flag_empty");
        }
    }

    //инициализация диалога выбора даты
    private void initDateDialog(final View view) throws ParseException {
        String dateFromString;
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        chooseDate = new DatePickerDialog(CreateNewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.set(year, monthOfYear, dayOfMonth);
                switch (view.getId()) {
                    case R.id.date_from:
                        dateFrom.setText(dateFormat.format(newCalendar.getTime()));
                        break;
                    case R.id.date_to:
                        dateTo.setText(dateFormat.format(newCalendar.getTime()));
                        break;
                }

            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        if (view.getId() == R.id.date_to) {
            if (!dateFrom.getText().toString().isEmpty()) {
                dateFromString = dateFrom.getText().toString();
                SimpleDateFormat dateFormatTemp = new SimpleDateFormat("dd.MM.yyyy");
                Date convertedDate = new Date();
                try {
                    convertedDate = dateFormatTemp.parse(dateFromString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //ограничение. нельзя выбрать дату отъзда ранее даты приезда
                chooseDate.getDatePicker().setMinDate(convertedDate.getTime());
            }
        }
        if (view.getId() == R.id.date_from) {
            //ограничение. нельзя выбрать дату приезда ранее сегодняшней даты
            chooseDate.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }
    }


    //вызывается когда сменился фокус на EditText
    //Потом проверка, если в фокусе, то инициализировать диалог выбора даты и показать
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.date_from:
            case R.id.date_to:
                try {
                    initDateDialog(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    switch (view.getId()) {
                        case R.id.date_from:
                            chooseDate.show();
                            break;
                        case R.id.date_to:
                            if (dateFrom.getText().toString().isEmpty()) {
                                Toast.makeText(CreateNewTripActivity.this, getResources().getString(R.string.warning_enter_date_from), Toast.LENGTH_SHORT).show();
                            } else chooseDate.show();
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBaseGoodsHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String personsString = "";

        if (data == null) {
            return;
        }

        //получаем данные участника из интента (из активити создания участника)
        String name = data.getStringExtra("name");
        String age = data.getStringExtra("age");
        String sex = data.getStringExtra("sex");
        int ava = data.getIntExtra("ava", 0);

        //очищаем SharedPreferences перед внесением второго и более участника
        //проверка, были ли уже сохранены участники
        Boolean isFirstPerson = sharedPreferences.contains("isFirstPerson");

        if (!isFirstPerson) {
            Log.e("asasas", "isFirstPerson отсутствует");
            getSharedPreferences("preferencePersons", 0).edit().clear().apply();
            personsString = "{\"peoples\":[{\"name\":\"" + name + "\",\"age\":\"" + age + "\",\"sex\":\"" + sex + "\",\"ava\":" + ava + "}]}";
            //сохраняем участника в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("personsStringShared", personsString);
            editor.putBoolean("isFirstPerson", true);
            editor.commit();

            parseJSON(personsString);
        } else {
            Log.e("asasas", "isFirstPerson существует");
            String personsStringForAddingPerson = sharedPreferences.getString("personsStringShared", "null");
            addToJSON(personsStringForAddingPerson, name, age, sex, ava);

        }

        PersonsListAdapter personsListAdapter = new PersonsListAdapter(persons);
        personsListAdapter.notifyDataSetChanged();
        personList.setAdapter(personsListAdapter);

        Log.e("personsString", personsString);
        Log.e("persons", persons.toString());
        Log.e("Круть", persons.size() + " name " + name + "   age " + age + "   sex = " + sex + "  ava = " + ava + "   " + getResources().getIdentifier("avatars_02", "drawable", "ru.goodibunakov.itravel"));

        // У меня есть id ресурса-строка из файла R. например: 21346466556. Это mp3 файл в папке raw. Как мне его преобразовать в File?
        // mContext.getResources().getResourceEntryName(Integer.parseInt("ResourceId")));
    }


    private List<HashMap<String, String>> addToJSON(String personsStringForAddingPerson, String name, String age, String sex, int ava) {
        JSONArray array;

        try {
            JSONObject object = new JSONObject(personsStringForAddingPerson);
            array = object.getJSONArray("peoples");
            JSONObject onePeople = new JSONObject();
            onePeople.put("name", name);
            onePeople.put("age", age);
            onePeople.put("sex", sex);
            onePeople.put("ava", ava);
            //добавил нового участника в json
            array.put(onePeople);
            persons.clear();

            //добавляем полный список участников с добавленным участником в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("personsStringShared", object.toString());
            editor.commit();

            //парсим всех участников из json в List
            for (int i = 0; i < array.length(); i++) {
                JSONObject json_data = array.getJSONObject(i);
                HashMap<String, String> person = new HashMap<>();
                Log.e("json_data addToJSON", json_data.toString());
                person.put("name", json_data.getString("name"));
                person.put("age", json_data.getString("age"));
                person.put("sex", json_data.getString("sex"));
                person.put("ava", json_data.getString("ava"));
                persons.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return persons;
    }

    //метод который из строки personsList возвращает List персон для адаптера горизонтального списка участников
    private List<HashMap<String, String>> parseJSON(String personsString) {
        try {
            JSONObject object = new JSONObject(personsString);
            JSONArray array = object.getJSONArray("peoples");
            for (int i = 0; i < array.length(); i++) {
                JSONObject json_data = array.getJSONObject(i);
                HashMap<String, String> person = new HashMap<>();
                Log.e("json_data parseJSON", json_data.toString());
                person.put("name", json_data.getString("name"));
                person.put("age", json_data.getString("age"));
                person.put("sex", json_data.getString("sex"));
                person.put("ava", json_data.getString("ava"));
                persons.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return persons;
    }

    @Override
    public void onBackPressed() {
    }

    class GetGoodsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createTrip.setEnabled(false);
            progressImageView = findViewById(R.id.progress_image_view);
            progressImageView.showLoading().withAutoHide(false).withBorderColor(getResources().getColor(R.color.colorWhite)).withOffset(10);
            progressImageView.setVisibility(View.VISIBLE);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(CreateNewTripActivity.this, R.anim.fade_in);
            progressImageView.startAnimation(fadeInAnimation);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressImageView.hideLoading();
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(CreateNewTripActivity.this, R.anim.fade_out);
            progressImageView.startAnimation(fadeOutAnimation);
            progressImageView.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(CreateNewTripActivity.this, TravelActivity.class);
            startActivity(intent);
        }
    }
}