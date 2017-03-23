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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import universum.studios.android.ui.util.ResourceUtils;

/**
 * PreferenceDecorator is used by extended versions of preferences to provide extended API functionality
 * for them.
 *
 * @author Martin Albedinsky
 */
abstract class PreferenceDecorator {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "PreferenceDecorator";

	/**
	 * Boolean flag indicating whether call to {@link #setCanRecycleLayout(boolean)} should be
	 * handled or not.
	 */
	private static final boolean HANDLE_CAN_RECYCLE_LAYOUT = !BuildConfig.APPLICATION_ID.contains("support");

	/**
	 * Name of the field that indicates whether the preference can recycle its layout or not.
	 */
	private static final String FIELD_NAME_CAN_RECYCLE_LAYOUT = "mCanRecycleLayout";

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
	 * Preference for which is this decorator created.
	 */
	private final Preference mPreference;

	/**
	 * Default value resolved from Xml attributes for the associated preference.
	 */
	private Object mDefaultValue;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of PreferenceDecorator for the given <var>preference</var>.
	 *
	 * @param preference The preference for which to create new decorator.
	 */
	PreferenceDecorator(Preference preference) {
		this.mPreference = preference;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * This should be called from the attached preference during its initialization.
	 *
	 * @param context      The context that can be used to access resource values.
	 * @param attrs        Set of attributes passed to the preference's constructor.
	 * @param defStyleAttr An attribute which contains a reference to a default style resource for
	 *                     the attached preference within a theme of the given context.
	 * @param defStyleRes  Resource id of the default style for the attached preference.
	 */
	void processAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_Preference, defStyleAttr, defStyleRes);
		for (int i = 0; i < attributes.getIndexCount(); i++) {
			final int index = attributes.getIndex(i);
			if (index == R.styleable.Ui_Settings_Preference_android_defaultValue) {
				this.mDefaultValue = onGetDefaultValue(attributes, index);
			} else if (index == R.styleable.Ui_Settings_Preference_uiVectorIcon) {
				setVectorIcon(attributes.getResourceId(index, 0));
			}
		}
		attributes.recycle();
	}

	/**
	 * todo:
	 *
	 * @param attributes
	 * @param index
	 * @return
	 */
	@Nullable
	abstract Object onGetDefaultValue(@NonNull TypedArray attributes, int index);

	/**
	 * Handles change in the key of the associated preference.
	 * <p>
	 * This implementation dispatches update of initial value to the associated preference.
	 */
	void handleKeyChange() {
		updateInitialValue();
	}

	/**
	 * Dispatches request for update of initial value to the associated preference via
	 * {@link #onUpdateInitialValue(boolean, Object)}.
	 */
	private void updateInitialValue() {
		final boolean shouldPersist = shouldPersist();
		if (shouldPersist && mPreference.getSharedPreferences().contains(mPreference.getKey())) {
			onUpdateInitialValue(true, null);
		} else if (mDefaultValue != null) {
			onUpdateInitialValue(false, mDefaultValue);
		}
	}

	/**
	 * Checks whether the value of the associated preference should be persisted or not.
	 *
	 * @return {@code True} if value should be persisted, {@code false} otherwise.
	 */
	private boolean shouldPersist() {
		return mPreference.getPreferenceManager() != null && mPreference.isPersistent() && mPreference.hasKey();
	}

	/**
	 * Invoked to update initial value of the associated preference.
	 *
	 * @param restorePersistedValue {@code True} if the initial value should be restored from
	 *                              preferences, {@code false} it the <var>defaultValue</var> should
	 *                              be used instead.
	 * @param defaultValue          The default value to use as initial one.
	 */
	abstract void onUpdateInitialValue(boolean restorePersistedValue, @Nullable Object defaultValue);

	/**
	 * Returns the default value resolved from Xml attributes for the associated preference.
	 *
	 * @return Default value or {@code null} if no value has been resolved.
	 */
	@Nullable
	Object getDefaultValue() {
		return mDefaultValue;
	}

	/**
	 * Sets a vector icon for the attached preference via {@link Preference#setIcon(Drawable)} where
	 * the vector drawable will be obtained via {@link ResourceUtils#getVectorDrawable(Resources, int, Resources.Theme)}.
	 *
	 * @param resId Resource id of the desired vector icon to set. May be {@code 0} to clear the
	 *              current icon.
	 */
	void setVectorIcon(@DrawableRes int resId) {
		final Context context = mPreference.getContext();
		mPreference.setIcon(ResourceUtils.getVectorDrawable(context.getResources(), resId, context.getTheme()));
	}

	/**
	 * Sets whether this preference can recycle its layout or not.
	 *
	 * @param canRecycleLayout {@code True} to identify that this preference can recycle its layout,
	 *                         {@code false} otherwise.
	 */
	@SuppressWarnings("TryWithIdenticalCatches")
	void setCanRecycleLayout(boolean canRecycleLayout) {
		if (HANDLE_CAN_RECYCLE_LAYOUT) {
			try {
				final Field canRecycleLayoutField = Preference.class.getDeclaredField(FIELD_NAME_CAN_RECYCLE_LAYOUT);
				canRecycleLayoutField.setAccessible(true);
				canRecycleLayoutField.setBoolean(mPreference, canRecycleLayout);
			} catch (NoSuchFieldException e) {
				Log.w(TAG, "Failed to set whether preference(" + getClass().getSimpleName() + ") can recycler its layout or not.", e);
			} catch (IllegalAccessException e) {
				Log.w(TAG, "Failed to set whether preference(" + getClass().getSimpleName() + ") can recycler its layout or not.", e);
			}
		}
	}

	/**
	 * This should be called from the attached preference whenever its
	 * {@link Preference#onBindView(View)} method is invoked.
	 *
	 * @param view The view/holder bound by the preference where the decorator may perform
	 *             additional modifications.
	 */
	void onBindView(@NonNull View view) {
		final View iconFrameView = view.findViewById(R.id.icon_frame);
		if (iconFrameView != null) {
			iconFrameView.setVisibility(mPreference.getIcon() == null ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}