package com.argus.caller.bean;


//ͨ����¼ʵ����
public class CallLogBean {
	private int id;
	private String name; // ����
	private String number; // ����
	private String date; // ����
	private int type; // ����:1������:2,δ��:3
	private String time;
	//private int count; // ͨ������
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	////////////////////////////////////////////
	//ͷ������
	private int contactId;
	private Long photoId;
	private String lookUpKey;
	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public Long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}
	public String getLookUpKey() {
		return lookUpKey;
	}
	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	} 
	
//////////////////////////////////////////////////////	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/*public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}*/
	
}
