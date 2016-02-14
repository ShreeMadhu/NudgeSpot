package com.mad.nudgespot;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mad.nudgespot.restservice.ServiceDelegate;
import com.mad.nudgespot.restservice.ServiceHandler;

public class FindActivity extends AppCompatActivity implements ServiceDelegate {

	private Context context;

	private EditText searchBox;
	private Button find;

	private CustomProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find);

		context = this;

		CustomToolBar.applyCustomToolBar(this);
		CustomToolBar.removeLeftImage();
		CustomToolBar.removeRightImage();
		CustomToolBar.setTitle("Find User");

		searchBox = (EditText) findViewById(R.id.searchEmail);
		find = (Button) findViewById(R.id.find);

		find.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = searchBox.getText().toString().trim();
				if (!email.isEmpty()) {
					if (Constants.emailValidation(email)) {
						dialog = new CustomProgressDialog(context);
						dialog.showDialog();
						ServiceCall.fetchUsingGet(new ServiceHandler(
								FindActivity.this), Constants.SERVICE_FIND_URL
								+ "uid=" + email, context, null);
					}
				}
			}
		});
	}

	@Override
	public void onDataReceived(JSONObject dataReceived) {
		Log.d("place", "data: " + dataReceived);
		String result = null;
		int responseCode = -1;
		try {
			result = dataReceived.getString("response");
			responseCode = dataReceived.getInt("responseCode");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Constants.callDismissDialog(dialog);
		if (responseCode != 404 && responseCode != -1) {
			Intent intent = new Intent(context, FindActivityResult.class);
			intent.putExtra(Constants.FIND_RESULT, result);
			startActivity(intent);
			finish();
		} else {
			Constants.showToast(context, "Oops, user does not exist!!");
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
