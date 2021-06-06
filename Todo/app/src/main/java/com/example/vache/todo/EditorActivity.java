package com.example.vache.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {

    private Button listItem;
    private LinearLayout editorList;
    private EditText title;
    private EditText note;
    private TextView editDateV;
    private Toolbar toolbar;
    private TodoModel resModel;
    private Context ctx;
    private LinearLayout checkedList;

    private String titleMsg;
    private ArrayList<String> notesMsg;
    private ArrayList<String> checkedNotes;
    private int id;
    private Boolean isPinned;
    private Database database;
    private TodoDao todoDao;
    private String editDate = "";
    private TextView chkTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        lightStatusBar(getWindow().getDecorView());
        ctx = this;
        database = Database.getInstance();
        todoDao = database.todoDao();

        resModel = new TodoModel("Untitled", new ArrayList<String>());
        listItem = findViewById(R.id.list_item);
        editorList = findViewById(R.id.editor_list);
        note = findViewById(R.id.note);
        title = findViewById(R.id.title);
        editDateV = findViewById(R.id.edit_date);
        checkedList = findViewById(R.id.checked_list);
        chkTxt = findViewById(R.id.chkTxt);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isPinned = false;
        checkedNotes = new ArrayList<>();

        final Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        LinearLayout def = (LinearLayout)findViewById(R.id.nested_linear);
        CheckBox defChk = (CheckBox)def.getChildAt(0);
        chkListener(defChk);
        if(id != -1){
            TodoModel modelB = todoDao.loadSingle(id);
            notesMsg = modelB.getItems();
            isPinned = modelB.isPinned();
            editDate = modelB.getEditDate();
            titleMsg = modelB.getTitle();
            checkedNotes = modelB.getChecked();

            editDateV.setText(editDate);
            title.setText(titleMsg);
            if(notesMsg.size() > 0) {
                note.setText(notesMsg.get(0));
                layoutInflate(modelB);
            }
        }

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout vi = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.note, null);
                CheckBox newChk = (CheckBox)vi.getChildAt(0);
                chkListener(newChk);
                EditText vic = (EditText)vi.getChildAt(1);
                vic.requestFocus();
                editorList.addView(vi);
            }
        });
    }

    private void layoutInflate(TodoModel modelB){
        ArrayList<String> items = modelB.getItems();
        ArrayList<String> checkedBoo = modelB.getChecked();

        for (int i = 1; i < items.size(); i++) {
            LinearLayout vi = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.note, null);
            EditText vic = (EditText)vi.getChildAt(1);
            CheckBox chk = (CheckBox)vi.getChildAt(0);
            chkListener(chk);
            vic.setText(items.get(i));
            editorList.addView(vi);
        }
        int chkSize = checkedBoo.size();
        if(chkSize > 0)
            chkTxt.setVisibility(View.VISIBLE);

        for (int i = 0; i < chkSize; i++) {
            TextView newT = new TextView(ctx);
            newT.setText(checkedBoo.get(i));
            newT.setPaintFlags(newT.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkedList.addView(newT);
        }
    }

    private void chkListener(CheckBox chk){
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   LinearLayout each = (LinearLayout)buttonView.getParent();
                   editorList.removeView(each);
                   TextView checked = (TextView)each.getChildAt(1);
                   String checkedStr = checked.getText().toString();
                   TextView newT = new TextView(ctx);
                   if(!checkedStr.matches("")) {
                       newT.setText(checked.getText());
                       newT.setPaintFlags(checked.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                       checkedNotes.add(checkedStr);
                       checkedList.addView(newT);
                       chkTxt.setVisibility(View.VISIBLE);
                   }
               }
           }
        );
    }

    private void lightStatusBar(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            this.getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        save(title.getText().toString(), null);
        Intent intent = new Intent(ctx, MainActivity.class);
        startActivity(intent);
    }

    private void save(String titleStr, ArrayList<String> notesRes){
        ArrayList<String> notes = new ArrayList<>();
        for (int i = 0; i < editorList.getChildCount(); i++) {
            LinearLayout vi = (LinearLayout) editorList.getChildAt(i);
            EditText vic = (EditText)vi.getChildAt(1);
            String note = vic.getText().toString();
            if(!note.matches(""))
                notes.add(note);
        }

        String titleRes =  titleStr.matches("") ? "Untitled" : titleStr;

        TodoModel todo = new TodoModel(titleRes, notesRes == null ? notes : notesRes);
        todo.setPinned(isPinned);
        todo.setEditDate(getFormatedDate());
        todo.setChecked(checkedNotes);
        todoDao.insertData(todo);
        if(id > 0)
            todoDao.delete(todoDao.loadSingle(id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this,
                isPinned ? R.drawable.ic_action_pin_black : R.drawable.ic_action_pin));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete:
                Database database = Database.getInstance();
                TodoDao todoDao = database.todoDao();
                if(id > 0)
                    todoDao.deleteById(id);
                intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.pin:
                item.setIcon(ContextCompat.getDrawable(this,
                isPinned ? R.drawable.ic_action_pin : R.drawable.ic_action_pin_black));
                isPinned = !isPinned;
                return true;
            default:
                return true;
        }
    }

    private String getFormatedDate(){
        String pattern = "E dd MMMM 'at' HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new java.util.Date());
    }

}
