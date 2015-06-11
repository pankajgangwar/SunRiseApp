package iriemo.bangaloreweather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ForecastAdapter extends CursorAdapter {

	private final int VIEW_TYPE_TODAY = 0;
	private final int VIEW_TYPE_FUTURE_DAY = 1;
	private boolean mUseTodayLayout;
	
	public ForecastAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
	}
	
	public void setUseTodayLayout(boolean useTodayLayout) {
		mUseTodayLayout = useTodayLayout;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		
		int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
		
		int viewType = getItemViewType(cursor.getPosition());
		
		Log.i("Forecast Adapter : ", "Weather Id:  "+weatherId);

        int fallbackIconId;

		switch (viewType) {
		
		case VIEW_TYPE_TODAY: {
//			viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            fallbackIconId = Utility.getArtResourceForWeatherCondition(weatherId);

			break;
		}
			
//		case VIEW_TYPE_FUTURE_DAY: {
            default: {
//			viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
            fallbackIconId = Utility.getIconResourceForWeatherCondition(weatherId);
			break;
		}

		/*default:
			break;*/
		}

        Glide.with(mContext)
                .load(Utility.getArtUrlForWeatherCondition(mContext,weatherId))
                .error(fallbackIconId)
                .crossFade()
                .into(viewHolder.iconView);
		
		
		String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
		
		viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));
		
		String forecastDescription = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
		
		viewHolder.descriptionView.setText(forecastDescription);
		viewHolder.iconView.setContentDescription(forecastDescription);
		
		boolean isMetric = Utility.isMetric(context);
		
		float highTemp  = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
		
		viewHolder.highTempView.setText(Utility.formatTemperature(context,highTemp, isMetric));
		
		float lowTemp  = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
		
		viewHolder.lowTempView.setText(Utility.formatTemperature(context,lowTemp, isMetric));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		int viewType = getItemViewType(cursor.getPosition());
		int layoutId = -1;
		
		if(viewType == VIEW_TYPE_TODAY) {
			layoutId = R.layout.list_item_forecast_today;
		} else if(viewType == VIEW_TYPE_FUTURE_DAY) {
			layoutId = R.layout.list_item_forecast;
		}
		
		View view =LayoutInflater.from(context).inflate(layoutId, parent, false); 
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);
		return view;
	}
	
	
	public static class ViewHolder {
		public final ImageView iconView;
		public final TextView dateView;
		public final TextView descriptionView;
		public final TextView highTempView;
		public final TextView lowTempView;
		
		public ViewHolder(View view) {
			iconView = (ImageView)view.findViewById(R.id.list_item_icon);
			dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
			descriptionView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
			highTempView = (TextView)view.findViewById(R.id.list_item_high_textview);
			lowTempView = (TextView)view.findViewById(R.id.list_item_low_textview);
		}
	}

}
