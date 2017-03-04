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

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * A {@link PreferenceActivity} implementation that uses {@link AppCompatDelegate} to provide
 * compatibility support features like attaching of custom action bar via {@link #setSupportActionBar(Toolbar)}.
 * <p>
 * This activity also intercepts default setting of list adapter with headers and uses {@link SettingHeadersAdapter}
 * to provide item views for the loaded preference headers via {@link #loadHeadersFromResource(int, List)}.
 * The headers adapter is specified via {@link #setHeadersAdapter(ListAdapter)} and the current one
 * may be obtained via {@link #setHeadersAdapter(ListAdapter)}. The headers that has been loaded and
 * are used by the headers adapter may be obtained via {@link #getHeaders()}.
 * <p>
 * If a {@link Toolbar} should be presented in the activity's view hierarchy, it may be added via
 * {@link #addToolbar()} which also attacheds the added toolbar as support action bar.
 *
 * <h3>Theme style attribute</h3>
 * {@link R.attr#uiSettingsActivityStyle uiSettingsActivityStyle}
 *
 * @author Martin Albedinsky
 */
public abstract class SettingsBaseActivity extends PreferenceActivity {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingsBaseActivity";

	/**
	 * Bundle key used by parent {@link PreferenceActivity} to save and restore list of headers.
	 */
	private static final String BUNDLE_HEADERS = ":android:headers";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Corrector used by instances of SettingsBaseActivity to correct/modify theirs root layout to
	 * match actual Material design guidelines.
	 */
	private static final LayoutCorrector sLayoutCorrector = new BasicLayoutCorrector();

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Application compatibility delegate used by this activity to provide compatibility features.
	 */
	private AppCompatDelegate mCompatDelegate;

	/**
	 * Current content view of this activity. Should be always valid whenever {@link #onContentChanged()}
	 * is received.
	 */
	private View mContentView;

	/**
	 * Toolbar added into view hierarchy of this activity via {@link #addToolbar()}.
	 */
	private Toolbar mToolbar;

	/**
	 * List containing preference headers inflated for this activity via {@link #loadHeadersFromResource(int, List)}.
	 */
	private List<Header> mHeaders;

	/**
	 * Adapter providing item views for inflated preference headers.
	 *
	 * @see #setHeadersAdapter(ListAdapter)
	 */
	private ListAdapter mHeadersAdapter;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Returns the application compatibility delegate that is used by this activity to provide
	 * compatibility features like attaching of support action bar via {@link #setSupportActionBar(Toolbar)}.
	 *
	 * @return This activity's compatibility delegate.
	 */
	@NonNull
	protected final AppCompatDelegate delegate() {
		return mCompatDelegate == null ? (mCompatDelegate = AppCompatDelegate.create(this, null)) : mCompatDelegate;
	}

	/**
	 */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		delegate().installViewFactory();
		delegate().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			this.mHeaders = savedInstanceState.getParcelableArrayList(BUNDLE_HEADERS);
		}
		if (mHeaders != null) {
			setHeadersAdapter(new SettingHeadersAdapter(this, mHeaders));
		}
	}

	/**
	 */
	@Override
	public void loadHeadersFromResource(@XmlRes int resid, @NonNull List<Header> target) {
		super.loadHeadersFromResource(resid, this.mHeaders = target);
	}

	/**
	 * Returns the list of headers that has been loaded via {@link #loadHeadersFromResource(int, List)}.
	 *
	 * @return The loaded list of headers or {@code null} if no headers has been loaded yet.
	 */
	@Nullable
	protected final List<Header> getHeaders() {
		return mHeaders;
	}

	/**
	 * Sets an adapter that will provide item views for preference items to be presented in the
	 * view hierarchy of this activity.
	 *
	 * @param adapter The desired adapter providing headers for this activity's list view. May be
	 *                {@code null} to clear the current one.
	 */
	public void setHeadersAdapter(@Nullable ListAdapter adapter) {
		super.setListAdapter(mHeadersAdapter = adapter);
	}

	/**
	 * Returns the current headers adapter specified for this activity.
	 *
	 * @return This activity's headers adapter or {@code null} if there was no adapter specified yet.
	 * @see #setHeadersAdapter(ListAdapter)
	 */
	@Nullable
	public ListAdapter getHeadersAdapter() {
		return mHeadersAdapter;
	}

	/**
	 * This method does nothing.
	 *
	 * @deprecated Use {@link #setHeadersAdapter(ListAdapter)} instead.
	 */
	@Override
	@Deprecated
	public final void setListAdapter(ListAdapter adapter) {
		// Ignored.
	}

	/**
	 * @return Always {@code null}.
	 * @deprecated Use {@link #getHeadersAdapter()} instead.
	 */
	@Override
	@Deprecated
	public final ListAdapter getListAdapter() {
		return null;
	}

	/**
	 */
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delegate().onPostCreate(savedInstanceState);
	}

	/**
	 */
	@NonNull
	@Override
	public MenuInflater getMenuInflater() {
		return delegate().getMenuInflater();
	}

	/**
	 */
	@Override
	public void invalidateOptionsMenu() {
		delegate().invalidateOptionsMenu();
	}

	/**
	 */
	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		delegate().setContentView(layoutResID);
	}

	/**
	 */
	@Override
	public void setContentView(View view) {
		delegate().setContentView(view);
	}

	/**
	 */
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		delegate().setContentView(view, params);
	}

	/**
	 */
	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		delegate().addContentView(view, params);
	}

	/**
	 */
	@Override
	@CallSuper
	public void onContentChanged() {
		super.onContentChanged();
		this.mContentView = ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
		sLayoutCorrector.correctLayout(mContentView);
	}

	/**
	 * Returns the content (root) view of this activity.
	 *
	 * @return This activity's content view.
	 * @throws IllegalStateException If content view of this activity has not been created yet.
	 * @see #onContentChanged()
	 */
	@NonNull
	protected final View getContentView() {
		if (mContentView == null) {
			throw new IllegalStateException("Activity(" + getClass() + ") does not have content view created yet!");
		}
		return mContentView;
	}

	/**
	 * Adds {@link Toolbar} into view hierarchy of this activity if it has not been added yet.
	 * <p>
	 * This implementation creates a new instance of the toolbar via {@link #onCreateToolbar(LayoutInflater, ViewGroup)}
	 * if it is not created and added yet and adds it into this activity's content view at the
	 * {@code 0} position, which is at the top. Also the added toolbar is attached to this activity
	 * as support action bar via {@link #setSupportActionBar(Toolbar)} and if this activity is not
	 * currently in state of hiding headers, the displaying of home as up for the attached action bar
	 * is enabled via {@link ActionBar#setDisplayHomeAsUpEnabled(boolean)} and {@link View.OnClickListener}
	 * is attached to the toolbar via {@link Toolbar#setNavigationOnClickListener(View.OnClickListener)}
	 * which when its {@link View.OnClickListener#onClick(View)} callback is fired calls
	 * {@link #onBackPressed()} of this activity.
	 *
	 * @return The added toolbar.
	 * @throws AndroidRuntimeException If the content view of this activity is not a ViewGroup, which
	 *                                 should not happen.
	 * @see #getToolbar()
	 * @see #getSupportActionBar()
	 */
	@NonNull
	@SuppressWarnings("ConstantConditions")
	protected Toolbar addToolbar() {
		if (!(mContentView instanceof ViewGroup)) {
			throw new AndroidRuntimeException("Cannot add Toolbar. The content view of activity(" + getClass() + ") is not a ViewGroup!");
		}
		if (mToolbar == null) {
			this.mToolbar = onCreateToolbar(getLayoutInflater(), (ViewGroup) mContentView);
			// Add Toolbar at the top of layout which is LinearLayout.
			((ViewGroup) mContentView).addView(mToolbar, 0);
			setSupportActionBar(mToolbar);
			if (onIsHidingHeaders()) {
				final ActionBar actionBar = getSupportActionBar();
				actionBar.setDisplayHomeAsUpEnabled(true);
				mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

					/**
					 */
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
			}
		}
		return mToolbar;
	}

	/**
	 * Invoked as result to call of {@link #addToolbar()} to create a new instance of {@link Toolbar}
	 * that will be added into view hierarchy of this activity.
	 * <p>
	 * This implementation by default inflates {@link R.layout#ui_toolbar} layout which contains
	 * single Toolbar. Subclasses may override this method to create or inflate custom toolbar.
	 *
	 * @param inflater Layout inflater that may be used to inflate the requested toolbar.
	 * @param root     This activity's root view.
	 * @return Instance of toolbar to be added into this activity's view hierarchy.
	 */
	@NonNull
	protected Toolbar onCreateToolbar(@NonNull LayoutInflater inflater, @NonNull ViewGroup root) {
		return (Toolbar) inflater.inflate(R.layout.ui_toolbar, root, false);
	}

	/**
	 * Returns the toolbar added to the view hierarchy of this activity via {@link #addToolbar()}.
	 * <p>
	 * If added, this toolbar may be also obtained as support action bar via {@link #getSupportActionBar()}.
	 *
	 * @return This activity's toolbar if added, {@code null} otherwise.
	 */
	@Nullable
	protected Toolbar getToolbar() {
		return mToolbar;
	}

	/**
	 */
	@Override
	public View findViewById(@IdRes int id) {
		return delegate().findViewById(id);
	}

	/**
	 * Attaches the given <var>toolbar</var> to this activity as support action bar.
	 *
	 * @param toolbar The desired toolbar to attach as action bar. May be {@code null} to clear the
	 *                current one.
	 * @see #getSupportActionBar()
	 */
	public void setSupportActionBar(@Nullable Toolbar toolbar) {
		delegate().setSupportActionBar(toolbar);
	}

	/**
	 * Returns the support action bar attached to this activity via {@link #setSupportActionBar(Toolbar)}.
	 *
	 * @return This activity's support action bar or {@code null} if there is no action bar attached.
	 */
	@Nullable
	public ActionBar getSupportActionBar() {
		return delegate().getSupportActionBar();
	}

	/**
	 */
	@Override
	protected void onPostResume() {
		super.onPostResume();
		delegate().onPostResume();
	}

	/**
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (!(mHeadersAdapter instanceof SettingHeadersAdapter)) {
			super.onListItemClick(l, v, position, id);
			return;
		}
		final SettingHeadersAdapter.Item item = (SettingHeadersAdapter.Item) mHeadersAdapter.getItem(position);
		onHeaderClick(item.getHeader(), position);
	}

	/**
	 */
	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		delegate().onSaveInstanceState(state);
	}

	/**
	 */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		delegate().setTitle(title);
	}

	/**
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		delegate().onConfigurationChanged(newConfig);
	}

	/**
	 */
	@Override
	protected void onStop() {
		super.onStop();
		delegate().onStop();
	}

	/**
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		delegate().onDestroy();
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * Interface for correctors used to correct/modify the view hierarchy of {@link SettingsBaseActivity}
	 * so it matches actual Material design guidelines.
	 */
	private interface LayoutCorrector {

		/**
		 * Performs correction/modification of the given <var>layout</var>.
		 *
		 * @param layout The root layout to be corrected/modified.
		 */
		void correctLayout(View layout);
	}

	/**
	 * A {@link LayoutCorrector} basic implementation that performs layout corrections related to
	 * all Android platform versions.
	 */
	private static class BasicLayoutCorrector implements LayoutCorrector {

		/**
		 */
		@Override
		public void correctLayout(View layout) {
			if (layout instanceof ViewGroup) {
				final View firstChild = ((ViewGroup) layout).getChildAt(0);
				if (firstChild instanceof ViewGroup && ((ViewGroup) firstChild).getChildCount() > 1) {
					final ViewGroup panelsContainer = (ViewGroup) firstChild;
					correctHeadersPanel(panelsContainer.getChildAt(0));
					correctPreferencesPanel(panelsContainer.getChildAt(1));
				}
			}
		}

		/**
		 * Performs correction of the given <var>headersPanelView</var>.
		 * <p>
		 * This implementation clears padding of the given headers panel view and also of the headers
		 * list view presented in view hierarchy of the panel view.
		 *
		 * @param headersPanelView The headers panel view to be corrected.
		 */
		void correctHeadersPanel(View headersPanelView) {
			headersPanelView.setPadding(0, 0, 0, 0);
			final ListView headersListView = (ListView) headersPanelView.findViewById(android.R.id.list);
			headersListView.setPadding(0, 0, 0, 0);
			headersListView.setDivider(null);
			headersListView.setDividerHeight(0);
		}

		/**
		 * Performs correction of the given <var>preferencesPanelView</var>.
		 * <p>
		 * This implementation clears padding of the given preferences panel view and also of the
		 * preferences frame view, if presented in the view hierarchy of the panel view.
		 *
		 * @param preferencesPanelView The preferences panel view to be corrected.
		 */
		void correctPreferencesPanel(View preferencesPanelView) {
			preferencesPanelView.setPadding(0, 0, 0, 0);
			if (preferencesPanelView instanceof ViewGroup && ((ViewGroup) preferencesPanelView).getChildCount() > 0) {
				final ViewGroup preferencesPanelViewGroup = (ViewGroup) preferencesPanelView;
				final View preferencesFrameView = preferencesPanelViewGroup.getChildAt(preferencesPanelViewGroup.getChildCount() - 1);
				if (preferencesFrameView != null) {
					((ViewGroup) preferencesFrameView).setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

						/**
						 */
						@Override
						public void onChildViewAdded(View parent, View child) {
							preferencesFrameView.setPadding(0, 0, 0, 0);
						}

						/**
						 */
						@Override
						public void onChildViewRemoved(View parent, View child) {
							// Ignored.
						}
					});
				}
			}
		}
	}
}
