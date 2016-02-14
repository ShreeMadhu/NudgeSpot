package com.mad.nudgespot;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mad.nudgespot.restservice.ServiceDelegate;
import com.mad.nudgespot.restservice.ServiceHandler;

public class IdentifyActivity extends AppCompatActivity implements
		ServiceDelegate {

	private EditText fullNameET;
	private EditText emailET;
	private EditText ageET;

	private CheckBox maleCheck;
	private CheckBox femaleCheck;

	private Button identify;

	private CustomProgressDialog dialog;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify);

		context = this;

		CustomToolBar.applyCustomToolBar(this);
		CustomToolBar.removeLeftImage();
		CustomToolBar.removeRightImage();
		CustomToolBar.setTitle("Identify User");

		fullNameET = (EditText) findViewById(R.id.name);
		emailET = (EditText) findViewById(R.id.email);
		ageET = (EditText) findViewById(R.id.age);

		maleCheck = (CheckBox) findViewById(R.id.maleBox);
		femaleCheck = (CheckBox) findViewById(R.id.femaleBox);

		maleCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (femaleCheck.isChecked()) {
					femaleCheck.setChecked(false);
					maleCheck.setChecked(true);
				}
			}
		});

		femaleCheck.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maleCheck.isChecked()) {
					maleCheck.setChecked(false);
					femaleCheck.setChecked(true);
				}
			}
		});

		identify = (Button) findViewById(R.id.identify);
		identify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Constants.isNetworkAvailable(context)) {
					validateCredentials();
				} else {
					Constants.showToast(context, "Network not available");
				}
			}
		});
	}

	public boolean validateCredentials() {
		boolean isValid = false;

		String fullName = fullNameET.getText().toString().trim();
		String email = emailET.getText().toString().trim();
		String age = ageET.getText().toString().trim();

		if (!emptyValidation(fullName, email, age)) {
			if (Constants.emailValidation(email)) {
				String gender = checkForGender();
				if (gender != null) {

					dialog = new CustomProgressDialog(context);
					dialog.showDialog();
					ServiceCall.fetchContent(new ServiceHandler(this),
							Constants.SERVICE_IDENTIFY_URL, this, null,
							generateRequest(fullName, email, age, gender)
									.toString());
				} else {
					Constants.showToast(this, "Kindly choose your gender");
				}
			} else {
				Constants
						.showToast(this, "Kindly provide valid E-Mail address");
			}
		} else {
			Constants.showToast(this, "Kindly provide all credentials");
		}

		return isValid;
	}

	public JSONObject generateRequest(String name, String email, String age,
			String gender) {
		JSONObject requestObj = new JSONObject();

		try {
			JSONObject subscriberObj = new JSONObject();
			subscriberObj.put(Constants.SUBSCRIBER_UID, email);
			subscriberObj.put(Constants.SUBSCRIBER_NAME, name);

			JSONObject propertiesObj = new JSONObject();
			propertiesObj.put(Constants.SUBSCRIBER_PROPERTIES_GENDER, gender);
			propertiesObj.put(Constants.SUBSCRIBER_PROPERTIES_AGE, age);

			subscriberObj.put(Constants.SUBSCRIBER_PROPERTIES, propertiesObj);
			requestObj.put(Constants.SUBSCRIBER, subscriberObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("place", "data request: " + requestObj);
		return requestObj;
	}

	public String checkForGender() {
		String gender = null;
		if (maleCheck.isChecked()) {
			gender = "male";
		} else if (femaleCheck.isChecked()) {
			gender = "female";
		}
		return gender;
	}

	public boolean emptyValidation(String name, String email, String age) {
		boolean credentialsEmpty = false;
		if (name.isEmpty() || email.isEmpty() || age.isEmpty()) {
			credentialsEmpty = true;
		}
		return credentialsEmpty;
	}

	@Override
	public void onDataReceived(JSONObject dataReceived) {
		Log.d("place", "data: " + dataReceived);
		Constants.callDismissDialog(dialog);
		try {
			JSONObject result = new JSONObject(
					dataReceived.getString("response"));
			String id = result.getString("id");
			Constants.showToast(context, "Hurray!! Your identification id is "
					+ id);
			clearAllCredentials();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void clearAllCredentials() {
		fullNameET.setText("");
		emailET.setText("");
		ageET.setText("");

		if (maleCheck.isChecked()) {
			maleCheck.setChecked(false);
		} else if (femaleCheck.isChecked()) {
			femaleCheck.setChecked(false);
		}
	}

	@Override
	public void onDataError(JSONObject dataReceived) {
		Log.d("place", "data: " + dataReceived);
		Constants.callDismissDialog(dialog);
		Constants.showToast(context,
				"Oops, an error occured, please try again later");
	}

}
