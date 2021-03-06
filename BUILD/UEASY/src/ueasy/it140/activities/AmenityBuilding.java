package ueasy.it140.activities;

import org.json.JSONArray;

import ueasy.it140.R;
import ueasy.it140.database.Database;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AmenityBuilding extends Activity {

	Database DB;
	Bundle b;
	String amenityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.building_info);

		DB = new Database(this);
		b = getIntent().getExtras();
		DatabaseObject amenity = new DatabaseObject();
		amenity = DB.getAmenityInformation(b.getString("AmenityName"));
		amenityName = b.getString("AmenityName");

		ActionBar ab = getActionBar();
		ab.setTitle(Html.fromHtml("<font color='#ffffff'>"
				+ b.getString("AmenityName", "NULL") + " </font>"));
		ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#048abf")));
		ab.setDisplayHomeAsUpEnabled(true);

		if (amenity.get_type().contains("Building")) {
			setContentView(R.layout.building_info);
			TextView desc = (TextView) findViewById(R.id.BasicInfoCont);
			desc.setText(amenity.getInfo());
			TextView blevels = (TextView) findViewById(R.id.bldg_level_number);
			Toast.makeText(this, Integer.toString(amenity.get_blevels()),
					Toast.LENGTH_SHORT).show();
			blevels.setText(Integer.toString(amenity.get_blevels()));
		} else if (amenity.get_type().contains("Classroom")) {
			setContentView(R.layout.room_info);
			TextView desc = (TextView) findViewById(R.id.BasicInfoCont);
			desc.setText(amenity.getInfo());
		} else {
			setContentView(R.layout.amenity_main);
			TextView desc = (TextView) findViewById(R.id.BasicInfoCont);
			desc.setText(amenity.getInfo());
		}
		// DB.

	}

	public void selectLevel(View v) {
		Intent in = new Intent(this, LevelExpandable.class);
		in.putExtra("BuildingName", amenityName);
		startActivity(in);
	}

}
