package com.banregio.mapa.utils;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class DrawMapItem extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext;
	
	// ******* <Contructores> *******
	
	public DrawMapItem(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public DrawMapItem(Drawable defaultMarker, Context context){
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	// ******** </Contructores> ******
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void removeOverlay(OverlayItem overlay) {
		mOverlays.remove(overlay);
	    populate();
	}
	
	public void clear() {
        mOverlays.clear();
        populate();
    }
	
	@Override
	protected boolean onTap(int arg0) {
		OverlayItem item = mOverlays.get(arg0);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
