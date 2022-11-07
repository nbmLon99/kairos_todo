package com.nbmlon.submodule.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.submodule.R;

import java.util.ArrayList;

public class searchResRV_adpater extends RecyclerView.Adapter<searchResRV_adpater.ViewHolder> {

    /** String[] = {title, content, table_naem}  **/
    private ArrayList<String[]> mResults;
    private SearchResClickListener listener;

    public searchResRV_adpater() {
        this.mResults = new ArrayList<>();
    }


    public searchResRV_adpater(ArrayList<String[]> results, SearchResClickListener listener) {
        this.mResults = results;
        this.listener = listener;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView resTitle;
        TextView resContent;
        TextView resDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resTitle = itemView.findViewById(R.id.searchRes_title);
            resContent = itemView.findViewById(R.id.searchRes_content);
            resDate = itemView.findViewById(R.id.searchRes_Date);
            itemView.setOnClickListener(v -> listener.ResClicked(resDate.getText().toString()));
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.serach_result_frame, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.resTitle.setText(mResults.get(position)[0]);
        if (mResults.get(position)[1] != null)
            holder.resContent.setText(mResults.get(position)[1].replaceAll(System.getProperty("line.separator")," "));
        holder.resDate.setText(mResults.get(position)[2]);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

}
