package com.example.vache.todo;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@androidx.room.Database(entities = {TodoModel.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {

    private static final String DATABASE_NAME = "db-todos";

    private static Database INSTANCE;

    private static final Object lock = new Object();

    public abstract TodoDao todoDao();

    public static Database getInstance(){
        synchronized (lock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                        MainActivity.getContext(),
                        Database.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

}