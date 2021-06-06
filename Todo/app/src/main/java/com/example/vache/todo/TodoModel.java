package com.example.vache.todo;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "data")
public class TodoModel {

    public TodoModel(String title, ArrayList<String> items){
        this.setTitle(title);
        this.items.addAll(items);
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "pinned")
    private boolean pinned;

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    @ColumnInfo(name = "editDate")
    private String editDate;

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @ColumnInfo(name = "items")
    private ArrayList<String> items = new ArrayList<>();

    @ColumnInfo(name = "checked")
    private ArrayList<String> checked = new ArrayList<>();

    //----------------------------------------------------------------

    public ArrayList<String> getChecked() {
        return checked;
    }

    public void setChecked(ArrayList<String> checked) {
        this.checked = checked;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items.addAll(items);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}