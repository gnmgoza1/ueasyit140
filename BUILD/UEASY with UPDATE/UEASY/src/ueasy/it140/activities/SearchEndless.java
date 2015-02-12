package ueasy.it140.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ueasy.it140.R;
import ueasy.it140.database.Database;
import ueasy.it140.endlessapi.EndlessAdapter;
import ueasy.it140.endlessapi.EndlessListView;
import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SearchEndless extends Activity {
	private final static int ITEM_PER_REQUEST = 10;
	Database DB;
	List<String> amenities;
	// List view
	private ListView lv;

	// Listview Adapter
	ArrayAdapter<String> adapter;

	// Search EditText
	EditText inputSearch;

	// ArrayList for Listview
	ArrayList<HashMap<String, String>> productList;
	EndlessListView elv;
	private SimpleCursorAdapter dataAdapter = null;
	int mult = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		ActionBar ab = getActionBar();
		ab.setTitle(Html.fromHtml("<font color='#ffffff'>SEARCH </font>"));
		ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#048abf")));

		DB = new Database(this);

		// Listview Data
		amenities = new ArrayList<String>();

		amenities = DB.getAllAmenityName();
		elv = (EndlessListView) findViewById(R.id.endless_list_view);

		EndlessAdapter adp = new EndlessAdapter(this, createItems(mult),
				R.layout.search_item);
		elv.setLoadingView(R.layout.loading_layout);
		elv.setAdapter(adp);

		// lv.setListener(this);
		// lv = (ListView) findViewById(R.id.list_view);
		// inputSearch = (EditText) findViewById(R.id.inputSearch);
		//
		// // Adding items to listview
		// adapter = new ArrayAdapter<String>(this, R.layout.search_item,
		// R.id.amenity_name, amenities);
		//
		// Cursor cursor1 = DB.getAllAmenityNameCursor();
		// if (cursor1 != null) {
		// cursor1.moveToFirst();
		// }
		// String[] columnNames = { "amenity_name" };
		// int[] resIds = { R.id.amenity_name };
		// dataAdapter = new SimpleCursorAdapter(this, R.layout.search_item,
		// cursor1, columnNames, resIds);
		// // Assign adapter to ListView
		// lv.setAdapter(dataAdapter);
		// lv.setVisibility(View.INVISIBLE);
		//
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> listView, View view,
		// int position, long id) {
		// // Get the cursor, positioned to the corresponding row in the
		// // result set
		// Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		// // Get the state's capital from this row in the database.
		// String item = cursor.getString(cursor
		// .getColumnIndexOrThrow("amenity_name"));
		// Intent i = new Intent(getBaseContext(), AmenityBuilding.class);
		// i.putExtra("AmenityName", item);
		// startActivity(i);
		//
		// }
		// });

		inputSearch.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 3) {
					dataAdapter.getFilter().filter(s.toString());
					lv.setVisibility(View.VISIBLE);
				}
				if (s.length() < 3) {
					lv.setVisibility(View.INVISIBLE);
				}
			}
		});

		dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				return DB.getAmenityNameCursor(constraint.toString());
			}
		});

	}

	private class FakeNetLoader extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return createItems(mult);
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			elv.addNewData(result);
		}

	}

	private List<String> createItems(int mult) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < ITEM_PER_REQUEST; i++) {
			result.add("Item " + (i * mult));
		}

		return result;
	}

	public void loadData() {
		System.out.println("Load data");
		mult += 10;
		// We load more data here
		FakeNetLoader fl = new FakeNetLoader();
		fl.execute(new String[] {});

	}

}