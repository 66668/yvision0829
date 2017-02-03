package com.yvision.utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
 
public class IntentUtil {
 
	public static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon().encodedAuthority(
				"com.android.htmlfileprovider").scheme("content").encodedPath(
				param).build();

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.setDataAndType(uri, "text/html");

		return intent;

	}
	
	//打开浏览器
	public static Intent getBrowserIntent(String url) {

		Uri uri = Uri.parse(url);

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.setData(uri);

		return intent;

	}

	// android��ȡһ�����ڴ�ͼƬ�ļ���intent

	public static Intent getImageFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "image/*");

		return intent;

	}

	// android��ȡһ�����ڴ�PDF�ļ���intent

	public static Intent getPdfFileIntent(String param)
	{
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;

	}

	// android��ȡһ�����ڴ��ı��ļ���intent

	public static Intent getTextFileIntent(String param, boolean paramBoolean)
	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri1 = Uri.parse(param);
		intent.setDataAndType(uri1, "text/plain");
		return intent;
	}

	public static Intent getMediaFileIntent(String type, Uri uri )
  {
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("oneshot", 0);
    intent.putExtra("configchange", 0);
    intent.setDataAndType(uri, type);
    return intent;
  }
	
	// android��ȡһ�����ڴ���Ƶ�ļ���intent

	public static Intent getAudioFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "audio/*");

		return intent;

	}

	// android��ȡһ�����ڴ���Ƶ�ļ���intent

	public static Intent getVideoFileIntent(String param)

	{
		if(param.toLowerCase().endsWith(".mp3")){
			return  getAudioFileIntent(param);
		 
		}else{
		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "video/*");

		return intent;
		}

	}

	// android��ȡһ�����ڴ�CHM�ļ���intent

	public static Intent getChmFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "application/x-chm");

		return intent;

	}

	// android��ȡһ�����ڴ�Word�ļ���intent

	public static Intent getWordFileIntent(String param)
	{
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android��ȡһ�����ڴ�Excel�ļ���intent

	public static Intent getExcelFileIntent(String param)
	{
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android��ȡһ�����ڴ�PPT�ļ���intent

	public static Intent getPptFileIntent(String param) 
	{
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

		return intent;

	}

	//��ȡһ�����ڴ򿪵绰���Ź��ܵ�intent
	public  static Intent getTelIntent(String telNum) {
		Uri telUri = Uri.parse("tel:"+telNum);
		Intent intent= new Intent(Intent.ACTION_DIAL, telUri);
		return intent;
	}
}