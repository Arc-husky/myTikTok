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
    EditText userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText = findViewById(R.id.idText);
        userText = findViewById(R.id.usernameText);
    }

    public void login(View view) {
        CharSequence content = editText.getText();
        CharSequence username = userText.getText();
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(username)) {
            SharedPreferences sp = getSharedPreferences(MainActivity.ID_SAVED, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(MainActivity.MY_ID_SAVE_KEY, content.toString());
            editor.putString(MainActivity.USERNAME_SAVED_KEY,username.toString());
            editor.apply();
            Intent intent = new Intent();
            intent.putExtra(MainActivity.MY_ID_SAVE_KEY,content.toString());
            Constants.USER_NAME = username.toString();
            setResult(RESULT_OK,intent);
            finish();
        }else if(TextUtils.isEmpty(content)){
            Toast.makeText(this,"请输入id",Toast.LENGTH_SHORT);
        }else {
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT);
        }
    }
}