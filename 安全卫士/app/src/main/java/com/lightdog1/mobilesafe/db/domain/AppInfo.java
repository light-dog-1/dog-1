package com.lightdog1.mobilesafe.db.domain;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name;
    private String packageName;
	private Drawable icon;
	private boolean isSystem;
	private boolean isSDcard;

	public void setIsSDcard(boolean isSDcard) {
		this.isSDcard = isSDcard;
	}

	public boolean isSDcard() {
		return isSDcard;
	}

	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}
    


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}}
