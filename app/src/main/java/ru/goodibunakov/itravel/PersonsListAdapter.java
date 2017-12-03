package ru.goodibunakov.itravel;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class PersonsListAdapter extends RecyclerView.Adapter<PersonsListAdapter.PersonsViewHolder> {

    List<HashMap<String, String>> persons;

    public PersonsListAdapter(List<HashMap<String, String>> persons) {
        this.persons = persons;
    }

    public static class PersonsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView ava;
        protected TextView name;

        public PersonsViewHolder(View itemView) {
            super(itemView);
            ava = (ImageView) itemView.findViewById(R.id.personlist_ava);
            name = (TextView) itemView.findViewById(R.id.personlist_name);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public PersonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_and_name_avatar, parent, false);
        v.setPadding(4, 4, 4, 4);
        return new PersonsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonsViewHolder holder, int position) {
        holder.ava.setImageResource(Integer.parseInt(persons.get(position).get("ava")));
        holder.ava.setAdjustViewBounds(true);
        holder.name.setText(persons.get(position).get("name"));
        Log.e("адаптер", String.valueOf(persons));

        // У меня есть id ресурса-строка из файла R. например: 21346466556. Это mp3 файл в папке raw. Как мне его преобразовать в File?
        // mContext.getResources().getResourceEntryName(Integer.parseInt("ResourceId")));
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}