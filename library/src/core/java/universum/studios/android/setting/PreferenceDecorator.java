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
class PreferenceDecorator {

	/**
	 * Interface ===================================================================================
	 */

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
	 * Set of attributes to be used to parse typed values for the attached preference whenever
	 * {@link #processAttributes(Context, AttributeSet, int, int)} is called.
	 */
	private final int[] mStyleableAttrs;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Same as {@link #PreferenceDecorator(Preference, int[])} with {@code null} <var>styleableAttrs</var>.
	 */
	PreferenceDecorator(Preference preference) {
		this(preference, null);
	}

	/**
	 * Creates a new instance of PreferenceDecorator for the given <var>preference</var>.
	 *
	 * @param preference     The preference for which to create new decorator.
	 * @param styleableAttrs Set of styleable attributes specific for the preference. These attributes
	 *                       will be used to obtain an instance of {@link TypedArray} passed to
	 *                       {@link #onProcessTypedValues(Context, TypedArray)} whenever
	 *                       {@link #processAttributes(Context, AttributeSet, int, int)} is called.
	 *                       May be {@code null} if the preference does not have any additional
	 *                       attributes to be processed by the decorator.
	 */
	PreferenceDecorator(Preference preference, int[] styleableAttrs) {
		this.mPreference = preference;
		this.mStyleableAttrs = styleableAttrs;
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
		final TypedArray preferenceTypedArray = context.obtainStyledAttributes(attrs, R.styleable.Ui_Settings_Preference, defStyleAttr, defStyleRes);
		if (preferenceTypedArray.hasValue(R.styleable.Ui_Settings_Preference_uiVectorIcon)) {
			setVectorIcon(preferenceTypedArray.getResourceId(R.styleable.Ui_Settings_Preference_uiVectorIcon, 0));
		}
		preferenceTypedArray.recycle();
		if (mStyleableAttrs != null) {
			final TypedArray typedArray = context.obtainStyledAttributes(attrs, mStyleableAttrs, defStyleAttr, defStyleRes);
			onProcessTypedValues(context, typedArray);
			typedArray.recycle();
		}
	}

	/**
	 * Invoked from {@link #processAttributes(Context, AttributeSet, int, int)} to process all values
	 * from the given <var>typedArray</var> that are related to the attached preference.
	 *
	 * @param context    The context that can be used to access resource values.
	 * @param typedArray The typed array obtained for the styleable attributes supplied to this decorator
	 *                   during its initialization.
	 */
	void onProcessTypedValues(Context context, TypedArray typedArray) {
		// May be implemented by the inheritance hierarchies.
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
	 *                   additional modifications.
	 */
	void onBindView(@NonNull View view) {
		final View iconFrameView = view.findViewById(R.id.icon_frame);
		if (iconFrameView != null) {
			iconFrameView.setVisibility(mPreference.getIcon() != null ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
