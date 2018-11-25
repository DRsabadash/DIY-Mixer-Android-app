package com.example.drsabs.diymixer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;

public class FlavorAdapter extends RecyclerView.Adapter<FlavorAdapter.MyViewHolder> {
    public static List<Recipe.Flavor> flavorList;
    private Context context;
    private int i = 0;

    public FlavorAdapter(Context context, List<Recipe.Flavor> flavorList) {
        this.context = context;
        this.flavorList = flavorList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flavor_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final Recipe.Flavor flavor = flavorList.get(position);
        holder.flavorName.setText(flavor.getName());
        holder.flavorStr.setText(flavor.getStrength().toString());
        holder.flavorStr.setTag(flavorList.size() - 1);
        holder.flavorName.setTag(flavorList.size() - 1);


    }

    @Override
    public int getItemCount() {
        return flavorList.size();
    }

    public void removeItem(int position) {
        flavorList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Recipe.Flavor flavor, int position) {
        flavorList.add(position, flavor);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public EditText flavorName;
        public EditText flavorStr;
        public RelativeLayout viewBackground, viewForeground;


        public MyViewHolder(View view) {
            super(view);
            flavorName = (EditText) view.findViewById(R.id.flavor);
            flavorStr = (EditText) view.findViewById(R.id.strength);


            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

        }
    }


}
