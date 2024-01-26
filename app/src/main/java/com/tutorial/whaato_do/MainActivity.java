package com.tutorial.whaato_do;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tutorial.whaato_do.db.TaskDBHelper;
import com.tutorial.whaato_do.db.TaskContract;

import java.util.ArrayList;

public class MainActivity extends Activity {

    // Declare variables
    ArrayAdapter<String> taskAdapter; // Adapter for ListView
    ListView taskListView; // ListView to display tasks
    ArrayList<String> tasks; // List of tasks
    private TaskDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView, tasks list, and ArrayAdapter
        taskListView = findViewById(R.id.lvTask);
        tasks = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tasks);
        taskListView.setAdapter(taskAdapter);

        dbHelper = new TaskDBHelper(this);

        // Load tasks from the database
        loadTasks();

        // Setup long click listener for ListView items
        setupListViewLongClickListener();
    }

    // To setup long click listener for ListView items
    private void setupListViewLongClickListener() {
        taskListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            // Show delete confirmation dialog
            showDeleteConfirmationDialog(i);
            return true;
        });
    }

    // To show delete confirmation dialog
    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If user confirms deletion, delete the task
                    deleteTask(position);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // If user cancels deletion, dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }

    // To delete task
    private void deleteTask(int position) {
        String taskToDelete = tasks.get(position);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TaskContract.TaskEntry.COLUMN_TASK_NAME + " = ?";
        String[] selectionArgs = {taskToDelete};
        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
        tasks.remove(position);
        taskAdapter.notifyDataSetChanged(); // Update ListView
    }

    // To add a new task to the database
    public void addTask(View view) {
        EditText etNewTask = findViewById(R.id.etNewTask);
        String itemText = etNewTask.getText().toString();
        if (itemText.isEmpty()) {
            // Alert if task is empty
            Toast.makeText(this, "Task is empty!", Toast.LENGTH_SHORT).show();

        } else if (tasks.contains(itemText)) {
            // Alert if task already exists
            Toast.makeText(this, "Task already exists!", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, itemText);
            long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
            if (newRowId != -1) {
                tasks.add(itemText);
                taskAdapter.notifyDataSetChanged();
                etNewTask.setText("");
            } else {
                Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // To load tasks from the database
    private void loadTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TaskContract.TaskEntry.COLUMN_TASK_NAME};
        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TASK_NAME));
            tasks.add(taskName);
        }
        cursor.close();
    }
}
