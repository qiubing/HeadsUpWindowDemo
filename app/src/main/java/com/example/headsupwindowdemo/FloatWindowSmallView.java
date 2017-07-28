package com.example.headsupwindowdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-07-27 20:42
 */
public class FloatWindowSmallView extends LinearLayout {

    // С�������Ŀ��
    public static int viewWith;

    // С�������ĸ߶�
    public static int viewHeight;

    // ��¼ϵͳ״̬���ĸ߶�
    private static int statusBarHeight;

    private WindowManager mWindowManager;

    // С�����Ĳ���
    private WindowManager.LayoutParams mParams;

    // ��¼��ǰ��ָ����Ļ�ϵ�x����ֵ
    private float xInScreen;

    // ��¼��ǰ��ָ����Ļ�ϵ�y����ֵ
    private float yInScreen;

    // ��¼��ָ����ʱ����Ļ�ϵ�x����ֵ
    private float xDownInScreen;

    // ��¼��ָ����ʱ����Ļ�ϵ�y����ֵ
    private float yDownInScreen;

    // ��¼����ʱ��С������View�ϵ�x����ֵ
    private float xInView;

    // ��¼����ʱ��С������View�ϵ�y����ֵ
    private float yInView;


    public FloatWindowSmallView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small,this);
        View view  = findViewById(R.id.small_float_window_layout);
        viewHeight = view.getLayoutParams().height;
        viewWith = view.getLayoutParams().width;
        TextView percentView = (TextView)findViewById(R.id.percent);
        percentView.setText(MyWindowManager.getUsedPercentValue(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            // ��ָ����ʱ��¼��Ҫ����,�������ֵ����Ҫ��ȥ״̬���߶�
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();

                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();

                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                // ��ָ�ƶ���ʱ�����С������λ��
                updateViewPosition();

                break;
            case MotionEvent.ACTION_UP:
                // �����ָ�뿪��Ļʱ��xDownInScreen��xInScreen��ȣ���yDownInScreen��yInScreen��ȣ�����Ϊ�����˵����¼���
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen){
                    openBigWindow();
                }
                break;
            default:
                break;
        }
        return true;
    }

    //��С�������Ĳ������룬���ڸ���С��������λ��
    public void setParams(WindowManager.LayoutParams params){
        this.mParams = params;
    }

    //����С����������Ļ�е�λ��
    private void updateViewPosition(){
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        mWindowManager.updateViewLayout(this,mParams);
    }

    private void openBigWindow(){
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    private int  getStatusBarHeight(){
        if (statusBarHeight == 0){
            try {
                Class<?> cls = Class.forName("com.android.internal.R$dime");
                Object object = cls.newInstance();
                Field field = cls.getField("status_bar_height");
                int x = (Integer) field.get(object);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
