package ru.goodibunakov.itravel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

public class AvaChooseRecyclerAdapter extends RecyclerView.Adapter<AvaChooseRecyclerAdapter.AvaViewHolder> {

    private static final String TAG = "AvaChooseAdapter";
    Context context;
    private AvaChooseRecyclerAdapter.AvaViewHolder.MyClickListener myClickListener;

    List<Integer> resourceIds = Arrays.asList(
            R.drawable.avatars_01, R.drawable.avatars_02, R.drawable.avatars_03,
            R.drawable.avatars_04, R.drawable.avatars_05, R.drawable.avatars_06,
            R.drawable.avatars_07, R.drawable.avatars_08, R.drawable.avatars_09,
            R.drawable.avatars_10, R.drawable.avatars_11, R.drawable.avatars_12,
            R.drawable.avatars_13, R.drawable.avatars_14, R.drawable.avatars_15,
            R.drawable.avatars_16, R.drawable.avatars_17, R.drawable.avatars_18,
            R.drawable.avatars_19, R.drawable.avatars_20, R.drawable.avatars_21,
            R.drawable.avatars_22, R.drawable.avatars_23, R.drawable.avatars_24,
            R.drawable.avatars_25, R.drawable.avatars_26, R.drawable.avatars_27,
            R.drawable.avatars_28, R.drawable.avatars_29, R.drawable.avatars_30,
            R.drawable.avatars_31, R.drawable.avatars_32, R.drawable.avatars_33,
            R.drawable.avatars_34, R.drawable.avatars_35, R.drawable.avatars_36,
            R.drawable.avatars_37, R.drawable.avatars_38, R.drawable.avatars_39,
            R.drawable.avatars_40, R.drawable.avatars_41, R.drawable.avatars_42,
            R.drawable.avatars_43, R.drawable.avatars_44, R.drawable.avatars_45,
            R.drawable.avatars_46, R.drawable.avatars_47, R.drawable.avatars_48,
            R.drawable.avatars_49, R.drawable.avatars_50, R.drawable.avatars_51,
            R.drawable.avatars_52, R.drawable.avatars_53, R.drawable.avatars_54,
            R.drawable.avatars_55, R.drawable.avatars_56, R.drawable.avatars_57,
            R.drawable.avatars_58, R.drawable.avatars_59, R.drawable.avatars_60,
            R.drawable.avatars_61, R.drawable.avatars_62, R.drawable.avatars_63,
            R.drawable.avatars_64, R.drawable.avatars_65, R.drawable.avatars_66);

    public AvaChooseRecyclerAdapter(Context context, AvaChooseRecyclerAdapter.AvaViewHolder.MyClickListener m) {
        this.context = context;
        myClickListener = m;
    }

    public static class AvaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected MyClickListener myClickListener;
        protected ImageView iv;

        public AvaViewHolder(View itemView, MyClickListener myClickListener) {
            super(itemView);
            this.myClickListener = myClickListener;
            iv = (ImageView) itemView.findViewById(R.id.ava_item_imageview);
            iv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myClickListener != null) myClickListener.onAvaClickListener(getLayoutPosition());
        }

        public interface MyClickListener {
            void onAvaClickListener(int position);
        }
    }

    @Override
    public long getItemId(int position) {
        return resourceIds.get(position);
    }

    @Override
    public AvaChooseRecyclerAdapter.AvaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ava_item, parent, false);
        v.setPadding(14, 14, 14, 14);
        return new AvaViewHolder(v, myClickListener);
    }

    @Override
    public void onBindViewHolder(AvaViewHolder holder, int position) {
        holder.iv.setImageResource(resourceIds.get(position));
        Log.d(TAG, "onBindViewHolder position: " + position + " | " + holder.toString());
    }

    @Override
    public int getItemCount() {
        return resourceIds.size();
    }
}