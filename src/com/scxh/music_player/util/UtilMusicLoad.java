package com.scxh.music_player.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UtilMusicLoad {

	/**
	 * 扫描制定单个文件,将文件数据添加到系统的媒体数据库
	 * 
	 * @param ctx 引用上下文
	 * @param file 指定文件,注意不能是目录
	 */
	public static void scanFileAsync(Context ctx, File file) {
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(file));
		ctx.sendBroadcast(scanIntent);
	}
	public static void scanDirAsync(Context ctx,  File file) {
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED,
				Uri.fromFile(file));
		ctx.sendBroadcast(scanIntent);
	}
	
	/**
	 * 遍历指定目录下的所有目录及文件
	 * @param rootDir
	 */
	public static void showFile(Context ctx,File rootDir) {
		File[] list = rootDir.listFiles();
		if(list != null) {
			for(File file:list) {
				if(file.isDirectory()){
//					Log.i("dir", file.getAbsolutePath());
					File fileDir = new File(file.getAbsolutePath());
				
				}else{
					//加入过滤条件
					if(file.getAbsolutePath().endsWith(".mp3")){
						scanFileAsync(ctx, file);
					}
				}
			}
		}
	}
}
