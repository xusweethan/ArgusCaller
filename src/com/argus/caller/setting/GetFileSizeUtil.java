package com.argus.caller.setting;

import java.io.File;
import java.text.DecimalFormat;
import java.io.FileInputStream;

public class GetFileSizeUtil {

	private static GetFileSizeUtil instance;

	public GetFileSizeUtil() {
	}

	public static GetFileSizeUtil getInstance() {
		if (instance == null) {
			instance = new GetFileSizeUtil();
		}
		return instance;
	}

	/*** ��ȡ�ļ���С ***/
	public long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("�ļ�������");
		}
		return s;
	}

	/*** ��ȡ�ļ��д�С ***/
	public long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/*** ת���ļ���С��λ(b/kb/mb/gb) ***/
	public String FormetFileSize(long fileS) {// ת���ļ���С
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/*** ��ȡ�ļ����� ***/
	public long getlist(File f) {// �ݹ���ȡĿ¼�ļ�����
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}
}
