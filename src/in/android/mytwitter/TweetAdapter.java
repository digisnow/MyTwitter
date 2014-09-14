/**
 * TLの詳細を取得するクラス
 * @author yoshiaki_tajima
 */

package in.android.mytwitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class TweetAdapter extends ArrayAdapter<Status> {

	private LayoutInflater mInflater;

	/** 1日をmsecで表示 */
	public static final long ONE_DAY_TIME_MILES = 1000 * 60 * 60 * 24;
	/** 1時間をmsecで表示 */
	public static final long ONE_HOUR_TIME_MILES = 1000 * 60 * 60;
	/** 1分をmsecで表示  */
	public static final long ONE_MINUTE_TIME_MILES = 1000 * 60;

	public TweetAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_tweet, null);
		}
		Status item = getItem(position);
		//ユーザ名
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(item.getUser().getName());
		//ユーザ名（ID）
		TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
		screenName.setText("@" + item.getUser().getScreenName());
		//本文
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(item.getText());
		//クライント名
		TextView cliant_name = (TextView) convertView.findViewById(R.id.cliant_name);
		cliant_name.setText(getDateAndClient(item));
		//アイコン画像
		SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
		icon.setImageUrl(item.getUser().getProfileImageURL());

		return convertView;
	}

	/**
	 * ツイートの日付とクライアントの文字列を取得する。
	 * @param status ツイート
	 * @return 日付＆クライアントの文字列
	 */
	private String getDateAndClient(Status status){
		//String dateText = sdf.format(status.getCreatedAt());
		//ツイート時間
		Date tweetTime = status.getCreatedAt();
		//現在時刻
		Date currentTime = new Date(System.currentTimeMillis());
		String dateText = differenceDays(tweetTime, currentTime);
		//ユーザの使用クライアント
		String client	= getClientName( status.getSource() );
		return " via " + client + " " + dateText;
	}

	/**
	 * aタグで囲まれたクライアント名から、クライアント名だけを切り取る
	 * @param source aタグで囲まれたクライアント名
	 * @return クライアント名
	 */
	private String getClientName(String source){
		String[] tokens = source.split("[<>]");
		if(tokens.length > 1){
			return tokens[2];
		}else{
			return tokens[0];
		}
	}

	/**
	 * 二つの日付の差分を求める
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static String differenceDays(Date date1,Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE);
		long datetime1 = date1.getTime();
		long datetime2 = date2.getTime();
		long diffDays = (datetime2 - datetime1) / ONE_DAY_TIME_MILES;
		if (diffDays > 1) {
			//1日以上前なので日付を表示
			return sdf.format(date1);
		} else {
			//1日以内なので時間か分を表示
			long diffHours = (datetime2 - datetime1) / ONE_HOUR_TIME_MILES;
			if( diffHours > 0) {
				//1時間以上前なの時間で表示
				return String.valueOf(diffHours) + "時間前";
			} else {
				//1時間以内なので分表示
				long diffMinutes = (datetime2 - datetime1) / ONE_MINUTE_TIME_MILES;
				return String.valueOf(diffMinutes) + "分前";
			}

		}
	}

}
