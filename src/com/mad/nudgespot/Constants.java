package com.mad.nudgespot;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Constants {

	public static final String DATA_NEED_BACK = "dataNeedBack";

	public static final String SUBSCRIBER = "subscriber";
	public static final String SUBSCRIBER_UID = "uid";
	public static final String SUBSCRIBER_NAME = "name";

	public static final String SUBSCRIBER_PROPERTIES = "properties";
	public static final String SUBSCRIBER_PROPERTIES_GENDER = "gender";
	public static final String SUBSCRIBER_PROPERTIES_AGE = "age";

	public static final String FIND_RESULT = "findResult";
	public static final String FIND_RESULT_ID = "id";
	public static final String FIND_RESULT_NAME = "name";
	public static final String FIND_RESULT_EMAIL = "uid";

	public static final String SERVICE_IDENTIFY_URL = "http://phoenix.nudgespot.com/201507/subscribers/identify";
	public static final String SERVICE_FIND_URL = "http://phoenix.nudgespot.com/201507/subscribers?";

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static boolean emailValidation(String email) {
		boolean emailValid = false;
		if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			emailValid = true;
		}
		return emailValid;
	}

	public static void callDismissDialog(CustomProgressDialog dialog) {
		if (dialog != null) {
			dialog.dismissDialog();
		}
	}

}
