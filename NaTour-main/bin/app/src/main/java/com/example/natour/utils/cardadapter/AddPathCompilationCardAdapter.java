package com.example.natour.utils.cardadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.data.Compilation;

import java.util.ArrayList;

public class AddPathCompilationCardAdapter extends RecyclerView.Adapter<AddPathCompilationCardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Compilation> compilations;
    private boolean [] checked;
    public AddPathCompilationCardAdapter(Context context, ArrayList<Compilation> compilations){
        this.compilations = compilations;
        this.context = context;
        checked = new boolean[compilations.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View view = inflater.inflate(R.layout.compilation_add_card, parent, false);
        return new AddPathCompilationCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.compilationName.setText(compilations.get(position).getName());
            holder.setCompilation(compilations.get(position));
            holder.compilationcheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checked[holder.getAdapterPosition()] = !checked[holder.getAdapterPosition()];
                }
            });
    }

    public boolean[] getChecked() {
        return checked;
    }

    public void clear() {
        int size = compilations.size();
        compilations.clear();
        notifyItemRangeRemoved(0, size);
    }
    @Override
    public int getItemCount() {
        return compilations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView compilationName;
        CheckBox compilationcheckBox;
        Compilation compilation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            compilationName = itemView.findViewById(R.id.add_compilation_text_name);
            compilationcheckBox = itemView.findViewById(R.id.add_compilation_check_box);
        }

        public CheckBox getCompilationcheckBox() {
            return compilationcheckBox;
        }

        private void setCompilation(Compilation compilation){
            this.compilation = compilation;
        }

        public Compilation getCompilation() {
            return compilation;
        }
    }
}
