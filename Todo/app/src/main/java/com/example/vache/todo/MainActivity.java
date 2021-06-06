package com.example.vache.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private TodoAdapter adapter;
    private Button takeANote;
    private static Context ctx;
    private EditText search;
    private TextView pinnedTxt;
    private TextView othersTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeANote = findViewById(R.id.add);
        search = findViewById(R.id.search);
        search.clearFocus();
        ctx = this;
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView2 = findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(false);
        recyclerView2.setHasFixedSize(false);

        pinnedTxt = findViewById(R.id.pinned_txt);
        othersTxt = findViewById(R.id.others_txt);

        lightStatusBar(getWindow().getDecorView());

        Database database = Database.getInstance();
        TodoDao todoDao = database.todoDao();

        RecyclerView.LayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        final List<TodoModel> todos = todoDao.getData();
        final List<TodoModel> todosP = todoDao.getDataP(true);
        final List<TodoModel> todosUP = todoDao.getDataP(false);
        final TodoAdapter adapter2 = new TodoAdapter(todosUP, this);
        if (todosP.size() != 0) {
            pinnedTxt.setVisibility(View.VISIBLE);
            othersTxt.setVisibility(View.VISIBLE);
            recyclerView2.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager lm2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView2.setLayoutManager(lm2);
            recyclerView2.setAdapter(adapter2);

        }

        recyclerView.setLayoutManager(lm);
        final TodoAdapter adapter = new TodoAdapter(todosP.size() == 0 ? todos : todosP, this);
        recyclerView.setAdapter(adapter);

        takeANote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, EditorActivity.class);
                startActivity(intent);
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                ArrayList<TodoModel> todosSearchedP = new ArrayList<>();
                ArrayList<TodoModel> todosSearchedUP = new ArrayList<>();
                if(todosP.size() == 0){
                    for (TodoModel todo: todos)
                        if(todo.getTitle().toLowerCase().contains(s))
                            todosSearchedP.add(todo);
                    adapter.setData(todosSearchedP);
                }else{
                    for (TodoModel todo: todosP)
                        if(todo.getTitle().toLowerCase().contains(s))
                            todosSearchedP.add(todo);
                    adapter.setData(todosSearchedP);
                    for (TodoModel todo: todosUP)
                        if(todo.getTitle().toLowerCase().contains(s))
                            todosSearchedUP.add(todo);
                    adapter2.setData(todosSearchedUP);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void lightStatusBar(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    public static Context getContext() {
        return ctx;
    }

    @Override
    public void onBackPressed() {
        search.setText("");
    }
}
