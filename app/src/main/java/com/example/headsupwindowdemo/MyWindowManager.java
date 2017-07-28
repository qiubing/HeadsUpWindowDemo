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
    //小悬浮窗View的实例
    private static FloatWindowSmallView mSmallWindow;

    //大悬浮窗View的实例
    private static FloatWindowBigView mBigWindow;

    // 小悬浮窗View的参数
    private static WindowManager.LayoutParams mSmallWindowParams;

    // 大悬浮窗View的参数
    private static WindowManager.LayoutParams mBigWindowParams;

    // 用于控制在屏幕上添加或移除悬浮窗
    private static WindowManager mWindowManager;

    // 用于获取手机可用内存
    private static ActivityManager mActivityManager;

    // 创建一个小悬浮窗。初始位置为屏幕的右部中间位置
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

    //将小悬浮窗从屏幕上移除
    public static void removeSmallWindow(Context context){
        if (mSmallWindow != null){
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mSmallWindow);
            mSmallWindow = null;
        }
    }

    // 创建一个大悬浮窗。位置为屏幕正中间
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

    //将大悬浮窗从屏幕上移除
   public static void removeBigWindow(Context context){
       if (mBigWindow != null){
           WindowManager windowManager = getWindowManager(context);
           windowManager.removeView(mBigWindow);
           mBigWindow = null;
       }
   }

    //更新小悬浮窗的TextView上的数据，显示内存使用的百分比
    public static void updateUsedPercent(Context context){
        if (mSmallWindow != null){
            TextView percentView = (TextView)mSmallWindow.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValue(context));
        }
    }

    public static boolean isWindowShowing(){
        return mSmallWindow != null || mBigWindow != null;
    }

    // 计算已使用内存的百分比，并返回
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
        return "悬浮窗";
    }

    // 获取当前可用内存，返回数据以字节为单位
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
