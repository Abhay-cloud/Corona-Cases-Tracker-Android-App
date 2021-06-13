package com.hcr2bot.coronatracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {


    private Context context;
    private List<StateList>  stateLists;

    public CustomRecyclerAdapter(Context context, List<StateList> stateLists) {
        this.context = context;
        this.stateLists = stateLists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.states_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(stateLists.get(position));

        StateList stateList = stateLists.get(position);

        holder.stateNameList.setText(stateList.getStateName());
        holder.stateCases.setText(String.valueOf(stateList.getConfirmedCases()));

    }

    @Override
    public int getItemCount() {
        return stateLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView stateNameList;
        public TextView stateCases;

        public ViewHolder(View itemView) {
            super(itemView);

            stateNameList = (TextView) itemView.findViewById(R.id.stateNameList);
            stateCases = (TextView) itemView.findViewById(R.id.stateConfirmedList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    StateList sList = (StateList) view.getTag();

                    String statename = sList.getStateName();

                    FancyToast.makeText(context, "Fetching stats...", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                    Intent intent = new Intent(view.getContext(), india.class);
                    intent.putExtra("STATE_NAME", statename.toLowerCase());
                    context.startActivity(intent);



                }
            });

        }
    }




}
