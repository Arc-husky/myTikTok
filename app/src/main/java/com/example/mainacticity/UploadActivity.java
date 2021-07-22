package com.example.mainacticity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mainacticity.model.UploadResponse;

import java.io.InputStream;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_VIDEO_SIZE = 28 * 1024 * 1024;
    private static final long MAX_COVER_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_VIDEO = 102;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";
    public static final String VIDEO_OUTER_PATH = "video-path";
    private IApi api;
    private Uri coverImageUri;
    private Uri videoUri;
    private Drawable coverSD;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initNetwork();
        button = findViewById(R.id.chooseImage);
        findViewById(R.id.chooseImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "选择视频");
            }
        });
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_VIDEO == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                //coverSD.setImageURI(coverImageUri);
                //TODO: select the cover image
                String ImagePath = CoverCapture.getCover(videoUri.toString());
                coverImageUri = Uri.parse(ImagePath);
                coverSD = LoadImageFromWebOperations(coverImageUri.toString());
                button.setImageDrawable(coverSD);
                if (videoUri != null) {
                    Log.d(TAG, "pick video " + videoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }


    private void initNetwork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);

    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        byte[] videoData = readDataFromUri(videoUri);
        if (videoData == null || videoData.length == 0) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (coverImageData.length >= MAX_COVER_SIZE || videoData.length >= MAX_VIDEO_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Part video = MultipartBody.Part.createFormData("video", "upload.mp4", RequestBody.create(MediaType.parse("multipart/from_data"), videoData));
        MultipartBody.Part cover_image = MultipartBody.Part.createFormData("cover_image", "cover.png", RequestBody.create(MediaType.parse("multipart/form_data"), coverImageData));
        Call<UploadResponse> call = api.submitVideo(Constants.STUDENT_ID, Constants.USER_NAME, "", cover_image, video, Constants.token);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "upload failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "upload succeeded", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Upload succeeded");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "upload failed", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    private Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("Exc=" + e);
            return null;
        }
    }
}