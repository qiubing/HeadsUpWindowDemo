package com.example.headsupwindowdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-07-27 21:18
 */
public class MyWindowManager {
    //С������View��ʵ��
    private static FloatWindowSmallView mSmallWindow;

    //��������View��ʵ��
    private static FloatWindowBigView mBigWindow;

    // С������View�Ĳ���
    private static WindowManager.LayoutParams mSmallWindowParams;

    // ��������View�Ĳ���
    private static WindowManager.LayoutParams mBigWindowParams;

    // ���ڿ�������Ļ����ӻ��Ƴ�������
    private static WindowManager mWindowManager;

    // ���ڻ�ȡ�ֻ������ڴ�
    private static ActivityManager mActivityManager;

    // ����һ��С����������ʼλ��Ϊ��Ļ���Ҳ��м�λ��
    public static void createSmallWindow(Context context){
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (mSmallWindow == null){
            mSmallWindow = new FloatWindowSmallView(context);
            if (mSmallWindowParams == null){
                mSmallWindowParams = new WindowManager.LayoutParams();
                mSmallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mSmallWindowParams.format = PixelFormat.RGBA_8888;
                mSmallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                mSmallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                mSmallWindowParams.width = FloatWindowSmallView.viewWith;
                mSmallWindowParams.height = FloatWindowSmallView.viewHeight;
                mSmallWindowParams.x = screenWidth;
                mSmallWindowParams.y = screenHeight / 2;
            }
            mSmallWindow.setParams(mSmallWindowParams);
            windowManager.addView(mSmallWindow,mSmallWindowParams);
        }
    }

    //��С����������Ļ���Ƴ�
    public static void removeSmallWindow(Context context){
        if (mSmallWindow != null){
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mSmallWindow);
            mSmallWindow = null;
        }
    }

    // ����һ������������λ��Ϊ��Ļ���м�
    public static void createBigWindow(Context context){
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (mBigWindow == null){
            mBigWindow = new FloatWindowBigView(context);
            if (mBigWindowParams == null){
                mBigWindowParams = new WindowManager.LayoutParams();
                mBigWindowParams.x = screenWidth / 2 - FloatWindowBigView.viewWith / 2;
                mBigWindowParams.y = screenHeight / 2 - FloatWindowBigView.viewHeight / 2;
                mBigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mBigWindowParams.format = PixelFormat.RGBA_8888;
                mBigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                mBigWindowParams.width = FloatWindowBigView.viewWith;
                mBigWindowParams.height = FloatWindowBigView.viewHeight;

            }
            windowManager.addView(mBigWindow,mBigWindowParams);
        }
    }

    //��������������Ļ���Ƴ�
   public static void removeBigWindow(Context context){
       if (mBigWindow != null){
           WindowManager windowManager = getWindowManager(context);
           windowManager.removeView(mBigWindow);
           mBigWindow = null;
       }
   }

    //����С��������TextView�ϵ����ݣ���ʾ�ڴ�ʹ�õİٷֱ�
    public static void updateUsedPercent(Context context){
        if (mSmallWindow != null){
            TextView percentView = (TextView)mSmallWindow.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValue(context));
        }
    }

    public static boolean isWindowShowing(){
        return mSmallWindow != null || mBigWindow != null;
    }

    // ������ʹ���ڴ�İٷֱȣ�������
    public static String getUsedPercentValue(Context context){
        String dir = "/proc/meminfo";
        try {
            FileReader fileReader = new FileReader(dir);
            BufferedReader br = new BufferedReader(fileReader,2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+",""));
            long availableSize = getAvailableMemory(context)/1024;
            int percent = (int)((totalMemorySize - availableSize)/(float)totalMemorySize * 100);
            return percent + "%";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "������";
    }

    // ��ȡ��ǰ�����ڴ棬�����������ֽ�Ϊ��λ
    private static long getAvailableMemory(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    private static ActivityManager getActivityManager(Context context){
        if (mActivityManager == null){
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null){
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
