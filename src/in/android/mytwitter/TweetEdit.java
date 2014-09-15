/**
 * ��^����o�^�A�폜����N���X
 * @author yoshiaki_tajima
 */


package in.android.mytwitter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TweetEdit extends Activity {


	/**
	 * �����\��
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//�X�[�p�[�N���X��onCreate���\�b�h�Ăяo��
		super.onCreate(savedInstanceState);
		//���C�A�E�g�ݒ�
		setContentView(R.layout.activity_tweet_edit);

		//���͕�����
		final EditText tweetEdit = (EditText)findViewById(R.id.tweet_edit_text);

		DBManager helper = new DBManager(TweetEdit.this);
		final SQLiteDatabase db = helper.getWritableDatabase();


		//�߂�{�^��
		Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		//�o�^�{�^���̃N���b�N���X�i�[�ݒ�
		Button insertButton = (Button)findViewById(R.id.insert_button);
		insertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�����񂪓��͂���Ă��Ȃ��ꍇ��140���𒴂��Ă���ꍇ�͓o�^���Ȃ�
				String edit = tweetEdit.getText().toString();
				if(edit != null && edit.length() > 0 && edit.length() < 140){
					//�o�^����Ă����^�����擾
					String textForm[] = getTweetText();
					int i = 0;
					for(int j = 0; j < 5; j++){
						if(textForm[j] != null)i++;
					}
					if(i < 5) {
						//�f�[�^�o�^
						try {

							//�o�^�f�[�^�ݒ�
							ContentValues val = new ContentValues();
							val.put("tweettext", edit);
							val.put("tweetcount", "0");
							val.put("tweettime", String.valueOf(System.currentTimeMillis()));
							//�f�[�^�o�^
							db.insert("TWEETFORM", "�Ȃɂ�", val);

							showToast("�u" + edit + "�v" +"��o�^���܂����I");
						} catch(Exception e) {
							showToast("�f�[�^�̓o�^�Ɏ��s���܂���");
						}
					} else {
						showToast("���ł�5�o�^����Ă��܂�");
					}
				}else if(edit.length() > 140) {
					showToast("140���ȓ��ɂ��Ă�������");
				} else {
					showToast("�o�^��������������͂��Ă�������");
				}
			}
		});

		//�폜�{�^��
		Button deleteButton = (Button)findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�o�^����Ă����^�����擾
				String textForm[] = getTweetText();
				if(textForm[0] == null) {
					//�Ȃɂ��o�^����Ă��Ȃ��ꍇ
					showToast("��^�����o�^����Ă��܂���B");
				} else {
					String[] items = new String[6];
					int i= 0;
					while(i < textForm.length && textForm[i].length() != 0 && textForm[i] != null){
						items[i] = textForm[i];
						i++;
					}

					for(i = 0; i < 6; i++){
						if(items[i] == null)items[i] = "";
					}

					new AlertDialog.Builder(TweetEdit.this)
					.setTitle("�ǂ�������H")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(getTweetText()[item] == null || getTweetText()[item].length() == 0){
								//nothing
							} else {
								//�I�����ꂽ���̂̍폜
								db.delete( "TWEETFORM", 
										"tweettext = ?",
										new String[]{ "" + getTweetText()[item] } );
								showToast("�폜������I");
							}
						}
					}).show();

				}


			}
		});

		//�m�F�{�^��
		Button checkButton = (Button)findViewById(R.id.check_button);
		checkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�o�^����Ă����^�����擾
				String textForm[] = getTweetText();
				if(textForm[0] == null) {
					//�Ȃɂ��o�^����Ă��Ȃ��ꍇ
					showToast("��^�����o�^����Ă��܂���B");
				} else {
					String[] items = new String[6];
					int i= 0;
					while(i < textForm.length && textForm[i].length() != 0 && textForm[i] != null){
						items[i] = textForm[i];
						i++;
					}

					for(i = 0; i < 6; i++){
						if(items[i] == null)items[i] = "";
					}

					new AlertDialog.Builder(TweetEdit.this)
					.setTitle("�o�^��^���ꗗ")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							//nothing
						}
					}).show();

				}


			}
		});
	}

	/**
	 * DB����o�^����Ă����^�����擾���郁�\�b�h
	 * @return textFrom �o�^����Ă���e�L�X�g
	 */
	private String[] getTweetText(){
		String[] textForm = new String[5]; //DB�ɓo�^����Ă��镶����
		DBManager helper = new DBManager(TweetEdit.this);
		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor c = db.query("TWEETFORM", new String[] {"tweettext", "tweetcount", "tweettime"}, null, null, null, null, null);
		int i = 0;
		boolean mov = c.moveToFirst();
		List<TweetTextForm> list = new ArrayList<TweetTextForm>();
		while(mov) {
			list.add(new TweetTextForm(c.getString(0), c.getString(1), c.getString(2)));
			i++;
			mov = c.moveToNext();
		}
		for(int j = 0; j < i; j++){
			textForm[j] = list.get(j).getTweetText();
		}

		return textForm;
	}

	/**
	 * �_�C�A���O
	 * @param text �_�C�A���O�ɕ\�����������e�L�X�g
	 */
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
