package com.fmx.list.string2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;


import java.util.ArrayList;

/**
 * Created by Dpuntu on 2017/3/8.
 */
public class BitmapUtil {
    private final static int WIDTH = 384;
    private final static float SMALL_TEXT = 23;
    private final static float LARGE_TEXT = 35;
    private final static int START_RIGHT = WIDTH;
    private final static int START_LEFT = 0;
    private final static int START_CENTER = WIDTH / 2;

    /**
     * 特殊需求：
     */
    public final static int IS_LARGE = 10;
    public final static int IS_SMALL = 11;
    public final static int IS_RIGHT = 100;
    public final static int IS_LEFT = 101;
    public final static int IS_CENTER = 102;


    private static float x = START_LEFT, y;

    /**
     * 生成图片
     */
    public static Bitmap StringListtoBitmap(Context context, ArrayList<StringBitmapParameter> AllString) {
        if (AllString.size() <= 0) return Bitmap.createBitmap(WIDTH, WIDTH / 4, Bitmap.Config.RGB_565);
        ArrayList<StringBitmapParameter> mBreakString = new ArrayList<>();

        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setTextSize(SMALL_TEXT);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/songti.TTF");// 仿宋打不出汉字
        Typeface font = Typeface.create(typeface, Typeface.NORMAL);
        paint.setTypeface(font);

        for (StringBitmapParameter mParameter : AllString) {
            int ALineLength = paint.breakText(mParameter.getText(), true, WIDTH, null);//检测一行多少字
            int lenght = mParameter.getText().length();
            if (ALineLength < lenght) {

                int num = lenght / ALineLength;
                String ALineString = new String();
                String RemainString = new String();

                for (int j = 0; j < num; j++) {
                    ALineString = mParameter.getText().substring(j * ALineLength, (j + 1) * ALineLength);
                    mBreakString.add(new StringBitmapParameter(ALineString, mParameter.getIsRightOrLeft(), mParameter.getIsSmallOrLarge()));
                }

                RemainString = mParameter.getText().substring(num * ALineLength, mParameter.getText().length());
                mBreakString.add(new StringBitmapParameter(RemainString, mParameter.getIsRightOrLeft(), mParameter.getIsSmallOrLarge()));
            } else {
                mBreakString.add(mParameter);
            }
        }


        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int FontHeight = (int) Math.abs(fontMetrics.leading) + (int) Math.abs(fontMetrics.ascent) + (int) Math.abs(fontMetrics.descent);
        y = (int) Math.abs(fontMetrics.leading) + (int) Math.abs(fontMetrics.ascent);

        int bNum = 0;
        for (StringBitmapParameter mParameter : mBreakString) {
            String bStr = mParameter.getText();
            if (bStr.isEmpty() | bStr.contains("\n") | mParameter.getIsSmallOrLarge() == IS_LARGE)
                bNum++;
        }
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, FontHeight * (mBreakString.size() + bNum), Bitmap.Config.RGB_565);

        drawWhiteBackground(bitmap);

        Canvas canvas = new Canvas(bitmap);

        for (StringBitmapParameter mParameter : mBreakString) {

            String str = mParameter.getText();

            if (mParameter.getIsSmallOrLarge() == IS_SMALL) {
                paint.setTextSize(SMALL_TEXT);

            } else if (mParameter.getIsSmallOrLarge() == IS_LARGE) {
                paint.setTextSize(LARGE_TEXT);
            }

            if (mParameter.getIsRightOrLeft() == IS_RIGHT) {
                x = WIDTH - paint.measureText(str);
            } else if (mParameter.getIsRightOrLeft() == IS_LEFT) {
                x = START_LEFT;
            } else if (mParameter.getIsRightOrLeft() == IS_CENTER) {
                x = (WIDTH - paint.measureText(str)) / 2.0f;
            }

            if (str.isEmpty() | str.contains("\n") | mParameter.getIsSmallOrLarge() == IS_LARGE) {
                canvas.drawText(str, x, y + FontHeight / 2, paint);
                y = y + FontHeight;
            } else {
                canvas.drawText(str, x, y, paint);
            }
            y = y + FontHeight;
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 合并图片
     */
    public static Bitmap addBitmapInHead(Bitmap first, Bitmap second) {
        int width = Math.max(first.getWidth(), second.getWidth());
        int startWidth = (width - first.getWidth()) / 2;
        int height = first.getHeight() + second.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        drawWhiteBackground(result);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, startWidth, 0, null);
        canvas.drawBitmap(second, 0, first.getHeight(), null);
        return result;
    }

    /***
     * 使用两个方法的原因是：
     * logo标志需要居中显示，如果直接使用同一个方法是可以显示的，但是不会居中
     */
    public static Bitmap addBitmapInFoot(Bitmap bitmap, Bitmap image) {
        int width = Math.max(bitmap.getWidth(), image.getWidth());
        int startWidth = (width - image.getWidth()) / 2;
        int height = bitmap.getHeight() + image.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        drawWhiteBackground(result);
        
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(image, startWidth, bitmap.getHeight(), null);
        return result;
    }

    private static void drawWhiteBackground(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = width * height;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < length; i++) {
            pixels[i] = Color.rgb(255, 255, 255);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    }

}
