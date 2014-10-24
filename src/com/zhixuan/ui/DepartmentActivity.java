package com.zhixuan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.zhixuan.R;
import com.zhixuan.utils.BitmapCache;

public class DepartmentActivity extends Activity {

	private RequestQueue mQueue;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());

		String departmentId = getIntent().getStringExtra("departmentId");
		String departmentName = getIntent().getStringExtra("departmentName");
		String departmentCMCount = getIntent().getStringExtra(
				"departmentCMCount");
		String departmentImageUrl = getIntent().getStringExtra(
				"departmentImageUrl");
		String departmentTel = getIntent().getStringExtra("departmentTel");
		String departmentDes = getIntent().getStringExtra("departmentDes");
		String departmentCompanyName = getIntent().getStringExtra(
				"departmentCompanyName");
		String departmentAddr = getIntent().getStringExtra("departmentAddr");

		this.setTitle("Ӫҵ������");

		setContentView(R.layout.activity_department);

		// ���ÿؼ�
		ImageView image = (ImageView) findViewById(R.id.departmentImage);
		ImageListener listener = ImageLoader.getImageListener(image,
				R.drawable.toutiao_3, R.drawable.toutiao_3);
		imageLoader.get(departmentImageUrl, listener);

		TextView name = (TextView) findViewById(R.id.departmentName);
		name.setText(departmentName);

		TextView companyName = (TextView) findViewById(R.id.departmentCompanyName);
		companyName.setText("������˾     " + departmentCompanyName);

		TextView tel = (TextView) findViewById(R.id.departmentTel);
		tel.setText("��ϵ�绰     " + departmentTel);

		TextView addr = (TextView) findViewById(R.id.departmentAddr);
		addr.setText("��ϵ��ַ     " + departmentAddr);

		TextView des = (TextView) findViewById(R.id.departmentDes);
		des.setText(Html.fromHtml(departmentDes));

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		this.finish();
		return super.onOptionsItemSelected(item);
	}
}
