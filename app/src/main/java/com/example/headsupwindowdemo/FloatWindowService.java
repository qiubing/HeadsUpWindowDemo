package com.example.headsupwindowdemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Description:
 * Author: qiubing
 * Date: 2017-07-27 20:20
 */
public class FloatWindowService extends Service {

    private Handler mHandler = new Handler();

    // 定时器，定时检测当前应该创建还是移除浮窗
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        // 开启定时器，每隔0.5s刷新一次
        if (timer == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, i, i1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 服务终止时，也停止定时器继续运行
        timer.cancel();
        timer = null;
    }

    private class RefreshTask extends TimerTask{

        @Override
        public void run() {
            // 1.当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗
            if (isHome() && !MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext());
                    }
                });
            // 2.当前界面不是桌面，并且有悬浮窗显示，则移除悬浮窗
            }else if (!isHome() && MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.removeSmallWindow(getApplicationContext());
                        MyWindowManager.removeBigWindow(getApplicationContext());
                    }
                });

            // 3.当前界面是桌面，且有悬浮窗显示，则更新内存数据
            }else if (isHome() && MyWindowManager.isWindowShowing()){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
        }
    }

    // 判断当前是否在桌面
    private boolean isHome(){
        boolean isLauncherForeground = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<String> launchers = getLaunchers();
        List<ActivityManager.RunningTaskInfo> rti = manager.getRunningTasks(1);
        if (launchers.contains(rti.get(0).topActivity.getPackageName())){
            isLauncherForeground = true;
        }
        return isLauncherForeground;
    }

    // 获取属于桌面应用的包名列表
    private List<String> getLaunchers(){
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : infos){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null){
                names.add(activityInfo.packageName);
            }
        }
        return names;
    }
}
