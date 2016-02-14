package com.mad.nudgespot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

	private Context context;

	private Button goToIdentify;
	private Button goToFind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);

		context = this;

		CustomToolBar.applyCustomToolBar(this);
		CustomToolBar.removeLeftImage();
		CustomToolBar.removeRightImage();
		CustomToolBar.setTitle("Home");

		goToIdentify = (Button) findViewById(R.id.goToIdentify);
		goToFind = (Button) findViewById(R.id.goToFind);

		goToIdentify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, IdentifyActivity.class);
				startActivity(intent);
			}
		});
		goToFind.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, FindActivity.class);
				startActivity(intent);
			}
		});
	}

}
