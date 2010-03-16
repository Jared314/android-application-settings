package com.bluecasedevelopment.applicationsettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends ListActivity {

	private List<ApplicationInfo> infos = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		infos = convert(findActivities(this));

		ListAdapter adapter = new AppInfoAdapter(this, infos);

		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ApplicationInfo ai = infos.get(position);
		Intent i = new Intent(this, ApplicationSettings.class).putExtra(
				"PackageName", ai.packageName).putExtra("AppName",
				ai.loadLabel(this.getPackageManager()));

		this.startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.item01) {
			// TODO About Dialog
			// Dialog d = new Dialog(this);
			// d.setTitle("Something");
			// d.show();
		}

		return true;
	}

	private static List<ApplicationInfo> convert(List<ResolveInfo> infos) {
		final List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();

		final Set<ApplicationInfo> apps = new HashSet<ApplicationInfo>();
		for (ResolveInfo r : infos)
			apps.add(r.activityInfo.applicationInfo);

		result.addAll(apps);

		return result;
	}

	private static List<ResolveInfo> findActivities(Context context) {
		final PackageManager packageManager = context.getPackageManager();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_PREFERENCE);

		final List<ResolveInfo> activities = packageManager
				.queryIntentActivities(mainIntent, 0);

		return activities;
	}

	private static class AppInfoAdapter extends BaseAdapter {

		public List<ApplicationInfo> ApplicationInfos = new ArrayList<ApplicationInfo>();
		private final LayoutInflater mInflater;
		private final PackageManager pm;

		public AppInfoAdapter(Context c) {
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pm = c.getPackageManager();
		}

		public AppInfoAdapter(Context c,
				Collection<? extends ApplicationInfo> applicationInfos) {
			this(c);
			this.ApplicationInfos.addAll(applicationInfos);
		}

		@Override
		public int getCount() {
			return this.ApplicationInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return ApplicationInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return this.ApplicationInfos.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ApplicationInfo info = this.ApplicationInfos.get(position);

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
			description.setText(info.loadDescription(pm));

			return convertView;
		}

	}
}