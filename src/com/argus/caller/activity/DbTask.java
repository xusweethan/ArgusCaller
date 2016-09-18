package com.argus.caller.activity;

import android.content.Context;
import android.os.AsyncTask;

public class DbTask extends AsyncTask<Integer, Integer, String>{  
    //后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型  
    
	private Context context;  
	public DbTask(Context context) {  
        this.context = context;  
    } 
	
    @Override  
    protected void onPreExecute() {  
        //第一个执行方法  
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
        //这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数  
        //但是这里取到的是一个数组,所以要用progesss[0]来取值  
        //第n个参数就用progress[n]来取值  
        super.onProgressUpdate(progress);  
    }  

    @Override  
    protected void onPostExecute(String result) {  
        //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发  
        //这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"  
        super.onPostExecute(result);  
    }  
      
}  

