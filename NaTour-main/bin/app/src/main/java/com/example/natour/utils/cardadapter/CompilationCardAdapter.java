package com.example.natour.utils.cardadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour.R;
import com.example.natour.data.Compilation;
import com.example.natour.activities.CompilationPathsActivity;

import java.util.ArrayList;

public class CompilationCardAdapter extends RecyclerView.Adapter<CompilationCardAdapter.ViewHolder> {

    ArrayList<Compilation> compilations;
    Context context;

    public CompilationCardAdapter(Context context, ArrayList<Compilation> compilations){
        this.compilations = compilations;
        this.context = context;
    }
    @NonNull
    @Override
    public CompilationCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.compilation_card, parent, false);
        return new CompilationCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompilationCardAdapter.ViewHolder holder, int position) {
            Compilation compilation = compilations.get(position);
            holder.setCompilation(compilation);
            holder.title.setText(compilation.getName());
            String numPaths = compilation.getPaths().size() + "Paths";
            holder.numPaths.setText(numPaths);
    }



    @Override
    public int getItemCount() {
        return compilations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private Compilation compilation;
        private TextView title;
        private TextView numPaths;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.compilation_title);
            numPaths = itemView.findViewById(R.id.compilation_paths_num);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CompilationPathsActivity.class);
                    intent.putExtra("compilation", compilation);
                    context.startActivity(intent);
                }
            });
        }


        private void setCompilation(Compilation compilation){
            this.compilation = compilation;
        }
    }
}
