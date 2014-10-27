package com.zhixuan.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhixuan.R;

public class LoadingDialog {
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// �õ�����view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// ���ز���
        // main.xml�е�ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loadingImg);
        TextView tipTextView = (TextView) v.findViewById(R.id.loadingTips);// ��ʾ����
        // ���ض���
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.animator.loading_animation);
        // ʹ��ImageView��ʾ����
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// ���ü�����Ϣ

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// �����Զ�����ʽdialog

        loadingDialog.setCancelable(true);// �������á����ؼ���ȡ��
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// ���ò���
        return loadingDialog;
    }
}
