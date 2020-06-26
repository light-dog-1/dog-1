package com.lightdog1.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.lightdog1.mobilesafe.R;
import com.lightdog1.mobilesafe.db.domain.AppInfo;
import com.lightdog1.mobilesafe.engine.AppInfoProvider;
import com.lightdog1.mobilesafe.utils.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements OnClickListener{

	private TextView tv_mSize,tv_SDSize,tv_des;
	private List<AppInfo> mS_appifliat,mc_appiflist;
	private ListView lv_applist;
	private MyAdapter mAdapter;
	private AppInfo mAppinfo;
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			mAdapter = new MyAdapter();
			lv_applist.setAdapter(mAdapter);
			if(tv_des!=null&&mc_appiflist!=null){
				tv_des.setText("用户应用(" + mc_appiflist.size() + ")");
			}
		}

	};
	class MyAdapter extends BaseAdapter {

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}


		@Override
		public int getCount() {
			return mS_appifliat.size() + mc_appiflist.size() + 2;
		}

		@Override
		public AppInfo getItem(int p1) {
			if (p1 == 0 || p1 == mc_appiflist.size() + 1) {
				return null;
			} else if (p1 > mc_appiflist.size() + 1) {
				return mS_appifliat.get(p1 - mc_appiflist.size() - 1);
			} else {
				return mc_appiflist.get(p1 - 1);
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mc_appiflist.size() + 1) {
				return 0;
			} else {
				return 1;
			}

		}


		@Override
		public long getItemId(int p1) {
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3) {
			int type=getItemViewType(p1);
			if (type == 1) {
				ViewHolder hodler=null;
				if (p2 == null) {
					p2 = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					hodler = new ViewHolder();
					hodler.iv_icon = p2.findViewById(R.id.appitem_iv_icon);
					hodler.tv_name = p2.findViewById(R.id.appitemtv_name);
					hodler.tv_path = p2.findViewById(R.id.appitem_tv_path);
					p2.setTag(hodler);
				} else {
					hodler = (AppManagerActivity.ViewHolder) p2.getTag();
				}
				hodler.iv_icon.setBackground(getItem(p1).getIcon());
				hodler.tv_name.setText(getItem(p1).getName());
				hodler.tv_path.setText(getItem(p1).isSystem() ?"系统应用": "手机应用");

				return p2;
			} else {
				View view=View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
				TextView tv_title=view.findViewById(R.id.appitemtitle_tv_title);
				tv_title.setText((p1 == 0) ?"手机应用(" + mc_appiflist.size() + ")": "系统应用(" + mS_appifliat.size() + ")");
				return view;
			}
		}
	}
	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name,tv_path;
	}
	static class ViewTitleHolder {
		TextView tv_title;
	}

	@Override
	protected void onResume() {
		getData();
		super.onResume();
	}

	private void getData() {
		new Thread(new Runnable(){

				@Override
				public void run() {
					List<AppInfo> appListInfo=AppInfoProvider.getAppInfoList(getApplicationContext());
					for (AppInfo appinfo:appListInfo) {
						if (appinfo.isSystem()) {
							mS_appifliat.add(appinfo);
						} else {
							mc_appiflist.add(appinfo);
						}
					}
					mHandler.sendEmptyMessage(0);
				}
			}).start();
	}
	

	@Override
	public void onClick(View p1) {
	    switch(p1.getId()){
			case R.id.popupwindow_tv_uninstall:
				if(mAppinfo.isSystem())
				{
					ToastUtil.show(getApplicationContext(),"此应用不能卸载");
				}else
				{
					Intent intent=new Intent("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"+mAppinfo.getPackageName()));
					startActivity(intent);
				}
				break;
			case R.id.popupwindow_tv_start:
				//通过桌面启动应用
				PackageManager pm=getPackageManager();
				Intent it=pm.getLaunchIntentForPackage(mAppinfo.getPackageName());
				if(it!=null)
				{
					startActivity(it);
				}else{
					ToastUtil.show(getApplicationContext(),"无法启动");
				}
				break;
			case R.id.popupwindow_tv_share:
				//分享(第三方(新浪,微信,qq)),智慧北京
				Intent intent=new Intent();
				intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用,应用名称为"+mAppinfo.getName());
				intent.setType("text/plain");
				startActivity(intent);
				
				break;
		}
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
		initTitle(); 

		initAppList();
    }

	private void initTitle() {
		String memoryPath=Environment.getDataDirectory().getAbsolutePath();
		String SDPath=Environment.getExternalStorageDirectory().getAbsolutePath();
		String mSize=Formatter.formatFileSize(this, getAvailableSize(memoryPath));
		String sSize=Formatter.formatFileSize(this, getAvailableSize(SDPath));

		tv_mSize = findViewById(R.id.appmanager_tv_mavail);
		tv_SDSize = findViewById(R.id.appmanager_tv_sdavail);

		tv_mSize.setText(mSize);
		tv_SDSize.setText(sSize);
	}

	private long getAvailableSize(String path) {
		StatFs statFs=new StatFs(path);
		long blocks=statFs.getAvailableBlocks();
		long bsize=statFs.getBlockSize();

		return blocks * bsize;
	}

	private void initAppList() {
		lv_applist = findViewById(R.id.appmanager_lv);
		tv_des = findViewById(R.id.appmanager_tv_des);

		mS_appifliat = new ArrayList<>();
		mc_appiflist = new ArrayList<>();
		
		lv_applist.setOnScrollListener(new OnScrollListener(){

				@Override
				public void onScrollStateChanged(AbsListView p1, int p2) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (mc_appiflist != null && mS_appifliat != null) {
						if (firstVisibleItem >= mc_appiflist.size() + 1) {
							tv_des.setText("系统应用(" + mS_appifliat.size() + ")");
						} else {
							tv_des.setText("手机应用(" + mc_appiflist.size() + ")");
						}
					}
				}
			});
		lv_applist.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
					if (pos == 0 || pos == mc_appiflist.size() + 1) {
						return;
					} else if (pos > mc_appiflist.size() + 1) {
						mAppinfo= mS_appifliat.get(pos - mc_appiflist.size() - 1);
					} else {
						mAppinfo= mc_appiflist.get(pos - 1);
					}
					showPopupWindow(view);
				}
			});
	}
	public void showPopupWindow(View v)
	{
		View view=View.inflate(this,R.layout.popupwindow_layout,null);
		TextView tv_uninstall=view.findViewById(R.id.popupwindow_tv_uninstall);
		TextView tv_start=view.findViewById(R.id.popupwindow_tv_start);
		TextView tv_share=view.findViewById(R.id.popupwindow_tv_share);
		
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		
		//透明动画
		AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
		alphaAnimation.setDuration(500);
		//保留动画最终位置
		alphaAnimation.setFillAfter(true);
		
		//
		ScaleAnimation scaleAnmation=new ScaleAnimation(
		0,1,
		0,1,
		Animation.RELATIVE_TO_SELF,0.5f,
		Animation.RELATIVE_TO_SELF,0.5f
		);
		scaleAnmation.setDuration(500);
		scaleAnmation.setFillAfter(true);
		
		//动画集合
		AnimationSet animationSet=new AnimationSet(true);
		animationSet.addAnimation(scaleAnmation);
		animationSet.addAnimation(alphaAnimation);
		
		PopupWindow popupWindow= new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		//设置透明北京(低版本需要设置北京才能响应返回键)
		//popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.showAsDropDown(v,60,-(v.getHeight()+75));
		
		view.startAnimation(animationSet);
		
	}

}
