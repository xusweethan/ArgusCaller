package com.argus.caller.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.argus.caller.R;

public class ContactsSortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> mList;
	private List<SortModel> mSelectedList;
	private Context mContext;
	LayoutInflater mInflater;

	public ContactsSortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		mSelectedList = new ArrayList<SortModel>();
		if (list == null) {
			this.mList = new ArrayList<SortModel>();
		} else {
			this.mList = list;
		}
	}

	/**
	 * ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		if (list == null) {
			this.mList = new ArrayList<SortModel>();
		} else {
			this.mList = list;
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = mList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvNumber = (TextView) view.findViewById(R.id.number);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		//����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);


		viewHolder.tvTitle.setText(this.mList.get(position).name);
		viewHolder.tvNumber.setText(this.mList.get(position).number);
		///////////////////////////
		/*viewHolder.quickContactBadge.assignContactUri(Contacts.getLookupUri(mContent.hashCode(),null ));
		if(0==mContent.hashCode()){
			viewHolder.quickContactBadge.setImageResource(R.drawable.touxiang);
		}else{
			Uri uri=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,mContent.hashCode());
			InputStream input=ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), uri);
			Bitmap contactPhoto=BitmapFactory.decodeStream(input);
			viewHolder.quickContactBadge.setImageBitmap(contactPhoto); 
		}*/
		return view;

	}

	public static class ViewHolder {
		public TextView tvLetter;
		public TextView tvTitle;
		public TextView tvNumber;
		///////////////////////
		//public ImageView tvVoice;
		//public TextView tvWenjian;
		//public CheckBox cbChecked;
		public QuickContactBadge quickContactBadge;
	}

	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	public int getSectionForPosition(int position) {
		return mList.get(position).sortLetters.charAt(0);
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).sortLetters;
			char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}


	@Override
	public Object[] getSections() {
		return null;
	}

}