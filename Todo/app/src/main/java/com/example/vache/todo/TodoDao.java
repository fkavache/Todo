package com.example.vache.todo;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface TodoDao {

    @Query("SELECT * FROM data")
    List<TodoModel> getData();

    @Query("SELECT * FROM data WHERE pinned = :pinned")
    List<TodoModel> getDataP(boolean pinned);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertData(TodoModel todo);

    @Delete
    void delete(TodoModel todo);

    @Query("SELECT * FROM data WHERE id = :id ")
    TodoModel loadSingle(int id);

    @Query("DELETE FROM data WHERE id = :id")
    void deleteById(int id);
}
