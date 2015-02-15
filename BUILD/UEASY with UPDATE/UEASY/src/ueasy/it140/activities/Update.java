package ueasy.it140.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ueasy.it140.R;
import ueasy.it140.database.Database;
import ueasy.it140.modals.ErrorModal;
import ueasy.it140.modals.Success;
import ueasy.it140.modals.UpdateFail;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Update extends ActionBarActivity {
	// DB Class to perform DB related operations
	Database controller = new Database(this);
	// Progress Dialog Object
	ProgressDialog prgDialog;
	HashMap<String, String> queryValues;
	
	//Room Utility Variable
	int ru_id; 		int cr_id; 
	String ru_mon; 	String ru_tue; 
	String ru_wed; 	String ru_thu; 
	String ru_fri; 	String ru_sat; 
	String ru_sun;
	
	//Campus Variables
	int campus_id;			String c_name;
	String c_addr;	 	String c_desc;
	
	//Amenities Variables
	String a_catName;	int a_id; 
	int bldg_id;		int a_bLevel;
	String a_name; 		String a_desc; 
	String a_pic; 		double a_la; 
	double a_longi;
	
	//BuildingLevel Variables
	int bl_id; 		int bl_bID; 
	int bl_bNum;
	
	//DatabaseVersion Variables
	int db_version;		int db_id;
	
	//Faqs Variables
	int f_id;	 String f_ques; 
	String f_ans;
	
	//About Variables
	int ab_id;			String ab_name; 
	String ab_version; 	String ab_desc; 
	String ab_email; 	String ab_footer;
	String ab_notes;	String ab_year;

			
	String type;
	
	
	ActionBar ab;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		// Get User records from SQLite DB
		ArrayList<HashMap<String, String>> crList = controller.getAllClassroom();
		// If users exists in SQLite DB
		//Toast.makeText(getApplicationContext(), "crList: "+crList.size(), Toast.LENGTH_LONG).show();
		ab = getActionBar();
		ab.hide();
		
		// Initialize Progress Dialog properties
		prgDialog = new ProgressDialog(this);
		prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prgDialog.setMessage("Content updating!");
		prgDialog.setIndeterminate(true);
		// BroadCase Receiver Intent Object
//		Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
		// Pending Intent Object
		syncSQLiteMySQLDB();
	
	}
	
	// Options Menu (ActionBar Menu)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// When Options Menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. 
		
		return super.onOptionsItemSelected(item);
	}
	
	// Method to Sync MySQL to SQLite DB
	public void syncSQLiteMySQLDB() {
		// Create AsycHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		// Http Request Params Object
		RequestParams params = new RequestParams();
		// Show ProgressBar
		prgDialog.show();
		// Make Http call to getusers.php
		client.post("http://192.168.56.1/ueasy/getClassroom.php", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String response) {
					// Hide ProgressBar
					
					// Update SQLite DB with response sent by getusers.php
					updateSQLite(response);
					prgDialog.hide();
				}
				// When error occured
				@Override
				public void onFailure(int statusCode, Throwable error, String content) {
					// TODO Auto-generated method stub
					// Hide ProgressBar
					prgDialog.hide();
					
					UpdateFail uf = new UpdateFail();
					uf.show(getFragmentManager(), "Fail");
					
				}
		});
	}
	
	public void updateSQLite(String response){
		
		long result = 0;
		
		
		ArrayList<HashMap<String, String>> amenitysynclist;
		amenitysynclist = new ArrayList<HashMap<String, String>>();
		
		// Create GSON object
		Gson gson = new GsonBuilder().create();
		try {
			// Extract JSON array from the response
			JSONArray arr = new JSONArray(response);
			System.out.println(arr.length());
			//Toast.makeText(getApplicationContext(), "arr lenght:  "+ arr.length() , Toast.LENGTH_SHORT).show();
			
			
			// If no of array elements is not zero
			if(arr.length() != 0){
				// Loop through each array element, get JSON object which has userid and username
				int jumpTime=0;
				for (int i = 0; i < arr.length(); i++) {
					
						jumpTime += (100/arr.length());
						prgDialog.setProgress(jumpTime);
					// Get JSON object
					JSONObject obj = (JSONObject) arr.get(i);
					
					//Getting Common Data from the Json
								
					obj.get("type");
					
					type = obj.getString("type").toString();
					
					if(type.equals("amenity"))
					{
						//Getting Amenity Data from Json
						obj.get("a_catName");		obj.get("a_id");
						obj.get("a_bLevel");		obj.get("a_name");
						obj.get("a_desc");			obj.get("a_pic");
						obj.get("a_latitude");		obj.get("a_longitude");
						obj.get("b_id");			obj.get("c_id");
					}
					
					else if(type.equals("utility")) //Getting Room Utility Data from Json
					{
						obj.get("ru_id");			obj.get("cr_id");
						obj.get("ru_monday");		obj.get("ru_tuesday");
						obj.get("ru_wednesday");	obj.get("ru_thursday");
						obj.get("ru_friday");		obj.get("ru_saturday");
						obj.get("ru_sunday");
					}
					
					else if(type.equals("faqs")) //Getting FAQS Data from Json
					{
						obj.get("f_id");			obj.get("f_ques");
						obj.get("f_ans");		
					}
					
					else if(type.equals("about")) //Getting FAQS Data from Json
					{
						obj.get("ab_id");			obj.get("ab_version");
						obj.get("ab_name");			obj.get("ab_email");
						obj.get("ab_desc");			obj.get("ab_footer");
						obj.get("ab_year");			obj.get("ab_notes");
					}
					
					else if(type.equals("databaseVersion")) //Getting Campus Data from Json
					{
						obj.get("db_id");	obj.get("db_version");
						
					}
					
					else if(type.equals("campus")) //Getting Campus Data from Json
					{
						obj.get("c_name");	obj.get("c_addr");
						obj.get("c_desc");	obj.get("c_id");
					}
					
					else
					{
						//Getting Building Level Data from Json
						obj.get("bl_id"); 		obj.get("bldg_levelNum");
						obj.get("bldg_id");			
					}
					
					
					
					// DB QueryValues Object to insert into SQLite
					queryValues = new HashMap<String, String>();
			
							
					if(type.equals("amenity"))
					{
						a_catName = obj.getString("a_catName").toString();
						a_id =Integer.parseInt( obj.getString("a_id").toString());
						a_bLevel  = Integer.parseInt(obj.getString("a_bLevel").toString());
						a_name = obj.getString("a_name").toString();
						a_desc = obj.getString("a_desc").toString();
						a_pic = obj.getString("a_pic").toString();
						a_la = Double.parseDouble(obj.getString("a_latitude").toString());
						a_longi = Double.parseDouble(obj.getString("a_longitude").toString());
						bldg_id = Integer.parseInt(obj.getString("b_id").toString());
						campus_id = Integer.parseInt(obj.getString("c_id").toString());
					}
					
					else if(type.equals("utility"))
					{
						ru_id = Integer.parseInt(obj.getString("ru_id").toString());
						ru_mon =  obj.getString("ru_monday").toString();
						ru_tue = obj.getString("ru_tuesday").toString();	 	
						ru_wed = obj.getString("ru_wednesday").toString();
						ru_thu = obj.getString("ru_thursday").toString();
						ru_fri = obj.getString("ru_friday").toString();	 	
						ru_sat = obj.getString("ru_saturday").toString();
						ru_sun = obj.getString("ru_sunday").toString();
						cr_id = Integer.parseInt(obj.getString("cr_id").toString());
						
					}
					
					else if(type.equals("about"))
					{
						ab_id = Integer.parseInt(obj.getString("ab_id").toString());
						ab_version =  obj.getString("ab_version").toString();
						ab_email = obj.getString("ab_email").toString();	 	
						ab_name = obj.getString("ab_name").toString();
						ab_desc = obj.getString("ab_desc").toString();
						ab_footer = obj.getString("ab_footer").toString();
						ab_notes = obj.getString("ab_notes").toString();
						ab_year = obj.getString("ab_year").toString();	
							
					}
					
					else if(type.equals("faqs"))
					{
						f_id = Integer.parseInt(obj.getString("f_id").toString());
						f_ans =  obj.getString("f_ans").toString();
						f_ques = obj.getString("f_ques").toString();	 	
						
					}
					
					
					else if(type.equals("databaseVersion"))
					{
						db_id = Integer.parseInt(obj.getString("db_id").toString());
						db_version =  Integer.parseInt( obj.getString("db_version").toString());
						
					}
					
					else if(type.equals("campus"))
					{
						c_name =  obj.getString("c_name").toString();
						c_addr = obj.getString("c_addr").toString();	 	
						c_desc = obj.getString("c_desc").toString();
						campus_id = Integer.parseInt(obj.getString("c_id").toString());
					}
					
					else 
					{
						bl_id =Integer.parseInt( obj.getString("bl_id").toString());
						bl_bID =Integer.parseInt( obj.getString("bldg_id").toString());
						bl_bNum =Integer.parseInt( obj.getString("bldg_levelNum").toString());
						
					}
					
	
						if(type.equals("amenity"))
						{
							result = controller.inserToAmenity(a_catName, a_id, campus_id, bldg_id, a_bLevel, a_name, a_desc, a_pic, a_la, a_longi);
						}
						
						else if(type.equals("utility"))
						{
							result = controller.inserToRoomUtility(ru_id, cr_id, ru_mon, ru_tue, ru_wed, ru_thu, ru_fri, ru_sat, ru_sun);
						}
						
						else if(type.equals("faqs"))
						{
							result = controller.inserToFaqs(f_id, f_ques, f_ans);
						}
						
						else if(type.equals("about"))
						{
							result = controller.inserToAbout(ab_id, ab_name, ab_version, ab_desc, ab_email, ab_footer, ab_year, ab_notes);
						}
						
						else if(type.equals("databaseVersion"))
						{
							result = controller.inserToDatabase(db_id, db_version);
						}
						
						else if(type.equals("campus"))
						{
							result = controller.inserToCampus(campus_id, c_name, c_addr, c_desc);
						}
						
						else
						{
							result = controller.inserToBLevel(bl_id, bl_bID, bl_bNum);
						}
						
		
				}
				// Reload the Main Activity
				reloadActivity();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block=
			UpdateFail uf = new UpdateFail();
			uf.show(getFragmentManager(), "Fail");
		}
	}
	
	
	
	// Reload MainActivity
	public void reloadActivity() {
		Success suc = new Success();
		suc.show(getFragmentManager(), "Success");
		
	}
}
