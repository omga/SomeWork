package se.anyro.nfc_reader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.otentico.android.model.DrawerItem;
import java.util.List;

public class DrawerAdapter extends BaseAdapter {
	
	private List<DrawerItem> mDrawerItems;
	private LayoutInflater mInflater;
	private final boolean mIsFirstType; //Choose between two types of list items
	
	public DrawerAdapter(Context context, List<DrawerItem> items, boolean isFirstType) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDrawerItems = items;
		mIsFirstType = isFirstType;
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDrawerItems.get(position).getTag();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view_item_navigation_drawer_1, parent, false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DrawerItem item = mDrawerItems.get(position);
		holder.icon.setImageResource(item.getIcon());
        Log.d("draweradapter","icon: "+item.getIcon());
		holder.title.setText(item.getTitle());
		
		return convertView;
	}
	
	private static class ViewHolder {
		public ImageView icon;
		public /*Roboto*/TextView title;
	}
}
