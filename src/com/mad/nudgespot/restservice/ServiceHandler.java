package com.mad.nudgespot.restservice;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

public class ServiceHandler extends Handler {
	private ServiceDelegate activity;

	public ServiceHandler(ServiceDelegate activity) {
		this.activity = activity;
	}

	public void handleMessage(Message msg) {
		if (msg.obj != null) {
			JSONObject json = null;
			String rs = msg.obj.toString();
			System.out.println("JSON Response == " + rs);
			try {
				json = new JSONObject(rs);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (msg.what == ServiceConstants.SERVICE_DATA_SUCCESS_HANDLER_CODE) {
				activity.onDataReceived(json);
			} else if (msg.what == ServiceConstants.SERVICE_ERROR_HANDLER_CODE) {
				activity.onDataError(json);
			}
		}
	}
}
