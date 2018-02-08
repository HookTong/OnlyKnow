package com.onlyknow.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OKBase64Util {
    private final static String TAG = "OKBase64Util";

    /**
     * Bitmap转Base64字符数据
     */
    public static String BitmapToBase64(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 读取和压缩IMAG , 100为不压缩 , 并将其压缩结果保存在ByteArrayOutputStream对象中
        photo.compress(CompressFormat.JPEG, 100, baos);
        // 对压缩后的字节进行base64编码
        String imageBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        try {
            baos.close();
            return imageBase64;
        } catch (IOException e) {
            Log.e("IMAGT2String", "图片转Base64失败");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析Base64字符数据 , 返回Bitmap
     */
    public static Bitmap Base64ToBitmap(String baseStr) {
        // 下面代码从XML文件中读取以保存的图像，并将其显示在IMAG控件中
        // 读取Base64格式的图像数据
        // 对Base64格式的字符串进行解码，还原成字节数组
        byte[] imageBytes = Base64.decode(baseStr.getBytes(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        if (bitmap != null) {
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 解析Base64字符数据 , 返回Drawable
     */
    public static Drawable Base64ToDrawable(String baseStr) {
        // 下面代码从XML文件中读取以保存的图像，并将其显示在IMAG控件中
        // 读取Base64格式的图像数据
        // 对Base64格式的字符串进行解码，还原成字节数组
        try {
            byte[] imageBytes = Base64.decode(baseStr.getBytes(), Base64.DEFAULT);

            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            Drawable drawable = Drawable.createFromStream(bais, "BIAOTI");
            bais.close();
            return drawable;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片的解析比率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 解析图片
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String FilePath, int reqWidth, int reqHeight) {
        try {
            if (new File(FilePath).exists()) {
                // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                // 解析图片
                BitmapFactory.decodeFile(FilePath, options);
                // 调用上面定义的方法计算inSampleSize值
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                // 使用获取到的inSampleSize值再次解析图片
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(FilePath, options);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将Drawable转换成Bitmap图片
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 转换图片成圆形 传入Bitmap对象
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 保存Bitmap对象到本地图片文件
     */
    public static void saveBitmap(Bitmap bm, String Dir, String picName) {
        OKLogUtil.print(TAG, "保存图片");
        File f = new File(Dir, picName);
        if (f.exists()) {
            f.delete();
        }
        if (!f.exists()) {
            try {
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                // 压缩图片 , 100为不压缩
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                OKLogUtil.print(TAG, "已经保存");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
