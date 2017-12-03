package ru.goodibunakov.itravel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;


public class PersonEditActivity extends AppCompatActivity implements View.OnClickListener {

    Boolean avaChosenByUser = false; //выбрана ава юзером?
    int chosenId; //id выбранной авы
    ImageView ava; // собственно ава
    Button btnOkPerson; // кнопка ОК, создать/сохранить персону
    EditText editTextName, editTextAge;
    RadioRealButtonGroup groupSex;
    int[] check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_person_edit));
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);

        check = new int[66]; //массив для все галочки аватаров выкл

        editTextAge = (EditText) findViewById(R.id.edittext_age);
        //ограничение ввода возраста - от 0 до 90
        editTextAge.setFilters(new InputFilter[]{new InputFilterMinMax(0, 90)});

        ava = (ImageView) findViewById(R.id.imageview_ava);
        ava.setImageResource(R.drawable.avatars_man);
        ava.setOnClickListener(this);

        btnOkPerson = (Button) findViewById(R.id.btn_ok_person);
        btnOkPerson.setOnClickListener(this);

        chosenId = getResources().getIdentifier("avatars_man", "drawable", "ru.goodibunakov.itravel");


        //группа выбора пола участника. по умолчанию принимаю пол мужской
        groupSex = (RadioRealButtonGroup) findViewById(R.id.radiogroup_sex);
        groupSex.setPosition(0, true);

        // onClickButton listener detects any click performed on buttons by touch
        groupSex.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                Toast.makeText(PersonEditActivity.this, "Clicked! Position: " + (position + 1), Toast.LENGTH_SHORT).show();
                //если юзер сам аватарку не выбрал то ставим аву исходя из пола участника
                if (!avaChosenByUser) {
                    switch (position) {
                        case 0:
                            ava.setImageResource(R.drawable.avatars_man);
                            chosenId = getResources().getIdentifier("avatars_man", "drawable", "ru.goodibunakov.itravel");
                            break;
                        case 1:
                            ava.setImageResource(R.drawable.avatars_woman);
                            chosenId = getResources().getIdentifier("avatars_woman", "drawable", "ru.goodibunakov.itravel");
                            break;
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //если клик по аватарке, то диалог выбора авы
            case (R.id.imageview_ava):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_ava_dialog);
                final View view2 = getLayoutInflater().inflate(R.layout.dialog_ava_choose, null);
                builder.setView(view2);
                final RecyclerView avaRecycler = (RecyclerView) view2.findViewById(R.id.ava_recycler);
                avaRecycler.setLayoutManager(new GridLayoutManager(PersonEditActivity.this, 3));
                avaRecycler.setHasFixedSize(true);
                final AvaChooseRecyclerAdapter avaChooseRecyclerAdapter = new AvaChooseRecyclerAdapter(PersonEditActivity.this, new AvaChooseRecyclerAdapter.AvaViewHolder.MyClickListener() {
                    @Override
                    public void onAvaClickListener(int position) {
                        //Animation anim = AnimationUtils.loadAnimation(PersonEditActivity.this, R.anim.anim_checkava);
//                        avavavava.setAnimation(anim);
//                        avavavava.startAnimation(anim);
                        Toast.makeText(view2.getContext(), "Выбрана ава " + position, Toast.LENGTH_SHORT).show();
                        chosenId = (int) avaRecycler.getAdapter().getItemId(position);
                        avaRecycler.getAdapter().notifyItemChanged(position);


                        //checkPic.setVisibility(View.VISIBLE);
                        Log.d("fdgdfgdfgdf", String.valueOf(avaRecycler.getAdapter().getItemId(position)));
                        Log.d("Жопа", String.valueOf(chosenId));
                    }
                });
                avaRecycler.setAdapter(avaChooseRecyclerAdapter);

                builder.setPositiveButton(R.string.btn_ava_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        avaChosenByUser = true;
                        ava.setImageResource(chosenId);
                    }
                });
                builder.setNegativeButton(R.string.btn_ava_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (chosenId == 0) avaChosenByUser = false;
                    }
                });
                AlertDialog chooseAva = builder.create();
                chooseAva.setCancelable(false);
                chooseAva.show();
                break;
            //обработка нажатия на ОК, сохранить персону и передать ее в активити создания поездки
            case (R.id.btn_ok_person):
                editTextName = (EditText) findViewById(R.id.edittext_name);


                String name = editTextName.getText().toString();
                String age = editTextAge.getText().toString();
                //определяем пол участника: мужской пол = 0, женский пол = 1
                String sex;
                if (groupSex.getPosition() == 0) sex = "man";
                else sex = "woman";
                if (name.isEmpty()) {
                    Toast.makeText(PersonEditActivity.this, getResources().getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                } else if (age.isEmpty()) {
                    Toast.makeText(PersonEditActivity.this, getResources().getString(R.string.enter_age), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("sex", sex);
                    intent.putExtra("ava", chosenId);
                    setResult(0, intent);
                    finish();
                }
        }
    }
}
