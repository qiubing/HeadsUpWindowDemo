package com.example.headsupwindowdemo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-07-27 21:20
 */
public class FloatWindowBigView extends LinearLayout {

    // ��¼���������Ŀ��
    public static int viewWith;

    // ��¼���������Ŀ��
    public static int viewHeight;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_float_window_layout);
        viewHeight = view.getLayoutParams().height;
        viewWith = view.getLayoutParams().width;

        Button close = (Button) findViewById(R.id.close);
        Button back = (Button) findViewById(R.id.back);

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // ����ر���������ʱ���Ƴ���������������ֹͣService
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(),FloatWindowService.class);
                context.stopService(intent);
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // ������ص�ʱ���Ƴ���������������С������
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }
}
