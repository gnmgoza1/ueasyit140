package ueasy.it140.modals;
import ueasy.it140.activities.UeasySplash;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


public class ErrorModal extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Connection problem").setTitle("Update");
		
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
			//	Toast.makeText(getActivity(), "Pressed OK", Toast.LENGTH_SHORT).show();
				Intent objIntent = new Intent(getActivity(), UeasySplash.class);
				startActivity(objIntent);
			}
		});
		
		
		Dialog dialog  = builder.create();
		
		return dialog;
	}
	
	
	
    

}
