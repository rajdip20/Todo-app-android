package com.example.justdoit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.justdoit.task_database.TaskContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView TaskList;

    private ArrayAdapter<String> arrayAdapter;

    private TaskDatabaseHelper taskHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskList = findViewById(R.id.list_todo);

        taskHelper = new TaskDatabaseHelper(this);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.add_task:
                final EditText taskEdit = new EditText(this);

                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Add A New Task").setMessage("What do you want to do next?").setView(taskEdit).setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String task = String.valueOf(taskEdit.getText());
                        SQLiteDatabase database = taskHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.TASK_TITLE, task);

                        database.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                        database.close();

                        updateUI();
                    }
                }).setNegativeButton("Cancel",null).create();

                dialog.show();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteTask(View view){

        View parent = (View) view.getParent();
        TextView taskText = parent.findViewById(R.id.title_task);

        String task = String.valueOf(taskText.getText());

        SQLiteDatabase database = taskHelper.getWritableDatabase();
        database.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry.TASK_TITLE + " = ?", new String[]{task});

        database.close();
        updateUI();
    }

    private void updateUI(){

        ArrayList<String> taskList = new ArrayList<>();

        SQLiteDatabase database = taskHelper.getReadableDatabase();

        Cursor cursor = database.query(TaskContract.TaskEntry.TABLE, new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.TASK_TITLE}, null, null, null, null, null);

        while (cursor.moveToNext()){

            int index = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TITLE);
            taskList.add(cursor.getString(index));
        }

        if (arrayAdapter == null){

            arrayAdapter = new ArrayAdapter<>(this, R.layout.todo_task, R.id.title_task, taskList);
            TaskList.setAdapter(arrayAdapter);
        }else {

            arrayAdapter.clear();
            arrayAdapter.addAll(taskList);
            arrayAdapter.notifyDataSetChanged();
        }

        cursor.close();
        database.close();
    }
}