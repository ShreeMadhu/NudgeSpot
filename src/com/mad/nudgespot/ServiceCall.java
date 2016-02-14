package com.mad.nudgespot;

import java.util.TreeMap;

import android.content.Context;
import android.os.Handler;

import com.mad.nudgespot.restservice.ServiceWrapper;
import com.mad.nudgespot.restservice.ServiceWrapper.ServiceRequest;
import com.mad.nudgespot.restservice.ServiceWrapper.ServiceRequestMethod;
import com.mad.nudgespot.restservice.ServiceWrapper.ServiceRequestProtocol;

public class ServiceCall {

	public static void fetchContent(Handler serviceHandler, String url,
			Context context, String dataNeededBack, String inputData) {
		ServiceWrapper wrapper = new ServiceWrapper(serviceHandler, context);

		ServiceRequest request = wrapper.new ServiceRequest(url,
				ServiceRequestMethod.POST, ServiceRequestProtocol.REST, null,
				null, null, inputData);
		TreeMap<String, Object> callBackData = new TreeMap<String, Object>();
		callBackData.put(Constants.DATA_NEED_BACK, dataNeededBack);
		request.setCallBackData(callBackData);
		wrapper.fetchData(request);
	}

	public static void fetchUsingGet(Handler serviceHandler, String url,
			Context context, String dataNeededBack) {
		ServiceWrapper wrapper = new ServiceWrapper(serviceHandler, context);

		ServiceRequest request = wrapper.new ServiceRequest(url,
				ServiceRequestMethod.GET, ServiceRequestProtocol.REST, null,
				null, null, null);
		TreeMap<String, Object> callBackData = new TreeMap<String, Object>();
		callBackData.put(Constants.DATA_NEED_BACK, dataNeededBack);
		request.setCallBackData(callBackData);
		wrapper.fetchData(request);
	}

}
