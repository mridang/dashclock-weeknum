package com.mridang.weeknum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.acra.ACRA;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class WeeknumWidget extends ImprovedExtension {

	/*
	 * (non-Javadoc)
	 * @see com.mridang.weeknum.ImprovedExtension#getIntents()
	 */
	@Override
	protected IntentFilter getIntents() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.weeknum.ImprovedExtension#getTag()
	 */
	@Override
	protected String getTag() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.weeknum.ImprovedExtension#getUris()
	 */
	@Override
	protected String[] getUris() {
		return null;
	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int intReason) {

		Log.d(getTag(), "Calculating the current week number");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(false);

		try {

			Calendar calCalendar = new GregorianCalendar();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
			Uri.Builder uriBuilder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
			ContentUris.appendId(uriBuilder, System.currentTimeMillis());			

			Calendar calWeek = Calendar.getInstance();
			calWeek.clear();
			calWeek.set(Calendar.WEEK_OF_YEAR, calCalendar.get(Calendar.WEEK_OF_YEAR));
			calWeek.set(Calendar.YEAR, calCalendar.get(Calendar.YEAR));

			String strStart = dateFormat.format(calWeek.getTime());
			calWeek.add(Calendar.DAY_OF_WEEK, 6);
			String strEnd = dateFormat.format(calWeek.getTime());
			Integer intWeek = calCalendar.get(Calendar.WEEK_OF_YEAR);

			edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW).setData(uriBuilder.build()));
			edtInformation.expandedTitle(getString(R.string.status, intWeek));
			edtInformation.status(intWeek.toString());
			edtInformation.expandedBody(getString(R.string.message, strStart, strEnd));
			edtInformation.visible(true);

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e(getTag(), "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		doUpdate(edtInformation);

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.weeknum.ImprovedExtension#onReceiveIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onReceiveIntent(Context ctxContext, Intent ittIntent) {
		onUpdateData(UPDATE_REASON_MANUAL);
	}

}