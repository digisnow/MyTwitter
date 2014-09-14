/**
 * TL�̏ڍׂ��擾����N���X
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

	/** 1����msec�ŕ\�� */
	public static final long ONE_DAY_TIME_MILES = 1000 * 60 * 60 * 24;
	/** 1���Ԃ�msec�ŕ\�� */
	public static final long ONE_HOUR_TIME_MILES = 1000 * 60 * 60;
	/** 1����msec�ŕ\��  */
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
		//���[�U��
		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(item.getUser().getName());
		//���[�U���iID�j
		TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
		screenName.setText("@" + item.getUser().getScreenName());
		//�{��
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(item.getText());
		//�N���C���g��
		TextView cliant_name = (TextView) convertView.findViewById(R.id.cliant_name);
		cliant_name.setText(getDateAndClient(item));
		//�A�C�R���摜
		SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
		icon.setImageUrl(item.getUser().getProfileImageURL());

		return convertView;
	}

	/**
	 * �c�C�[�g�̓��t�ƃN���C�A���g�̕�������擾����B
	 * @param status �c�C�[�g
	 * @return ���t���N���C�A���g�̕�����
	 */
	private String getDateAndClient(Status status){
		//String dateText = sdf.format(status.getCreatedAt());
		//�c�C�[�g����
		Date tweetTime = status.getCreatedAt();
		//���ݎ���
		Date currentTime = new Date(System.currentTimeMillis());
		String dateText = differenceDays(tweetTime, currentTime);
		//���[�U�̎g�p�N���C�A���g
		String client	= getClientName( status.getSource() );
		return " via " + client + " " + dateText;
	}

	/**
	 * a�^�O�ň͂܂ꂽ�N���C�A���g������A�N���C�A���g��������؂���
	 * @param source a�^�O�ň͂܂ꂽ�N���C�A���g��
	 * @return �N���C�A���g��
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
	 * ��̓��t�̍��������߂�
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
			//1���ȏ�O�Ȃ̂œ��t��\��
			return sdf.format(date1);
		} else {
			//1���ȓ��Ȃ̂Ŏ��Ԃ�����\��
			long diffHours = (datetime2 - datetime1) / ONE_HOUR_TIME_MILES;
			if( diffHours > 0) {
				//1���Ԉȏ�O�Ȃ̎��Ԃŕ\��
				return String.valueOf(diffHours) + "���ԑO";
			} else {
				//1���Ԉȓ��Ȃ̂ŕ��\��
				long diffMinutes = (datetime2 - datetime1) / ONE_MINUTE_TIME_MILES;
				return String.valueOf(diffMinutes) + "���O";
			}

		}
	}

}
