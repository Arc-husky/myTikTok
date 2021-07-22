package com.example.mainacticity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText = findViewById(R.id.idText);

    }

    public void login(View view) {
        CharSequence content = editText.getText();
        if (!TextUtils.isEmpty(content)) {
            SharedPreferences sp = getSharedPreferences(MainActivity.ID_SAVED, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(MainActivity.MY_ID_SAVE_KEY, content.toString());
            editor.apply();
            Intent intent = new Intent();
            intent.putExtra(MainActivity.MY_ID_SAVE_KEY,content.toString());
            setResult(RESULT_OK,intent);
            finish();
        }else {
            Toast.makeText(this,"请输入id",Toast.LENGTH_SHORT);
        }
    }
}