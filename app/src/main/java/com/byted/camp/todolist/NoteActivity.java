package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.noteEntry;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.security.PrivateKey;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private int priority = 1;
    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;

    TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoDbHelper = new TodoDbHelper(this);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        cb1 = findViewById(R.id.CB1);
        cb2 = findViewById(R.id.CB2);
        cb3 = findViewById(R.id.CB3);
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        cb1.setOnCheckedChangeListener(new cbListener());
        cb2.setOnCheckedChangeListener(new cbListener());
        cb3.setOnCheckedChangeListener(new cbListener());
    }

    private class cbListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String text = buttonView.getText().toString();
            if(!isChecked){
                priority = 1;
                return;
            }
            if(text.equals("一般")){
                priority = 1;
                cb2.setChecked(false);
                cb3.setChecked(false);
            }else if(text.equals("重要")){
                priority = 2;
                cb1.setChecked(false);
                cb3.setChecked(false);
            }else{
                priority = 3;
                cb1.setChecked(false);
                cb2.setChecked(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content,int priority) {
        // TODO 插入一条新数据，返回是否插入成功
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(noteEntry.TABLE_DATE,System.currentTimeMillis());
        contentValues.put(noteEntry.TABLE_STATE, 0);
        contentValues.put(noteEntry.TABLE_CONTENT,content);
        contentValues.put(noteEntry.TABLE_PRIORITY,priority);
        return db.insert(noteEntry.TABLE_NAME,null, contentValues) != -1;
    }
}
