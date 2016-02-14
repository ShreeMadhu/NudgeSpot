package com.mad.nudgespot;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FindActivityResult extends AppCompatActivity {

	private Context context;
	private TextView idTv;
	private TextView nameTv;
	private TextView emailTv;

	private Button close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_result);
		context = this;

		CustomToolBar.applyCustomToolBar(this);
		CustomToolBar.removeLeftImage();
		CustomToolBar.removeRightImage();
		CustomToolBar.setTitle("User Details");

		String result = getIntent().getStringExtra(Constants.FIND_RESULT);
		String id = "N/A";
		String name = "N/a";
		String email = "N/a";

		idTv = (TextView) findViewById(R.id.id);
		nameTv = (TextView) findViewById(R.id.findName);
		emailTv = (TextView) findViewById(R.id.findEmail);

		try {
			JSONObject resultObj = new JSONObject(result);
			id = resultObj.getString(Constants.FIND_RESULT_ID);
			name = resultObj.getString(Constants.FIND_RESULT_NAME);
			email = resultObj.getString(Constants.FIND_RESULT_EMAIL);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		idTv.setText(id);
		nameTv.setText(name);
		emailTv.setText(email);

		close = (Button) findViewById(R.id.close);

		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

}
