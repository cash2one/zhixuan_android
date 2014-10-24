package com.zhixuan.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhixuan.R;
import com.zhixuan.utils.UpdateManager;

public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_about, container, false);

		TextView versionTextView = (TextView) view
				.findViewById(R.id.tv_version_name);
		versionTextView.setText("当前版本:"
				+ new UpdateManager(getActivity()).getVersionName());

		// 关于我们
		TextView aboutUsTextView = (TextView) view
				.findViewById(R.id.tv_about_us);
		aboutUsTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AboutUsActivity.class);
				startActivity(intent);
			}
		});

		// 联系我们
		TextView contactUsTextView = (TextView) view
				.findViewById(R.id.tv_contact_us);
		contactUsTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ContactUsActivity.class);
				startActivity(intent);
			}
		});

		// 检查更新
		TextView checkUpdateTextView = (TextView) view
				.findViewById(R.id.tv_check_update);
		checkUpdateTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateManager up = new UpdateManager(getActivity());
				up.checkUpdate(true);
			}
		});

		// 投票
		TextView voteTextView = (TextView) view.findViewById(R.id.tv_vote);
		voteTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		return view;
	}
}
