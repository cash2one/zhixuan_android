package com.zhixuan.utils;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhixuan.R;
import com.zhixuan.ui.AboutFragment;

public class UpdateManager {
	private Context mContext;
	private RequestQueue mQueue;
	private final String CHECK_UPDATE_URL = Consts.MAIN_DOMAIN
			+ "/static/app/android.json";
	private final String SAVE_APK_NAME = "zhixuan.apk";
	private String apkUrl;
	private boolean interceptFlag = false;

	private int progress = 0;
	private boolean forceNotice = false;
	private ProgressBar mProgress;
	private ZXSharedPreferences mZxSharedPreferences;

	public UpdateManager(Context context) {
		this.mContext = context;
		this.mQueue = Volley.newRequestQueue(context);
		this.mZxSharedPreferences = new ZXSharedPreferences(mContext);
		this.forceNotice = false;
	}

	public int getVersionCode() {
		int versonCode = 0;

		try {
			versonCode = mContext.getPackageManager().getPackageInfo(
					"com.zhixuan", 0).versionCode;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return versonCode;
	}

	public String getVersionName() {
		String versonName = "1.0.0";

		try {
			versonName = mContext.getPackageManager().getPackageInfo(
					"com.zhixuan", 0).versionName;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return versonName;
	}

	private void downloadApk() {
		Thread downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	public String getRomInfo() {
		// 此处为SDCard的存储空间
		File file = Environment.getDataDirectory();
		StatFs stat = new StatFs(file.getPath());
		long availableBlocks = stat.getAvailableBlocks();
		long blockCount = stat.getBlockCount();
		long blockSize = stat.getBlockSize();
		long freeBlocks = stat.getFreeBlocks();
		Formatter formatter = new Formatter();
		String total = formatter.formatFileSize(mContext, blockSize
				* blockCount);
		String free = formatter
				.formatFileSize(mContext, blockSize * freeBlocks);
		return "ROM总空间：" + (total) + " 可用空间：" + (free);
	}

	public String getSdInfo() {
		// 此处为data/data路径下的存储空间
		File file = Environment.getExternalStorageDirectory();
		// 得到路径下的存储空间的状态，根据此状态得到各种属性
		StatFs stat = new StatFs(file.getPath());
		long availableBlocks = stat.getAvailableBlocks();
		long blockCount = stat.getBlockCount();
		long blockSize = stat.getBlockSize();
		long freeBlocks = stat.getFreeBlocks();
		Formatter formatter = new Formatter();
		// 格式化，将数字转化为存储空间的字节大小
		String total = formatter.formatFileSize(mContext, blockSize
				* blockCount);
		String free = formatter
				.formatFileSize(mContext, blockSize * freeBlocks);
		return "SD总空间：" + (total) + " 可用空间：" + (free);
	}

	private void installApk() {
		File apkfile = new File(Environment.getExternalStorageDirectory(),
				SAVE_APK_NAME);
		if (!apkfile.exists()) {
			return;
		}
		// Log.d("---------------------->>>>", apkfile.exists()+"");
		// Log.d("---------------------->>>>", getRomInfo()+"");
		// Log.d("---------------------->>>>", getSdInfo()+"");

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkfile),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mProgress.setProgress(progress);
				break;
			case 2:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	/*
	 * 显示下载对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		// builder.setTitle("软件版本更新");

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});

		builder.create().show();

		// 下载apk
		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File apkFile = new File(
						Environment.getExternalStorageDirectory(),
						SAVE_APK_NAME);
				FileOutputStream fos = new FileOutputStream(apkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(1);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(2);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/*
	 * 显示提示是否更新对话框
	 */
	private void showNoticeDialog(String versonName, String desc) {
		AlertDialog.Builder builder = new Builder(mContext);
		// builder.setTitle("软件版本更新");
		builder.setMessage("发现新版本啦 (" + versonName + ")\n\n" + desc);
		builder.setPositiveButton("欢乐升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void showNoNeedUpdateDialog() {
		
		if(!forceNotice){
			return;
		}
		
		AlertDialog.Builder builder = new Builder(mContext);
		
		builder.setMessage("当前版本已经是最新版本，无需升级!");
		builder.setPositiveButton("好滴", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/*
	 * 今天是否已经提醒过了
	 */
	private boolean needNotice() {
		boolean flag = true;

		String strDate = mZxSharedPreferences.getLastCheckUpdate();
		Date today = strToDate(getStringDateShort());
		Date lastDate = strToDate(strDate);

		if (today.getTime() - lastDate.getTime() <= 0) {
			flag = false;
		}

		return flag;
	}

	private String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	private Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public void checkUpdate(boolean force) {
		StringRequest stringRequest = new StringRequest(CHECK_UPDATE_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject obj = new JSONObject(response);

							int oldVersionCode = getVersionCode();
							int newVersionCode = obj.getInt("index");

							String des = new String(obj.getString("des")
									.getBytes("iso-8859-1"), "utf-8");
							String version = obj.getString("version");
							apkUrl = obj.getString("url");

							// 需要更新
							if (newVersionCode > oldVersionCode) {
								showNoticeDialog(version, des);
							} else {
								showNoNeedUpdateDialog();
							}

							mZxSharedPreferences
									.setLastCheckUpdate(getStringDateShort());
						} catch (JSONException e) {

						} catch (UnsupportedEncodingException e) {

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});

		forceNotice = force;
		if (force) {
			mQueue.add(stringRequest);
		} else {
			if (needNotice()) {
				mQueue.add(stringRequest);
			}
		}

	}
}
