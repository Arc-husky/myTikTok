package com.example.mainacticity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.mainacticity.model.UploadResponse;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String absolute_Path;
    private static final String MY_ID_SAVE_KEY = "my-id";
    private String MY_ID;

    public static Uri getUriForFile(Context context, String path) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Intent intent = getIntent();
        button = findViewById(R.id.chooseImage);
        String str = intent.getStringExtra(VIDEO_OUTER_PATH);
        MY_ID = intent.getStringExtra(MY_ID_SAVE_KEY);
        if (str != null) {
            absolute_Path = intent.getStringExtra(VIDEO_OUTER_PATH);
            videoUri = getUriForFile(this, absolute_Path);

            coverImageUri = getUriForFile(CoverCapture.getCover(this, absolute_Path, getOutputMediaPath()));

            Glide.with(this).load(coverImageUri).into(button);
//            button.setImageBitmap();
        }
        initNetwork();
        findViewById(R.id.chooseImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
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
                //String path = handleImageOnKitKat(videoUri);
                //String ImagePath = CoverCapture.getCover(videoUri.toString());
                //coverImageUri = Uri.parse(ImagePath);
                //coverSD = LoadImageFromWebOperations(coverImageUri.toString());
                //button.setImageDrawable(coverSD);
                if (videoUri != null) {
                    Log.d(TAG, "pick video " + videoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        } else if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                Glide.with(this).load(coverImageUri).into(button);
                if (coverImageUri != null) {
//                    coverSD = LoadImageFromWebOperations(coverImageUri.toString());
//                    button.setImageDrawable(coverSD);
                    Log.d(TAG, "pick video " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }
            }
        }
    }

    private String handleImageOnKitKat(Uri uri) {
        String imagePath = null;
//        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, id);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //Log.d(TAG, "content: " + uri.toString());
            imagePath = getImagePath(uri, null, null);
        }
        return imagePath;
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
        Call<UploadResponse> call = api.submitVideo(MY_ID, Constants.USER_NAME, "", cover_image, video, Constants.token);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                Toast.makeText(getBaseContext(), "upload succeeded", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Upload succeeded");
                    finish();

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

    private String getImagePath(Uri uri, String selection, String Arg) {
        String path = null;
        String[] Args = {Arg};
        Cursor cursor = getContentResolver().query(uri, null, selection, Args, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }

            cursor.close();
        }
        return path;
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    //    public static Uri getMediaUriFromPath(Context context, String path) {
//        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Cursor cursor = context.getContentResolver().query(mediaUri,
//                null,
//                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
//                new String[] {path.substring(path.lastIndexOf("/") + 1)},
//                null);
//
//        Uri uri = null;
//        if(cursor.moveToFirst()) {
//            uri = ContentUris.withAppendedId(mediaUri,
//                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
//        }
//        cursor.close();
//        return uri;
//    }
//    public static Uri getImageStreamFromExternal(String imageName) {
//        File externalPubPath = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES
//        );
//
//        File picPath = new File(externalPubPath, imageName);
//        Uri uri = null;
//        if(picPath.exists()) {
//            uri = Uri.fromFile(picPath);
//        }
//
//        return uri;
//    }
    private Uri getUriForFile(String takeImagePath) {
        Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.example.mainacticity.fileprovider", new File(takeImagePath));
        return contentUri;
    }

}