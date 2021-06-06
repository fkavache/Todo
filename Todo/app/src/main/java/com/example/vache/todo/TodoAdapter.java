package com.example.vache.todo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoModel> data;
    private Context ctx;
    private boolean itemsAdded;

    public TodoAdapter(List<TodoModel> data, Context ctx) {
        this.ctx = ctx;
        this.data = data;
    }

    public void setData(ArrayList<TodoModel> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        int layoutId = R.layout.res_cell;

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        itemsAdded = false;
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder itemViewHolder, int index) {

        itemViewHolder.title.setText(data.get(index).getTitle());

        if(!itemsAdded) {
            ArrayList<String> items = data.get(index).getItems();
            for (int i = 0; i < items.size(); i++) {
                TextView vi = new TextView(ctx);
                vi.setText(items.get(i));
                itemViewHolder.listView.addView(vi);
            }
            int checkedSize = data.get(index).getChecked().size();
            if (checkedSize > 0) {
                TextView checkedTxt = new TextView(ctx);
                checkedTxt.setText("+ " + checkedSize + " checked item");
                itemViewHolder.listView.addView(checkedTxt);
            }
        }
        itemsAdded = true;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView title;
        public LinearLayout listView;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            title = itemView.findViewById(R.id.title);

            listView = itemView.findViewById(R.id.list_view);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TodoModel todo = data.get(position);
            Intent intent = new Intent(ctx, EditorActivity.class);
            intent.putExtra("id", todo.getId());
            ctx.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            return true;
        }
    }

    public String filter(String str){
        int maxLen = 15;
        if(str.length() >= maxLen)
            return str.substring(0, maxLen) + "...";
        return str;
    }
}