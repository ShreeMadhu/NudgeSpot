package com.mad.nudgespot.restservice;

import org.json.JSONObject;

public interface ServiceDelegate {

	public void onDataReceived(JSONObject dataReceived);
	public void onDataError(JSONObject dataReceived);
}
