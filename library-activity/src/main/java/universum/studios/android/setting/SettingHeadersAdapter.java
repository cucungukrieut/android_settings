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
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import universum.studios.android.ui.util.ResourceUtils;
import universum.studios.android.widget.adapter.SimpleAdapter;
import universum.studios.android.widget.adapter.ViewHolder;

/**
 * A {@link SimpleAdapter} implementation used to provide data set of {@link Item Items} for adapter
 * view that displays a list of setting preferences in the context of {@link SettingsBaseActivity}.
 * <p>
 * This adapter uses the following view types to provide views for its data set of items:
 * <ul>
 * <li>{@link #VIEW_TYPE_CATEGORY} - view for the category header that has no fragment nor intent associated,</li>
 * <li>{@link #VIEW_TYPE_CATEGORY_DIVIDER} - view for divider between category headers,</li>
 * <li>{@link #VIEW_TYPE_HEADER} - view for the selectable header.</li>
 * </ul>
 * <p>
 * Data set of items with associated headers may be supplied via {@link #SettingHeadersAdapter(Context, List)}
 * constructor or changed via {@link #changeHeaders(List)}. The current data set of transformed items
 * may be obtained via {@link #getItems()}. A specific item at a desired position may be obtained
 * via {@link #getItem(int)} and its associated header via {@link Item#getHeader() getItem(int).getHeader()}.
 *
 * @author Martin Albedinsky
 */
public class SettingHeadersAdapter extends SimpleAdapter<SettingHeadersAdapter.Item, ViewHolder> {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "SettingHeadersAdapter";

	/**
	 * Count of view types that this adapter may provide.
	 */
	private static final int VIEW_TYPES_COUNT = 3;

	/**
	 * View type for the category item.
	 */
	public static final int VIEW_TYPE_CATEGORY = 0;

	/**
	 * View type for the category divider.
	 */
	public static final int VIEW_TYPE_CATEGORY_DIVIDER = 1;

	/**
	 * View type for the header item.
	 * <p>
	 * This is only view type that is by default enabled by this adapter.
	 *
	 * @see #isEnabled(int)
	 */
	public static final int VIEW_TYPE_HEADER = 2;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Boolean flag indicating whether this adapter should treat icon resource specified for each
	 * header item via {@link PreferenceActivity.Header#iconRes} as vector drawable or as standard
	 * drawable.
	 *
	 * @see #onBindViewHolder(ViewHolder, int)
	 */
	private boolean mUseVectorIcons;

	/**
	 * List containing headers supplied to this adapter via {@link #changeHeaders(List)}.
	 */
	private List<PreferenceActivity.Header> mHeaders;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of SettingHeadersAdapter with empty data set.
	 *
	 * @see SimpleAdapter#SimpleAdapter(Context)
	 * @see #SettingHeadersAdapter(Context, List)
	 */
	public SettingHeadersAdapter(@NonNull Context context) {
		super(context);
	}

	/**
	 * Same as {@link #SettingHeadersAdapter(Context, List)} for array of headers.
	 */
	public SettingHeadersAdapter(@NonNull Context context, @NonNull PreferenceActivity.Header[] headers) {
		this(context, Arrays.asList(headers));
	}

	/**
	 * Creates a new instance of SettingHeadersAdapter with the initial data set of headers.
	 *
	 * @param context Context in which will be the new adapter used.
	 * @param headers Initial data set of headers.
	 * @see #changeHeaders(List)
	 * @see #getItems()
	 */
	public SettingHeadersAdapter(@NonNull Context context, @NonNull List<PreferenceActivity.Header> headers) {
		super(context);
		changeHeaders(headers);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a boolean flag indicating whether this adapter should treat icon parameter specified
	 * via {@link PreferenceActivity.Header#iconRes} as resource of vector drawable or as resource
	 * of standard drawable.
	 *
	 * @param useVectorIcons {@code True} to apply icon resources of items as vector drawables,
	 *                       {@code false} as standard drawables.
	 */
	public void setUseVectorIcons(boolean useVectorIcons) {
		if (mUseVectorIcons != useVectorIcons) {
			this.mUseVectorIcons = useVectorIcons;
			notifyDataSetChanged();
		}
	}

	/**
	 * @throws UnsupportedOperationException Use {@link #changeHeaders(List)} instead.
	 */
	@Override
	public final void changeItems(@Nullable List<Item> items) {
		throw new UnsupportedOperationException("Cannot change items of SettingHeadersAdapter directly! Use changeHeaders(...) instead.");
	}

	/**
	 * Same as {@link #swapHeaders(List)} where the old headers are ignored.
	 */
	public void changeHeaders(@Nullable List<PreferenceActivity.Header> headers) {
		swapHeaders(headers);
	}

	/**
	 * Swaps the current data set of this adapter for the given <var>headers</var>.
	 * <p>
	 * <b>Note</b>, that as this adapter uses multiple view types to present its data set, the specified
	 * headers are transformed into data set of {@link Item Items}. The items data set may be obtained
	 * via {@link #getItems()} or a specific item at a desired position via {@link #getItem(int)}.
	 * {@link PreferenceActivity.Header Header} associated with an item at a specific position may
	 * be obtained via {@link Item#getHeader() getItem(int).getHeader()}.
	 *
	 * @param headers The headers for which to create a new data set. May be {@code null} to clear
	 *                the current data set.
	 * @see #changeHeaders(List)
	 */
	@Nullable
	public List<PreferenceActivity.Header> swapHeaders(@Nullable List<PreferenceActivity.Header> headers) {
		final List<PreferenceActivity.Header> oldHeaders = mHeaders;
		this.mHeaders = headers;
		if (headers != null && !headers.isEmpty()) {
			super.swapItems(createItemsFromHeaders(headers));
		} else {
			super.swapItems(Collections.<Item>emptyList());
		}
		return oldHeaders;
	}

	/**
	 * Creates a data set of {@link Item items} from the given list of <var>headers</var>.
	 * <p>
	 * This method resolves the proper view type for each item and also whether a divider should be
	 * shown for item depending on a position within the headers list and on how a particular item
	 * is surrounded by header that is type of category header.
	 *
	 * @param headers The headers from which to create items for this adapter.
	 * @return List of items, where each item, except divider item, will have the corresponding header
	 * associated.
	 * @see #isCategoryHeader(PreferenceActivity.Header)
	 */
	private static List<Item> createItemsFromHeaders(List<PreferenceActivity.Header> headers) {
		final List<Item> items = new ArrayList<>(headers.size());
		final int headersCount = headers.size();
		for (int i = 0; i < headersCount; i++) {
			final PreferenceActivity.Header header = headers.get(i);
			if (isCategoryHeader(header)) {
				// Category header found.
				if (i > 0) {
					// Add category divider before each new category.
					items.add(new Item(VIEW_TYPE_CATEGORY_DIVIDER, null));
				}
				items.add(new Item(VIEW_TYPE_CATEGORY, header));
			} else {
				final Item item = new Item(VIEW_TYPE_HEADER, header);
				item.showDivider = i < headersCount - 1 && !isCategoryHeader(headers.get(i + 1));
				items.add(item);
			}
		}
		// Add category divider also at the end of all items.
		items.add(new Item(VIEW_TYPE_CATEGORY_DIVIDER, null));
		return items;
	}

	/**
	 * @throws UnsupportedOperationException Use {@link #swapHeaders(List)} instead.
	 */
	@Nullable
	@Override
	public final List<Item> swapItems(@Nullable List<Item> items) {
		throw new UnsupportedOperationException("Cannot swap items of SettingHeadersAdapter directly! Use swapHeaders(...) instead.");
	}

	/**
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * Performs check whether the given <var>header</var> should be treated as category header or not.
	 *
	 * @param header The header to check.
	 * @return {@code True} if the header does not have any <b>fragment</b> nor <b>intent</b> attached.
	 * @see PreferenceActivity.Header#fragment
	 * @see PreferenceActivity.Header#intent
	 */
	private static boolean isCategoryHeader(PreferenceActivity.Header header) {
		return TextUtils.isEmpty(header.fragment) && header.intent == null;
	}

	/**
	 */
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPES_COUNT;
	}

	/**
	 */
	@Override
	public int getItemViewType(int position) {
		if (isCategoryAt(position)) return VIEW_TYPE_CATEGORY;
		if (isCategoryDividerAt(position)) return VIEW_TYPE_CATEGORY_DIVIDER;
		else return VIEW_TYPE_HEADER;
	}

	/**
	 * @return {@code True} if item at the position is header, {@code false} otherwise.
	 * @see #isHeaderAt(int)
	 */
	@Override
	public boolean isEnabled(int position) {
		return isHeaderAt(position);
	}

	/**
	 * Checks whether item at the specified <var>position</var> is a <b>header</b>.
	 *
	 * @param position The position of item to check.
	 * @return {@code True} if item at the position is header, {@code false} otherwise.
	 */
	public boolean isHeaderAt(int position) {
		return hasItemAtViewTypeOf(position, VIEW_TYPE_HEADER);
	}

	/**
	 * Checks whether item at the specified <var>position</var> is a <b>category</b>.
	 *
	 * @param position The position of item to check.
	 * @return {@code True} if item at the position is category, {@code false} otherwise.
	 * @see #isHeaderAt(int)
	 * @see #isCategoryDividerAt(int)
	 */
	public boolean isCategoryAt(int position) {
		return hasItemAtViewTypeOf(position, VIEW_TYPE_CATEGORY);
	}

	/**
	 * Checks whether item at the specified <var>position</var> is a <b>category divider</b>.
	 *
	 * @param position The position of item to check.
	 * @return {@code True} if item at the position is category divider, {@code false} otherwise.
	 * @see #isCategoryAt(int)
	 */
	public boolean isCategoryDividerAt(int position) {
		return hasItemAtViewTypeOf(position, VIEW_TYPE_CATEGORY_DIVIDER);
	}

	/**
	 * Checks whether item at the specified <var>position</var> has associated the specified
	 * <var>viewType</var>.
	 *
	 * @param position The position of item to check its view type.
	 * @param viewType View type to check if the item has associated.
	 * @return {@code True} if the item has the view type associated, {@code false} otherwise.
	 */
	private boolean hasItemAtViewTypeOf(int position, int viewType) {
		final Item item = hasItemAt(position) ? getItem(position) : null;
		return item != null && item.viewType == viewType;
	}

	/**
	 */
	@NonNull
	@Override
	protected View onCreateView(@NonNull ViewGroup parent, int position) {
		final int viewType = currentViewType();
		switch (currentViewType()) {
			case VIEW_TYPE_CATEGORY:
				return inflate(R.layout.ui_setting_category_header, parent);
			case VIEW_TYPE_CATEGORY_DIVIDER:
				return inflate(R.layout.ui_setting_category_divider, parent);
			case VIEW_TYPE_HEADER:
				return inflate(R.layout.ui_setting_header, parent);
		}
		throw unknownViewTypeAtPositionException(viewType, position);
	}

	/**
	 */
	@Nullable
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull View itemView, int position) {
		final int viewType = currentViewType();
		switch (currentViewType()) {
			case VIEW_TYPE_CATEGORY:
				return new CategoryHolder(itemView);
			case VIEW_TYPE_CATEGORY_DIVIDER:
				return new CategoryDividerHolder(itemView);
			case VIEW_TYPE_HEADER:
				return new HeaderHolder(itemView);
		}
		throw unknownViewTypeAtPositionException(viewType, position);
	}

	/**
	 */
	@Override
	@SuppressWarnings("ResourceType")
	protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
		switch (currentViewType()) {
			case VIEW_TYPE_CATEGORY:
				final PreferenceActivity.Header categoryHeader = getItem(position).header;
				((CategoryHolder) viewHolder).title.setText(categoryHeader.getTitle(mResources));
				break;
			case VIEW_TYPE_CATEGORY_DIVIDER:
				final CategoryDividerHolder dividerHolder = (CategoryDividerHolder) viewHolder;
				dividerHolder.shadowBottom.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
				break;
			case VIEW_TYPE_HEADER:
				final Item item = getItem(position);
				final HeaderHolder headerHolder = (HeaderHolder) viewHolder;
				if (item.header.iconRes != 0) {
					headerHolder.iconFrame.setVisibility(View.VISIBLE);
					if (mUseVectorIcons) {
						headerHolder.icon.setImageDrawable(ResourceUtils.getVectorDrawable(
								mResources,
								item.header.iconRes,
								mContext.getTheme()
						));
					} else {
						headerHolder.icon.setImageResource(item.header.iconRes);
					}
				} else {
					headerHolder.iconFrame.setVisibility(View.GONE);
				}
				headerHolder.title.setText(item.header.getTitle(mResources));
				final CharSequence summary = item.header.getSummary(mResources);
				if (TextUtils.isEmpty(summary)) {
					headerHolder.summary.setVisibility(View.GONE);
				} else {
					headerHolder.summary.setVisibility(View.VISIBLE);
					headerHolder.summary.setText(summary);
				}
				headerHolder.divider.setVisibility(item.showDivider ? View.VISIBLE : View.GONE);
				break;
		}
	}

	/**
	 * Creates a new instance of IllegalArgumentException informing that the specified <var>viewType</var>
	 * at the <var>position</var> is unknown type and it is not supported by default implementation.
	 *
	 * @param viewType Type of the view that is not supported.
	 * @param position Position for which the view type has been specified.
	 */
	private static IllegalArgumentException unknownViewTypeAtPositionException(int viewType, int position) {
		return new IllegalArgumentException("Unknown view type(" + viewType + ") at(" + position + ")!");
	}

	/**
	 * Inner classes ===============================================================================
	 */

	/**
	 * Item class represents a data model for {@link SettingHeadersAdapter}. Each item, except divider
	 * item, has associated header data that may be obtained via {@link #getHeader()}.
	 *
	 * @author Martin Albedinsky
	 */
	public static final class Item {

		/**
		 * Type of the view that should be inflated for this item. Should be one of view types defined
		 * in {@link SettingHeadersAdapter}.
		 */
		private final int viewType;

		/**
		 * Header data associated with this item.
		 */
		private final PreferenceActivity.Header header;

		/**
		 * Boolean flag indicating whether there should be visible divider in the view inflated for
		 * this item.
		 */
		private boolean showDivider = false;

		/**
		 * Creates a new instance of Item with the specified <var>viewType</var> and <var>header</var>
		 * data.
		 *
		 * @param viewType Type of the view that should be inflated for this item's data.
		 * @param header   The header data to be associated with the new item.
		 */
		private Item(int viewType, PreferenceActivity.Header header) {
			this.viewType = viewType;
			this.header = header;
		}

		/**
		 * Returns the header data associated with this item.
		 *
		 * @return This item's header data.
		 */
		@NonNull
		public PreferenceActivity.Header getHeader() {
			return header;
		}
	}

	/**
	 * A {@link ViewHolder} implementation for category item view of {@link SettingHeadersAdapter}.
	 *
	 * @author Martin Albedinsky
	 */
	public static class CategoryHolder extends ViewHolder {

		/**
		 * Text view that displays a category's title text.
		 */
		public final TextView title;

		/**
		 * Creates a new instance of CategoryHolder for the given <var>itemView</var>.
		 *
		 * @param itemView Instance of view to be hold by the holder.
		 */
		public CategoryHolder(@NonNull View itemView) {
			super(itemView);
			this.title = (TextView) itemView.findViewById(android.R.id.title);
		}
	}

	/**
	 * A {@link ViewHolder} implementation for category divider view of {@link SettingHeadersAdapter}.
	 *
	 * @author Martin Albedinsky
	 */
	public static class CategoryDividerHolder extends ViewHolder {

		/**
		 * View that displays a shadow at the top of divider's view hierarchy.
		 */
		public final View shadowTop;

		/**
		 * View that displays a shadow at the bottom of divider's view hierarchy.
		 */
		public final View shadowBottom;

		/**
		 * Creates a new instance of CategoryDividerHolder for the given <var>itemView</var>.
		 *
		 * @param itemView Instance of view to be hold by the holder.
		 */
		public CategoryDividerHolder(@NonNull View itemView) {
			super(itemView);
			this.shadowTop = itemView.findViewById(R.id.ui_setting_category_divider_shadow_top);
			this.shadowBottom = itemView.findViewById(R.id.ui_setting_category_divider_shadow_bottom);
		}
	}

	/**
	 * A {@link ViewHolder} implementation for header item view of {@link SettingHeadersAdapter}.
	 *
	 * @author Martin Albedinsky
	 */
	public static class HeaderHolder extends ViewHolder {

		/**
		 * Frame layout in which is placed {@link #icon} view.
		 * <p>
		 * This frame layout is used to show/hide presence of the icon.
		 */
		public final View iconFrame;

		/**
		 * Image view that displays an icon (if specified).
		 */
		public final ImageView icon;

		/**
		 * Text view that displays a title text.
		 */
		public final TextView title;

		/**
		 * Text view that displays a summary text (if specified).
		 */
		public final TextView summary;

		/**
		 * View that displays a divider at the bottom of item.
		 */
		public final View divider;

		/**
		 * Creates a new instance of HeaderHolder for the given <var>itemView</var>.
		 *
		 * @param itemView Instance of view to be hold by the holder.
		 */
		public HeaderHolder(@NonNull View itemView) {
			super(itemView);
			this.iconFrame = itemView.findViewById(R.id.icon_frame);
			this.icon = (ImageView) itemView.findViewById(android.R.id.icon);
			this.title = (TextView) itemView.findViewById(android.R.id.title);
			this.summary = (TextView) itemView.findViewById(android.R.id.summary);
			this.divider = itemView.findViewById(R.id.ui_setting_divider);
		}
	}
}
