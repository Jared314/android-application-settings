package com.bluecasedevelopment.applicationsettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ApplicationSettings extends ListActivity {
	private List<ResolveInfo> infos = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		this.requestWindowFeature(Window.FEATURE_PROGRESS);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		String packageName = b.getString("PackageName");
		String appName = b.getString("AppName");

		setContentView(R.layout.main);

		if (appName != null && !appName.equals(""))
			this.setTitle(appName + " Settings");

		infos = findActivitiesForPackage(this, packageName);

		ListAdapter adapter = new InfoAdapter(this, infos);

		this.setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ResolveInfo ri = infos.get(position);
		Intent i = new Intent();
		i.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
		this.startActivityForResult(i, 0);
	}

	private static List<ResolveInfo> findActivitiesForPackage(Context context,
			String packageName) {
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_PREFERENCE);

		final List<ResolveInfo> apps = packageManager.queryIntentActivities(
				mainIntent, 0);
		final List<ResolveInfo> matches = new ArrayList<ResolveInfo>();

		if (apps != null) {
			// Find all activities that match the packageName
			int count = apps.size();
			for (int i = 0; i < count; i++) {
				final ResolveInfo info = apps.get(i);
				final ActivityInfo activityInfo = info.activityInfo;
				if (packageName.equals(activityInfo.packageName)) {
					matches.add(info);
				}
			}
		}

		return matches;
	}

	private static class InfoAdapter extends BaseAdapter {

		public List<ResolveInfo> Infos = new ArrayList<ResolveInfo>();
		private final LayoutInflater mInflater;
		private final PackageManager pm;

		public InfoAdapter(Context c) {
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pm = c.getPackageManager();
		}

		public InfoAdapter(Context c,
				Collection<? extends ResolveInfo> applicationInfos) {
			this(c);
			Infos.addAll(applicationInfos);
		}

		@Override
		public int getCount() {
			return Infos.size();
		}

		@Override
		public Object getItem(int position) {
			return Infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return this.Infos.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ResolveInfo info = this.Infos.get(position);

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.manage_applications_item, null);

			}

			TextView name = (TextView) convertView.findViewById(R.id.app_name);
			name.setText(info.loadLabel(pm));

			ImageView icon = (ImageView) convertView
					.findViewById(R.id.app_icon);
			icon.setImageDrawable(info.loadIcon(pm));
			TextView description = (TextView) convertView
					.findViewById(R.id.app_size);
			description.setText(getClassName(info.activityInfo));

			return convertView;
		}

		private String getClassName(ActivityInfo info) {
			String result = info.name.replace(info.packageName, "");
			if (result.startsWith("."))
				result = result.substring(1);
			return result;
		}

	}
}
