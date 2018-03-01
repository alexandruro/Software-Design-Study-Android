package com.alexandruro.recyclinghelper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.ViewHolder> {

    ArrayList<AssistantSuggestion> arrayList;

    public AssistantAdapter(ArrayList<AssistantSuggestion> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assistant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AssistantSuggestion card = arrayList.get(position);

        ((TextView)holder.itemView.findViewById(R.id.title)).setText(card.getTitle());
        ((TextView)holder.itemView.findViewById(R.id.subtitle)).setText(card.getSubtitle());
        ((Button)holder.itemView.findViewById(R.id.positive_button)).setText(card.getPositiveButtonText());

        ((ImageView)holder.itemView.findViewById(R.id.image)).setImageDrawable(card.getImage());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
