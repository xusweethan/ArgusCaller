package com.argus.caller.activity;

import android.content.Context;
import android.os.AsyncTask;

public class DbTask extends AsyncTask<Integer, Integer, String>{  
    //����������ڷֱ��ǲ��������������߳���Ϣʱ�䣩������(publishProgress�õ�)������ֵ ����  
    
	private Context context;  
	public DbTask(Context context) {  
        this.context = context;  
    } 
	
    @Override  
    protected void onPreExecute() {  
        //��һ��ִ�з���  
        super.onPreExecute();  
    }  
      
    @Override  
    protected String doInBackground(Integer... params) {
    	try {
    		phonelistener.getInstance().queryContacts(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "dbok";  
    }  

    @Override  
    protected void onProgressUpdate(Integer... progress) {  
        //���������doInBackground����publishProgressʱ��������Ȼ����ʱֻ��һ������  
        //��������ȡ������һ������,����Ҫ��progesss[0]��ȡֵ  
        //��n����������progress[n]��ȡֵ  
        super.onProgressUpdate(progress);  
    }  

    @Override  
    protected void onPostExecute(String result) {  
        //doInBackground����ʱ���������仰˵������doInBackgroundִ����󴥷�  
        //�����result��������doInBackgroundִ�к�ķ���ֵ������������"ִ�����"  
        super.onPostExecute(result);  
    }  
      
}  

