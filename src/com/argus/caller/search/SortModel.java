package com.argus.caller.search;

import android.graphics.Bitmap;
import android.widget.QuickContactBadge;

public class SortModel extends Contact {


	public SortModel(String name, String number,Bitmap quickContactBadge, String sortKey) {
		super(name, number,quickContactBadge, sortKey);
	}

	public String sortLetters; //��ʾ����ƴ��������ĸ

	public SortToken sortToken=new SortToken();
}
