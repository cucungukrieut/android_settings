/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License 
 * you may obtain at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * You can redistribute, modify or publish any part of the code written within this file but as it 
 * is described in the License, the software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
 */
package universum.studios.android.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import universum.studios.android.setting.key.PreferenceScreenKeyModificator;
import universum.studios.android.widget.adapter.DataSet;

/**
 * A {@link PreferenceFragment} implementation which inflates its layout from a style specified in
 * the current theme.
 *
 * <h3>Theme style attribute</h3>
 * {@link R.attr#uiSettingsFragmentStyle uiSettingsFragmentStyle}
 *
 * @author Martin Albedinsky
 */
public abstract class SettingsBaseFragment extends PreferenceFragment {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingsBaseFragmentCompat";

	/**
	 * Base for keys used to store state of SettingsBaseFragment fragments.
	 */
	private static final String SAVED_STATE_KEY_BASE = SettingsBaseFragment.class.getName() + ".SAVED_STATE.";

	/**
	 * Key used to store position of the first visible preference item in the settings fragment's
	 * list view so such state may be restored later.
	 *
	 * @see #onSaveInstanceState(Bundle)
	 * @see #onViewCreated(View, Bundle)
	 */
	private static final String SAVED_STATE_FIRST_VISIBLE_ITEM_POSITION = SAVED_STATE_KEY_BASE + "FirstVisibleItemPosition";

	/**
	 * Key used to store top offset of the first visible preference item in the settings fragment's
	 * list view so such state may be restored later.
	 *
	 * @see #onSaveInstanceState(Bundle)
	 * @see #onViewCreated(View, Bundle)
	 */
	private static final String SAVED_STATE_FIRST_VISIBLE_ITEM_TOP = SAVED_STATE_KEY_BASE + "FirstVisibleItemTop";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Modificator that is used to modify keys of preferences displayed in preference screen associated
	 * with this fragment.
	 */
	private PreferenceScreenKeyModificator mKeyModificator;

	/**
	 * Boolean flag indicating whether there is a pending request for preferences binding registered
	 * or not.
	 *
	 * @see #requestBindPreferences()
	 */
	private boolean mPendingBindPreferencesRequest;

	/**
	 * Boolean flag indicating whether the preferences for this fragment has been added via one
	 * of {@link #addPreferencesFromResource(int)} or {@link #addPreferencesFromIntent(Intent)} methods.
	 */
	private boolean mPreferencesAdded;

	/**
	 * Saved position of the first visible preference item from the preferences {@link ListView}.
	 * This position is used to restore the scroll position when a new view for this fragment is
	 * re-created.
	 *
	 * @see #onViewCreated(View, Bundle)
	 * @see #onDestroyView()
	 */
	private int mSavedFirstVisibleItemPosition = DataSet.NO_POSITION;

	/**
	 * Saved top offset of the first visible preference item from the preferences {@link ListView}.
	 * This top offset is used along with {@link #mSavedFirstVisibleItemPosition} for restoring of
	 * the scroll position when a new view for this fragment is re-created.
	 *
	 * @see #onViewCreated(View, Bundle)
	 * @see #onDestroyView()
	 */
	private int mSavedFirstVisibleItemTop;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a modificator that should be used to modify keys of preferences displayed in preference
	 * screen associated with this fragment.
	 * <p>
	 * <b>If you want to store setting preferences in a multi-account application where each account
	 * should have its associated set of settings stored separately, consider using {@link Context#getSharedPreferences(String, int)}
	 * where you would create {@link SharedPreferences} for each of accounts by its unique identifier
	 * used as name for shared preferences file. You can always attach the account dependent preferences
	 * to a settings fragment via its associated {@link #getPreferenceManager() PreferenceManager}.
	 * This approach is a more elegant and error-prone solution than specifying key modificator for
	 * that matter.</b>
	 *
	 * @param keyModificator The desired modificator. May be {@code null} to clear the current one.
	 */
	protected void setPreferenceScreenKeyModificator(@Nullable PreferenceScreenKeyModificator keyModificator) {
		this.mKeyModificator = keyModificator;
		if (mPreferencesAdded && mKeyModificator != null) {
			mKeyModificator.modifyKeys(getPreferenceScreen());
		}
	}

	/**
	 * Delegated call to {@link ActivityCompat#checkSelfPermission(Context, String)}.
	 *
	 * @param permission The desired permission for which to perform check.
	 * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
	 * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
	 */
	@CheckResult
	protected int checkSelfPermission(@NonNull String permission) {
		return ActivityCompat.checkSelfPermission(getActivity(), permission);
	}

	/**
	 */
	@Override
	@CheckResult
	public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && super.shouldShowRequestPermissionRationale(permission);
	}

	/**
	 * Invokes {@link #requestPermissions(String[], int)} on Android versions above {@link Build.VERSION_CODES#M Marshmallow}
	 * (including).
	 * <p>
	 * Calling this method on Android versions before <b>MARSHMALLOW</b> will be ignored.
	 *
	 * @param permissions The desired set of permissions to request.
	 * @param requestCode Code to identify this request in {@link #onRequestPermissionsResult(int, String[], int[])}.
	 */
	protected void supportRequestPermissions(@NonNull String[] permissions, int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			requestPermissions(permissions, requestCode);
	}

	/**
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 * Adds a preferences from the specified <var>preferencesResId</var> Xml resource and registers
	 * this fragment as {@link android.preference.Preference.OnPreferenceChangeListener} on the current
	 * preference screen and calls {@link #onPreferencesAdded()}.
	 */
	@Override
	public void addPreferencesFromResource(@XmlRes int preferencesResId) {
		super.addPreferencesFromResource(preferencesResId);
		this.handlePreferencesAdded();
	}

	/**
	 * Adds a preferences from the specified <var>intent</var> and registers this fragment as
	 * {@link android.preference.Preference.OnPreferenceChangeListener} on the current preference
	 * screen and calls {@link #onPreferencesAdded()}.
	 */
	@Override
	public void addPreferencesFromIntent(@NonNull Intent intent) {
		super.addPreferencesFromIntent(intent);
		this.handlePreferencesAdded();
	}

	/**
	 * Handles call to one of {@link #addPreferencesFromResource(int)} or {@link #addPreferencesFromIntent(Intent)}
	 * methods.
	 */
	private void handlePreferencesAdded() {
		this.mPreferencesAdded = true;
		if (mKeyModificator != null) {
			mKeyModificator.modifyKeys(getPreferenceScreen());
		}
		onPreferencesAdded();
	}

	/**
	 * Called immediately after {@link #addPreferencesFromResource(int)} or {@link #addPreferencesFromIntent(Intent)}
	 * when preferences for this fragment has been added into the associated preference screen.
	 * <p>
	 * Default implementation invokes {@link #onBindPreferences()} if binding of preferences has been
	 * requested before actually adding them.
	 * <p>
	 * <b>Note, that if this fragment has attached {@link PreferenceScreenKeyModificator} the added
	 * preferences have already theirs keys modified.</b>
	 *
	 * #setPreferenceScreenKeyModificator(PreferenceScreenKeyModificator)
	 *
	 * @see #getPreferenceScreen()
	 */
	protected void onPreferencesAdded() {
		if (mPendingBindPreferencesRequest) {
			this.mPendingBindPreferencesRequest = false;
			onBindPreferences();
		}
	}

	/**
	 * Requests binding of preferences via {@link #onBindPreferences()}.
	 * <p>
	 * If preferences for this fragment has not been added yet via one of {@link #addPreferencesFromResource(int)}
	 * or {@link #addPreferencesFromIntent(Intent)} methods, a pending request to bind preferences
	 * is registered and binding will be performed when {@link #onPreferencesAdded()} is called,
	 * otherwise call to this method immediately invokes {@link #onBindPreferences()}.
	 */
	protected void requestBindPreferences() {
		if (mPreferencesAdded) onBindPreferences();
		else this.mPendingBindPreferencesRequest = true;
	}

	/**
	 * Called immediately after {@link #requestBindPreferences()} if preferences for this fragment
	 * has been already added via one of {@link #addPreferencesFromIntent(Intent)} or
	 * {@link #addPreferencesFromResource(int)} methods.
	 */
	protected void onBindPreferences() {
		// Inheritance hierarchies may perform here binding of the preferences presented in context
		// of this fragment.
	}

	/**
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final TypedArray attributes = inflater.getContext().obtainStyledAttributes(
				null,
				R.styleable.Ui_Settings_Fragment,
				R.attr.uiSettingsFragmentStyle,
				0
		);
		final int layoutResource = attributes.getResourceId(
				R.styleable.Ui_Settings_Fragment_android_layout,
				R.layout.ui_settings_fragment
		);
		attributes.recycle();
		return inflater.inflate(layoutResource, container, false);
	}

	/**
	 * This implementation restores the first visible item's position and its offset.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final ListView listView = (ListView) view.findViewById(android.R.id.list);
		if (listView != null) {
			final int itemPosition;
			final int itemTop;
			if (savedInstanceState != null) {
				itemPosition = savedInstanceState.getInt(SAVED_STATE_FIRST_VISIBLE_ITEM_POSITION, 0);
				itemTop = savedInstanceState.getInt(SAVED_STATE_FIRST_VISIBLE_ITEM_TOP, 0);
			} else if (mSavedFirstVisibleItemPosition != DataSet.NO_POSITION) {
				itemPosition = mSavedFirstVisibleItemPosition;
				itemTop = mSavedFirstVisibleItemTop;
				this.mSavedFirstVisibleItemPosition = DataSet.NO_POSITION;
				this.mSavedFirstVisibleItemTop = 0;
			} else {
				itemPosition = itemTop = 0;
			}
			view.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelectionFromTop(itemPosition, itemTop);
				}
			});
		}
	}

	/**
	 * This implementation saves the current scroll position of the preferences list view.
	 */
	@Override
	@SuppressWarnings("ConstantConditions")
	public void onDestroyView() {
		super.onDestroyView();
		final ListView listView = (ListView) getView().findViewById(android.R.id.list);
		if (listView != null) {
			this.mSavedFirstVisibleItemPosition = listView.getFirstVisiblePosition();
			if (listView.getChildCount() > 0) {
				this.mSavedFirstVisibleItemTop = listView.getChildAt(0).getTop();
				if (mSavedFirstVisibleItemTop < 0) {
					// The item is scrolled beyond the top visible boundary of the list view, use
					// item at the next position.
					this.mSavedFirstVisibleItemTop = listView.getChildAt(1).getTop();
					this.mSavedFirstVisibleItemPosition += 1;
				}
			}
		}
	}

	/**
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt(SAVED_STATE_FIRST_VISIBLE_ITEM_POSITION, mSavedFirstVisibleItemPosition);
		state.putInt(SAVED_STATE_FIRST_VISIBLE_ITEM_TOP, mSavedFirstVisibleItemTop);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
