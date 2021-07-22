package com.example.mainacticity;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

public class CoverCapture {

    public static String getCover(String videoPath) {
        Bitmap bitmap = getVideoThumbnail(videoPath,540,960,MINI_KIND);
        return bitmap2File(bitmap,"VideoCaputer_cover");
    }

    private static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }
    /**

     * Bitmap保存成File

     *

     * @param bitmap input bitmap

     * @param name output file's name

     * @return String output file's path

     */

    private static String bitmap2File(Bitmap bitmap, String name) {

        File f = new File(Environment.getExternalStorageDirectory() + name +  ".jpg");

        if  (f.exists()) f.delete();

        FileOutputStream fOut = null;

        try  {

            fOut = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            fOut.flush();

            fOut.close();

        } catch (IOException e) {

            return  null;

        }

        return  f.getAbsolutePath();

    }
}
