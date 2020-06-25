package com.light.mobileSafe.activity;

import android.app.Activity;
import android.os.Bundle;
import com.light.mobileSafe.R;
import android.widget.Button;
import android.widget.ListView;
import android.os.Handler;
import android.os.Message;
import com.light.mobileSafe.db.dao.BlackNumberDao;
import java.util.List;
import com.light.mobileSafe.db.domain.BlackNumberInfo;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.widget.Magnifier.Builder;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.AbsListView;

public class BlackNumberActivity extends Activity {

    private Button btn_add;
    private ListView lv_numbers;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mNumberList;
    private BlackNumberActivity.MyNewAdapter myAdapter;
    private int mode=1;
    private boolean mIsLoad=false;
    private int mCount=0;

    private Handler handler=new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(myAdapter==null){
            myAdapter = new MyNewAdapter();
            lv_numbers.setAdapter(myAdapter);
            }else
            {
                myAdapter.notifyDataSetChanged();
            }
        }
    };
    //优化adapter
    private class MyNewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mNumberList.size();
        }

        @Override
        public Object getItem(int p1) {
            return null;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }
        public class ViewHolder {
            TextView tv_phone,tv_mode;
            ImageView iv_delect;
        }

        @Override
        public View getView(final int position, View p2, ViewGroup p3) {
            ViewHolder holder=null;
            if (p2 == null) {
                p2 = View.inflate(getApplicationContext(), R.layout.view_items_bn, null);

                holder = new ViewHolder();

                holder. tv_phone = p2.findViewById(R.id.viewitemsbn_tv_phone);
                holder. tv_mode = p2.findViewById(R.id.viewitemsbn_tv_mode);
                holder. iv_delect = p2.findViewById(R.id.viewitemsbn_delect);
                p2.setTag(holder);

            } else {
                holder = (BlackNumberActivity.MyNewAdapter.ViewHolder) p2.getTag();
            }

            final String phone = mNumberList.get(position).getPhone();
            holder. tv_phone.setText(phone);
            int mode_code=Integer.parseInt(mNumberList.get(position).getMode());
            String mode=null;
            switch (mode_code) {
                case 1:
                    mode = "拦截短信";
                    break;
                case 2:
                    mode = "拦截电话";
                    break;
                case 3:
                    mode = "拦截所有";
                    break;
            }
            holder. tv_mode.setText(mode);

            holder. iv_delect.setOnClickListener(new OnClickListener(){

                    @Override
                    public void onClick(View p1) {
                        mDao.delect(phone);
                        mNumberList.remove(position);
                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });

            return p2;
        }
    }
    private class MyAdapter extends BaseAdapter {
        private TextView tv_phone,tv_mode;
        private ImageView iv_delect;

        @Override
        public int getCount() {
            return mNumberList.size();
        }

        @Override
        public Object getItem(int p1) {
            return null;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }

        @Override
        public View getView(final int position, View p2, ViewGroup p3) {
            View view=null;
            if (p2 == null) {
                view = View.inflate(getApplicationContext(), R.layout.view_items_bn, null);
                tv_phone = view.findViewById(R.id.viewitemsbn_tv_phone);
                tv_mode = view.findViewById(R.id.viewitemsbn_tv_mode);
                iv_delect = view.findViewById(R.id.viewitemsbn_delect);

            } else {
                view = p2;
            }

            final String phone = mNumberList.get(position).getPhone();
            tv_phone.setText(phone);
            int mode_code=Integer.parseInt(mNumberList.get(position).getMode());
            String mode=null;
            switch (mode_code) {
                case 1:
                    mode = "拦截短信";
                    break;
                case 2:
                    mode = "拦截电话";
                    break;
                case 3:
                    mode = "拦截所有";
                    break;
            }
            tv_mode.setText(mode);

            iv_delect.setOnClickListener(new OnClickListener(){

                    @Override
                    public void onClick(View p1) {
                        mDao.delect(phone);
                        mNumberList.remove(position);
                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);
        initUI();
        initData();
    }

    private void initUI() {
        btn_add = findViewById(R.id.activityblacknumber_add);
        lv_numbers = findViewById(R.id.activityblacknumber_lv);

        btn_add.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1) {
                    showDialog();
                }
            });
        lv_numbers.setOnScrollListener(new AbsListView.OnScrollListener(){

                @Override
                public void onScrollStateChanged(AbsListView p1, int state) {
                    final int listcount=mNumberList.size();
                    if (mNumberList != null) {
                        if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_numbers.getLastVisiblePosition() >= listcount - 1
                            && !mIsLoad) {
                            if(mCount>listcount){
                            new Thread(new Runnable(){

                                    @Override
                                    public void run() {
                                        mDao = BlackNumberDao.newInstance(getApplicationContext());
                                        mNumberList.addAll( mDao.find(listcount));
                                        handler.sendEmptyMessage(0);
                                    }
                                }).start();
                                }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView p1, int p2, int p3, int p4) {
                }
            });
    }

    private void initData() {
        new Thread(new Runnable(){

                @Override
                public void run() {
                    mDao = BlackNumberDao.newInstance(getApplicationContext());
                    mNumberList = mDao.find(0);
                    mCount=mDao.getCount();
                    handler.sendEmptyMessage(0);
                }
            }).start();
    }
    private void showDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(BlackNumberActivity.this);
        final AlertDialog dialog= builder.create();
        View view=View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText et_phone=view.findViewById(R.id.dialogaddblacknumber_et_phone);
        RadioGroup rg_group=view.findViewById(R.id.dialog_add_rg);
        Button btn_submit=view.findViewById(R.id.dialog_add_submit);
        Button btn_cancal=view.findViewById(R.id.dialog_add_cancal);
        rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(RadioGroup p1, int p2) {

                    switch (p2) {
                        case R.id.dialog_add_rb_sms:
                            mode = 1;
                            break;
                        case R.id.dialog_add_rb_phone:
                            mode = 2;
                            break;
                        case R.id.dialog_add_rb_all:
                            mode = 3;
                            break;
                    }
                }
            });
        btn_submit.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1) {
                    String phone=et_phone.getText().toString();
                    if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    } else {
                        mDao.insert(phone, mode + "");
                        BlackNumberInfo bninfo=new BlackNumberInfo();
                        bninfo.setPhone(phone);
                        bninfo.setMode(mode + "");
                        mNumberList.add(0, bninfo);

                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                }
            });
        btn_cancal.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1) {
                    dialog.dismiss();
                }
            });
        dialog.show();
    }
    // create table blacknumber (_id integer primary key autoincrement,phone varchar(20),mode varchar(5));
}
