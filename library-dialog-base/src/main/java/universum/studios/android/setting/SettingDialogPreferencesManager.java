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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import universum.studios.android.dialog.Dialog;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogXmlFactory;
import universum.studios.android.setting.R;

/**
 * Manager that may be used to manage {@link SettingDialogPreference dialog prefernces} presented on
 * a single preference screen. This manager handles attaching of {@link SettingDialogPreference.OnClickListener}
 * to all dialog preferences of {@link PreferenceScreen} passed to {@link #attachToPreferenceScreen(PreferenceScreen)}
 * and showing of preference dialogs for the associated dialog preferences whenever
 * {@link SettingDialogPreference.OnClickListener#onDialogPreferenceClick(SettingDialogPreference)}
 * is fired for a particular dialog preference. Dialog preferences manager also provides default
 * handling of button click events occurred in those shown preference dialogs via
 * {@link #handleOnPreferenceDialogButtonClick(Dialog, int)} where this method delegates the click
 * event to the associated dialog preference via {@link SettingDialogPreference#handleOnDialogButtonClick(Dialog, int)}.
 * <p>
 * Subclasses may inherit this manager and override any default behavior as needed.
 *
 * @author Martin Albedinsky
 */
@SuppressLint("LongLogTag")
public class SettingDialogPreferencesManager implements SettingDialogPreference.OnClickListener {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	private static final String TAG = "SettingDialogPreferencesManager";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Controller used to show dialog for clicked preferences.
	 */
	private DialogController mDialogController;

	/**
	 * Factory providing dialog instances for {@link #mDialogController}.
	 */
	private DialogController.DialogFactory mDialogFactory;

	/**
	 * Boolean flag indicating whether this manager is attached to preference screen or not.
	 *
	 * @see #attachToPreference(Preference)
	 * @see #detachFromPreferenceScreen(PreferenceScreen)
	 */
	private boolean mAttachedToPreferenceScreen;

	/**
	 * Array map containing dialog preferences gathered from the preferences of the {@link PreferenceScreen}
	 * to which is this manager attached. The dialog preferences are mapped to theirs corresponding
	 * dialog id.
	 *
	 * @see SettingDialogPreference#getDialogId()
	 */
	private SparseArray<SettingDialogPreference> mDialogPreferences;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of SettingDialogPreferencesManager without either {@link DialogController}
	 * nor default {@link DialogController.DialogFactory}.
	 * <p>
	 * These should be specified via {@link #setDialogController(DialogController)} and
	 * {@link #setDialogFactory(DialogController.DialogFactory)} otherwise the new manager will throw
	 * an exception if functionality related to dialogs is requested.
	 *
	 * @see #SettingDialogPreferencesManager(Activity)
	 * @see #SettingDialogPreferencesManager(Fragment)
	 */
	public SettingDialogPreferencesManager() {
		this(null, null);
	}

	/**
	 * Creates a new instance of SettingDialogPreferencesManager for the given <var>activity</var>
	 * context.
	 * <p>
	 * This constructor creates this manager with default instance of {@link DialogController} and
	 * default {@link DialogXmlFactory} with {@link R.xml#ui_settings_dialogs} file containing dialogs
	 * for all dialog preferences provided by the <b>Settings</b> library.
	 *
	 * @param activity The activity used to instantiate the default dialog controller along with
	 *                 default dialog factory.
	 * @see #SettingDialogPreferencesManager(Fragment)
	 * @see DialogController#DialogController(Activity)
	 * @see #getDialogController()
	 * @see #getDialogFactory()
	 */
	public SettingDialogPreferencesManager(@NonNull Activity activity) {
		this(new DialogController(activity), new DialogXmlFactory(activity, R.xml.ui_settings_dialogs));
	}

	/**
	 * Creates a new instance of SettingDialogPreferencesManager for the given <var>fragment</var>
	 * context.
	 * <p>
	 * This constructor creates this manager with default instance of {@link DialogController} and
	 * default {@link DialogXmlFactory} with {@link R.xml#ui_settings_dialogs} file containing dialogs
	 * for all dialog preferences provided by the <b>Settings</b> library.
	 *
	 * @param fragment The fragment used to instantiate the default dialog controller along with
	 *                 default dialog factory.
	 * @see #SettingDialogPreferencesManager(Activity)
	 * @see DialogController#DialogController(Fragment)
	 * @see #getDialogController()
	 * @see #getDialogFactory()
	 */
	public SettingDialogPreferencesManager(@NonNull Fragment fragment) {
		this(new DialogController(fragment), new DialogXmlFactory(fragment.getActivity(), R.xml.ui_settings_dialogs));
	}

	/**
	 * Creates a new instance of SettingDialogPreferencesManager for the given <var>fragmentManager</var>
	 * context.
	 * <p>
	 * This constructor creates this manager with default instance of {@link DialogController}. The
	 * dialog factory need to be specified via {@link #setDialogFactory(DialogController.DialogFactory)}
	 *
	 * @param fragmentManager The fragment manager used to instantiate the default dialog controller.
	 * @see DialogController#DialogController(FragmentManager)
	 * @see #getDialogController()
	 */
	public SettingDialogPreferencesManager(@NonNull FragmentManager fragmentManager) {
		this(new DialogController(fragmentManager), null);
	}

	/**
	 * Creates a new instance of SettingDialogPreferencesManager with the given <var>dialogController</var>
	 * and <var>dialogFactory</var>.
	 *
	 * @param dialogController Dialog controller used to show dialogs for clicked preferences.
	 * @param dialogFactory    Factory providing dialog instances for the given dialog controller.
	 */
	private SettingDialogPreferencesManager(DialogController dialogController, DialogController.DialogFactory dialogFactory) {
		this.mDialogController = dialogController;
		this.mDialogFactory = dialogFactory;
		if (mDialogController != null && mDialogFactory != null) {
			mDialogController.setDialogFactory(mDialogFactory);
		}
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a dialog controller that should be used by this manager to show dialogs for the clicked
	 * dialog preferences.
	 * <p>
	 * <b>Note</b>, that if the specified dialog controller does not have specified any dialog factory,
	 * the one specified for this manager will be set to it via {@link DialogController#setDialogFactory(DialogController.DialogFactory)}.
	 *
	 * @param dialogController The desired dialog controller.
	 * @see #getDialogController()
	 * @see #setDialogFactory(DialogController.DialogFactory)
	 */
	public void setDialogController(@NonNull DialogController dialogController) {
		this.mDialogController = dialogController;
		if (!mDialogController.hasDialogFactory()) {
			mDialogController.setDialogFactory(mDialogFactory);
		}
	}

	/**
	 * Returns the dialog controller used by this manager.
	 *
	 * @return Default dialog controller or the one specified via {@link #setDialogController(DialogController)}.
	 * @see #SettingDialogPreferencesManager(Activity)
	 * @see #SettingDialogPreferencesManager(Fragment)
	 * @see #SettingDialogPreferencesManager(FragmentManager)
	 */
	@NonNull
	public DialogController getDialogController() {
		return mDialogController;
	}

	/**
	 * Sets a factory that provides dialog instances associated with the clicked dialog preferences.
	 * The given factory will be attached to the current dialog controller via
	 * {@link DialogController#setDialogFactory(DialogController.DialogFactory)}.
	 *
	 * @param dialogFactory The desired dialog factory. May be {@code null} to clear the current one.
	 * @see #getDialogFactory()
	 * @see #setDialogController(DialogController)
	 */
	public void setDialogFactory(@Nullable DialogController.DialogFactory dialogFactory) {
		mDialogController.setDialogFactory(mDialogFactory = dialogFactory);
	}

	/**
	 * Returns the current dialog factory set to this manager.
	 *
	 * @return Default dialog factory or the one specified via {@link #setDialogFactory(DialogController.DialogFactory)}
	 * @see #setDialogFactory(DialogController.DialogFactory)
	 * @see #SettingDialogPreferencesManager(Activity)
	 * @see #SettingDialogPreferencesManager(Fragment)
	 */
	@Nullable
	public DialogController.DialogFactory getDialogFactory() {
		return mDialogFactory;
	}

	/**
	 * Attaches this manager to the given <var>preferenceScreen</var>.
	 * <p>
	 * Dialog preferences manager will use the preference screen to look up for all instances of
	 * {@link SettingDialogPreference dialog preferences} that are added to that preference screen.
	 * To all those dialog preferences is than this manager attached as {@link SettingDialogPreference.OnClickListener}
	 * so it may receive the preference click callback and show dialog for that particular preference.
	 * The context that is using this manager should dispatch {@link Dialog.OnDialogListener#onDialogButtonClick(Dialog, int)}
	 * callback to this manager via {@link #handleOnPreferenceDialogButtonClick(Dialog, int)}.
	 * <p>
	 * When this manager is no more needed it should be detached from the attached preference screen
	 * via {@link #detachFromPreferenceScreen(PreferenceScreen)}.
	 *
	 * @param preferenceScreen The preference screen to attach to.
	 */
	public void attachToPreferenceScreen(@NonNull PreferenceScreen preferenceScreen) {
		if (mAttachedToPreferenceScreen) {
			throw new IllegalStateException("Already attached to preference screen! Detach via detachFromPreferenceScreen(...) first.");
		}
		final int preferenceCount = preferenceScreen.getPreferenceCount();
		this.mDialogPreferences = new SparseArray<>(preferenceCount);
		for (int i = 0; i < preferenceCount; i++) {
			this.attachToPreference(preferenceScreen.getPreference(i));
		}
		this.mAttachedToPreferenceScreen = true;
	}

	/**
	 * Attaches this manager as {@link SettingDialogPreference.OnClickListener} to the given
	 * <var>preference</var> if it is instance of {@link SettingDialogPreference}. If the specified
	 * preference is instance of {@link PreferenceCategory} this method will recursively iterate
	 * child preferences of the specified preference category.
	 *
	 * @param preference The preference to which attach on click listener if it is a dialog preference
	 *                   type.
	 */
	private void attachToPreference(Preference preference) {
		if (preference instanceof SettingDialogPreference) {
			final SettingDialogPreference dialogPreference = (SettingDialogPreference) preference;
			final int dialogId = dialogPreference.getDialogId();
			if (dialogId != SettingDialogPreference.NO_DIALOG_ID) {
				mDialogPreferences.put(dialogId, dialogPreference);
				dialogPreference.setOnClickListener(this);
				onAttachedToPreference(dialogPreference);
			} else {
				Log.w(TAG, "Found preference(" + dialogPreference.getClass().getSimpleName() + ") without dialog id!");
			}
		} else if (preference instanceof PreferenceCategory) {
			final PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
			final int preferenceCount = preferenceCategory.getPreferenceCount();
			for (int i = 0; i < preferenceCount; i++) {
				attachToPreference(preferenceCategory.getPreference(i));
			}
		}
	}

	/**
	 * Called when this manager has been successfully attached to the given <var>dialogPreference</var>.
	 *
	 * @param dialogPreference The dialog preference to which has been this manager just attached.
	 * @see #attachToPreferenceScreen(PreferenceScreen)
	 */
	protected void onAttachedToPreference(@NonNull SettingDialogPreference dialogPreference) {
		// May be implemented by the inheritance hierarchies.
	}

	/**
	 * Searches for the dialog preference associated with the specified <var>dialogId</var>.
	 * <p>
	 * <b>Note</b>, that the manager should be attached to preference screen via
	 * {@link #attachToPreferenceScreen(PreferenceScreen)} otherwise this method will always return
	 * {@code null}.
	 *
	 * @param dialogId Id of the dialog associated with the desired preference to find.
	 * @return Found dialog preference or {@code null} if this manager is not attached to preference
	 * screen at this time or there is no dialog preference with such dialog id associated.
	 */
	@Nullable
	public SettingDialogPreference findDialogPreference(int dialogId) {
		return mDialogPreferences != null ? mDialogPreferences.get(dialogId) : null;
	}

	/**
	 * Detaches this manager from the given <var>preferenceScreen</var> if previously attached via
	 * {@link #attachToPreferenceScreen(PreferenceScreen)}.
	 *
	 * @param preferenceScreen The preference screen from which to detach this manager.
	 */
	public void detachFromPreferenceScreen(@NonNull PreferenceScreen preferenceScreen) {
		if (mAttachedToPreferenceScreen) {
			for (int i = 0; i < mDialogPreferences.size(); i++) {
				final SettingDialogPreference preference = mDialogPreferences.get(mDialogPreferences.keyAt(i));
				preference.setOnClickListener(null);
				onDetachedFromPreference(preference);
			}
			this.mDialogPreferences = null;
			this.mAttachedToPreferenceScreen = false;
		}
	}

	/**
	 * Called when this manager has been successfully detached from the given <var>dialogPreference</var>.
	 *
	 * @param dialogPreference The dialog preference from which has been this manager just detached.
	 * @see #detachFromPreferenceScreen(PreferenceScreen)
	 */
	protected void onDetachedFromPreference(@NonNull SettingDialogPreference dialogPreference) {
		// May be implemented by the inheritance hierarchies.
	}

	/**
	 * This implementation check whether this manager has dialog factory attached and also if that
	 * factory provides dialog instance with dialog id associated with the clicked <var>dialogPreference</var>.
	 * If so, {@link #onShowPreferenceDialog(DialogController, SettingDialogPreference)} is called
	 * to show the preference dialog.
	 */
	@Override
	public boolean onDialogPreferenceClick(@NonNull SettingDialogPreference dialogPreference) {
		final int dialogId = dialogPreference.getDialogId();
		if (dialogId != SettingDialogPreference.NO_DIALOG_ID) {
			final DialogController.DialogFactory dialogFactory = mDialogController.getDialogFactory();
			if (dialogFactory == null) {
				throw new NullPointerException("No dialog factory specified to provide dialogs for preferences!");
			}
			if (!dialogFactory.isDialogProvided(dialogId)) {
				Log.w(
						TAG,
						"Clicked preference(" + dialogPreference.getClass().getSimpleName() + ") with dialog " +
								"id(" + dialogId + ") but the current dialog factory does not provide dialog with such id!"
				);
				return false;
			}
			return onShowPreferenceDialog(mDialogController, dialogPreference);
		}
		Log.w(TAG, "Clicked preference(" + dialogPreference.getClass().getSimpleName() + ") with not specified dialog id!");
		return false;
	}

	/**
	 * Called from {@link #onDialogPreferenceClick(SettingDialogPreference)} to show a dialog for
	 * the clicked <var>preference</var>.
	 *
	 * @param dialogController Current dialog controller of this manager that may be used to show
	 *                         the dialog associated with the clicked preference..
	 * @param preference       The preference that has been clicked.
	 * @return {@code True} if the preference dialog has been successfully shown, {@code false} otherwise.
	 */
	protected boolean onShowPreferenceDialog(@NonNull DialogController dialogController, @NonNull SettingDialogPreference preference) {
		return dialogController.showDialog(preference.getDialogId(), preference.getDialogOptions());
	}

	/**
	 * Handles button click occurred in the given preference <var>dialog</var>.
	 *
	 * @param dialog The preference dialog where the <var>button</var> has been clicked.
	 * @param button The clicked dialog button.
	 * @return {@code True} if this manager is attached to preference screen and the button click
	 * has been successfully handled by the associated dialog preference, {@code false} otherwise.
	 * @see SettingDialogPreference#handleOnDialogButtonClick(Dialog, int)
	 */
	public boolean handleOnPreferenceDialogButtonClick(@NonNull Dialog dialog, @Dialog.Button int button) {
		if (mAttachedToPreferenceScreen) {
			final int dialogId = dialog.getDialogId();
			final SettingDialogPreference dialogPreference = findDialogPreference(dialogId);
			if (dialogPreference == null) {
				Log.w(TAG, "No preference found for the clicked button of dialog with id(" + dialogId + ")!");
				return false;
			}
			return dialogPreference.handleOnDialogButtonClick(dialog, button);
		}
		return false;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
