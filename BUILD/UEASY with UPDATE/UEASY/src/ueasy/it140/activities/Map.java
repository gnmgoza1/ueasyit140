package ueasy.it140.activities;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import ueasy.it140.R;
import ueasy.it140.database.Database;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class Map extends Activity implements MapEventsReceiver, MapListener {
	ArrayList<Marker> anotherOverlayItemArray;
	Database DB;
	Bundle b;

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TITLE = "Limited scroll area";
	public static FixedMapView mapView;

	private static final BoundingBoxE6 sCentralParkBoundingBox;
	// private static final SafePaint sPaint;

	// ===========================================================
	// Fields
	// ===========================================================

	// private ShadeAreaOverlay mShadeAreaOverlay;

	static {
		// $$dE1235413$$eE1235452$$fN0102131$$gN0102103
		// final int west= 1235413;
		// final int north = 1235452;
		// final int east = 102131;
		// final int south = 102103;

		// North: 10.3600 -
		// East: 123.9025 +
		// South: 10.3500
		// West: 123.9160 -

		// North: 10.3593
		// west: 123.9032
		// East:123.9155
		// South: 10.3511
		// sCentralParkBoundingBox = new BoundingBoxE6(10.3590, 123.9070,
		// 10.3500,
		// 123.9100);
		sCentralParkBoundingBox = new BoundingBoxE6(10.3590, 123.9068, 10.3500,
				123.9122);

		// sPaint = new SafePaint();
		// sPaint.setColor(Color.argb(50, 255, 0, 0));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_map);
		// getIntent().setAction("Already created");
		// North: 10.3600 -
		// East: 123.9025 +
		// South: 10.3500
		// West: 123.9160 -
		// sCentralParkBoundingBox = new BoundingBoxE6(10.3590, 123.9070,
		// 10.3500,
		// 123.9100);
		// sCentralParkBoundingBox = new BoundingBoxE6(10.3590, 123.9070,
		// 10.3500,
		// 123.9100);
		// SpannableString s = new SpannableString("My Title");
		// s.setSpan(new TypefaceSpan(this, .otf"), 0, s.length(),
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getActionBar().setTitle("UEASY-TC");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#048abf")));

		mapView = new FixedMapView(this, 500);

		// constructor
		mapView.setClickable(true);
		// mShadeAreaOverlay = new ShadeAreaOverlay(this);
		// mapView.getOverlayManager().add(mShadeAreaOverlay);
		setContentView(mapView); // displaying the MapView

		// mRotationDetector = new RotationGestureDetector(this);

		mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		// mapView.
		// mapView.setMinZoomLevel(17);
		// mapView.getController().setZoom(17);

		setLimitScrolling(true);
		// mapView.setScrollableAreaLimit(sCentralParkBoundingBox);

		// mapView.getController().a

		mapView.setUseDataConnection(false);
		mapView.setMultiTouchControls(true);
		final Scroller mScroller = mapView.getScroller();
		mScroller.abortAnimation();
		// mScroller.
		MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
		DB = new Database(this);
		mapView.getController().setCenter(sCentralParkBoundingBox.getCenter());
		String table = "Building";
		String amenity = "";
		b = getIntent().getExtras();
		List<DatabaseObject> K = new ArrayList<DatabaseObject>();

		if (b != null) {
			if (b.containsKey("AmenityName")) {
				amenity = b.getString("AmenityName");
				K.add(DB.getAmenityInformation(amenity));
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mapView.getController().animateTo(
								new GeoPoint(b.getDouble("Latitude"), b
										.getDouble("Longitude")));
					}
				}, 100);

				// mapView.getController().setCenter(
				// new GeoPoint(b.getDouble("Latitude"), b
				// .getDouble("Longitude")));

			} else {
				table = b.getString("tableName", "Building");
				K = DB.getAllDatabaseObject(table);
				mapView.getController().animateTo(
						sCentralParkBoundingBox.getCenter());
			}

		} else {
			K = DB.getAllDatabaseObject(table);
			mapView.getController().animateTo(
					sCentralParkBoundingBox.getCenter());
		}

		Marker oi;
		// K = DB.getAllDatabaseObject(table);

		anotherOverlayItemArray = new ArrayList<Marker>();
		if (K != null) {
			for (DatabaseObject cn : K) {
				oi = new Marker(mapView);
				oi.setPosition(new GeoPoint(cn.getLat(), cn.getLong()));
				oi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
				oi.setIcon(getResources()
						.getDrawable(R.drawable.amenity_marker));
				oi.setTitle(cn.getName());
				InfoWindow infoWindow = new MyInfoWindow(
						R.layout.bonuspack_bubble, mapView);
				oi.setInfoWindow(infoWindow);

				anotherOverlayItemArray.add(oi);
			}
		}
		mapView.getOverlays().addAll(anotherOverlayItemArray);
		mapView.getOverlays().add(0, mapEventsOverlay);
		// ---

		// Add Scale Bar
		// ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
		// mapView.getOverlays().add(myScaleBarOverlay);
		// mapView.getOverlayManager().add(mShadeAreaOverlay);

		// InfoWindow.getOpenedInfoWindowsOn(mapView)

	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// mRotationDetector.onTouchEvent(event);
	// return super.onTouchEvent(event);
	// }
	//
	// @Override
	// public void OnRotation(RotationGestureDetector rotationDetector) {
	// float angle = rotationDetector.getAngle();
	// Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_map);

		item.setEnabled(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		// case MENU_LIMIT_SCROLLING_ID:
		// setLimitScrolling(mapView.getScrollableAreaLimit() == null);
		// break;

		case R.id.action_about:
			startActivity(new Intent(this, AboutAndFaqs.class));
			break;
		case R.id.actionbar_search:
			startActivity(new Intent(this, Search.class));
			break;
		case R.id.action_search:
			startActivity(new Intent(this, Search.class));
			break;
		case R.id.action_category:
			startActivity(new Intent(this, Category.class));
			break;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean longPressHelper(GeoPoint arg0) {
		// TODO Auto-generated method stub
		InfoWindow.closeAllInfoWindowsOn(mapView);
		return false;
	}

	@Override
	public boolean singleTapConfirmedHelper(GeoPoint arg0) {
		// TODO Auto-generated method stub
		InfoWindow.closeAllInfoWindowsOn(mapView);
		return true;
	}

	protected void setLimitScrolling(boolean limitScrolling) {
		if (limitScrolling) {
			mapView.getController().setZoom(17);
			mapView.setScrollableAreaLimit(sCentralParkBoundingBox);
			mapView.setMinZoomLevel(17);
			mapView.setMaxZoomLevel(18);
			// mapView.getController().animateTo(
			// sCentralParkBoundingBox.getCenter());
			// mShadeAreaOverlay.setEnabled(true);
			mapView.invalidate();
		} else {
			mapView.setScrollableAreaLimit(null);
			mapView.setMinZoomLevel(null);
			mapView.setMaxZoomLevel(null);
			// mShadeAreaOverlay.setEnabled(false);
			mapView.invalidate();
		}
	}

	private class MyInfoWindow extends InfoWindow {

		public MyInfoWindow(int layoutResId, MapView mapView) {
			// this.layoutResId = layoutResId;
			// mView = mapView;
			// amenityName = name;
			super(layoutResId, mapView);
		}

		public void onClose() {
			close();
		}

		public void onOpen(Object marker) {
			LinearLayout layout = (LinearLayout) mView
					.findViewById(R.id.bubble_layout);
			Marker current = (Marker) marker;
			for (int i = 0; i < mapView.getOverlays().size(); ++i) {
				Overlay o = mapView.getOverlays().get(i);
				if (o instanceof Marker) {
					Marker m = (Marker) o;
					;
					if (!m.getTitle().equals(current.getTitle()))
						m.closeInfoWindow();
				}
			}
			final String title = current.getTitle();
			TextView txtTitle = (TextView) mView
					.findViewById(R.id.bubble_title);
			txtTitle.setText(title);
			layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Override Marker's onClick behaviour here
					Intent i = new Intent(v.getContext(), AmenityBuilding.class);

					i.putExtra("AmenityName", title);

					startActivity(i);

				}
			});

		}

	}

	@Override
	public boolean onScroll(ScrollEvent arg0) {
		// TODO Auto-generated method stub
		InfoWindow.closeAllInfoWindowsOn(mapView);
		return false;
	}

	@Override
	public boolean onZoom(ZoomEvent arg0) {
		InfoWindow.closeAllInfoWindowsOn(mapView);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mapView.getTileProvider().clearTileCache();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mapView.getTileProvider().clearTileCache();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mapView.getTileProvider().clearTileCache();
		super.onDestroy();
	}

	// @Override
	// protected void onResume() {
	// Log.v("Example", "onResume");
	//
	// String action = getIntent().getAction();
	// // Prevent endless loop by adding a unique action, don't restart if
	// // action is present
	// if (action == null || !action.equals("Already created")) {
	// Log.v("Example", "Force restart");
	// Intent intent = new Intent(this, Map.class);
	// startActivity(intent);
	// finish();
	// }
	// // Remove the unique action so the next time onResume is called it will
	// // restart
	// else
	// getIntent().setAction(null);
	//
	// super.onResume();
	// }

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// class ShadeAreaOverlay extends Overlay {
	//
	// final GeoPoint mTopLeft;
	// final GeoPoint mBottomRight;
	// final Point mTopLeftPoint = new Point();
	// final Point mBottomRightPoint = new Point();
	//
	// public ShadeAreaOverlay(Context ctx) {
	// super(ctx);
	// mTopLeft = new GeoPoint(sCentralParkBoundingBox.getLatNorthE6(),
	// sCentralParkBoundingBox.getLonWestE6());
	// mBottomRight = new GeoPoint(
	// sCentralParkBoundingBox.getLatSouthE6(),
	// sCentralParkBoundingBox.getLonEastE6());
	// }
	//
	// @Override
	// protected void draw(Canvas c, MapView osmv, boolean shadow) {
	// if (shadow)
	// return;
	//
	// final Projection proj = osmv.getProjection();
	//
	// proj.toPixels(mTopLeft, mTopLeftPoint);
	// proj.toPixels(mBottomRight, mBottomRightPoint);
	//
	// Rect area = new Rect(mTopLeftPoint.x, mTopLeftPoint.y,
	// mBottomRightPoint.x, mBottomRightPoint.y);
	// c.drawRect(area, sPaint);
	// }
	// }

}
