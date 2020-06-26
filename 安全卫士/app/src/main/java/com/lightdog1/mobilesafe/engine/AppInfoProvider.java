package com.lightdog1.mobilesafe.engine;
import android.content.Context;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.PackageInfo;
import com.lightdog1.mobilesafe.db.domain.AppInfo;
import android.content.pm.ApplicationInfo;

public class AppInfoProvider {
    
    public static void getAppInfoList(Context ctx)
	{
		PackageManager pManager=ctx.getPackageManager();
		List<PackageInfo> pList=pManager.getInstalledPackages(0);
		for(PackageInfo pi:pList)
		{
			AppInfo appinfo=new AppInfo();
			appinfo.setPackageName(pi.packageName);
			ApplicationInfo applicationInfo=pi.applicationInfo;
			appinfo.setName(applicationInfo.loadLabel(pManager).toString());
			appinfo.setIcon(applicationInfo.loadIcon(pManager));
			//判断是否为系统应用
			if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM)
			{
				//系统应用
				appinfo.setIsSystem(true);
			}else{
				appinfo.setIsSystem(false);
			}
			//判断是否安装在sd卡
			if((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE)
			{
				//安装在sd卡
				appinfo.setIsSDcard(true);
			}else{
				appinfo.setIsSDcard(false);
			}
			
		}

	}

}
