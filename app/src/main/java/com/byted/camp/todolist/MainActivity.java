package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoContract.noteEntry;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Templates;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoDbHelper = new TodoDbHelper(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        SQLiteDatabase db = todoDbHelper.getReadableDatabase();
        List<Note> result = new ArrayList<Note>();
        String[] columns = new String[]{
                noteEntry._ID,
                noteEntry.TABLE_DATE,
                noteEntry.TABLE_STATE,
                noteEntry.TABLE_PRIORITY,
                noteEntry.TABLE_CONTENT
        };

        Cursor cursor = db.query(
                noteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                noteEntry.TABLE_PRIORITY+ " DESC"
        );
        while (cursor.moveToNext()){
            Note temp = new Note(cursor.getLong(cursor.getColumnIndex(noteEntry._ID)));
            temp.setState(State.from(cursor.getInt(cursor.getColumnIndex(noteEntry.TABLE_STATE))));
            temp.setDate(new Date(cursor.getLong(cursor.getColumnIndex(noteEntry.TABLE_DATE))));
            temp.setContent(cursor.getString(cursor.getColumnIndex(noteEntry.TABLE_CONTENT)));
            temp.setPriority(cursor.getInt(cursor.getColumnIndex(noteEntry.TABLE_PRIORITY)));
            result.add(temp);
        }
        cursor.close();
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
       SQLiteDatabase db = todoDbHelper.getWritableDatabase();
       db.delete(noteEntry.TABLE_NAME,noteEntry._ID+ "= ?",new String[]{Long.toString(note.id)});
       db.close();
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(noteEntry.TABLE_STATE,note.getState().intValue);
        db.update(noteEntry.TABLE_NAME,contentValues,noteEntry._ID + " = ?",new String[]{Long.toString(note.id)});
    }
}
