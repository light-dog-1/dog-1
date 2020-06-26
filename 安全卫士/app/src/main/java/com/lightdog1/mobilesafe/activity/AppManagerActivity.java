package com.lightdog1.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.TextView;
import com.lightdog1.mobilesafe.R;
import com.lightdog1.mobilesafe.db.domain.AppInfo;
import java.util.List;
import com.lightdog1.mobilesafe.engine.AppInfoProvider;
import android.widget.ListView;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;

public class AppManagerActivity extends Activity {

	private TextView tv_mSize,tv_SDSize,tv_des;
	private List<AppInfo> mS_appifliat,mc_appiflist;
	private ListView lv_applist;
	private MyAdapter mAdapter;
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
	}

}
