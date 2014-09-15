/**
 * 定型文を登録、削除するクラス
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
	 * 初期表示
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//スーパークラスのonCreateメソッド呼び出し
		super.onCreate(savedInstanceState);
		//レイアウト設定
		setContentView(R.layout.activity_tweet_edit);

		//入力文字列
		final EditText tweetEdit = (EditText)findViewById(R.id.tweet_edit_text);

		DBManager helper = new DBManager(TweetEdit.this);
		final SQLiteDatabase db = helper.getWritableDatabase();


		//戻るボタン
		Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		//登録ボタンのクリックリスナー設定
		Button insertButton = (Button)findViewById(R.id.insert_button);
		insertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//文字列が入力されていない場合と140字を超えている場合は登録しない
				String edit = tweetEdit.getText().toString();
				if(edit != null && edit.length() > 0 && edit.length() < 140){
					//登録されている定型文を取得
					String textForm[] = getTweetText();
					int i = 0;
					for(int j = 0; j < 5; j++){
						if(textForm[j] != null)i++;
					}
					if(i < 5) {
						//データ登録
						try {

							//登録データ設定
							ContentValues val = new ContentValues();
							val.put("tweettext", edit);
							val.put("tweetcount", "0");
							val.put("tweettime", String.valueOf(System.currentTimeMillis()));
							//データ登録
							db.insert("TWEETFORM", "なにか", val);

							showToast("「" + edit + "」" +"を登録しました！");
						} catch(Exception e) {
							showToast("データの登録に失敗しました");
						}
					} else {
						showToast("すでに5つ登録されています");
					}
				}else if(edit.length() > 140) {
					showToast("140字以内にしてください");
				} else {
					showToast("登録したい文字を入力してください");
				}
			}
		});

		//削除ボタン
		Button deleteButton = (Button)findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//登録されている定型文を取得
				String textForm[] = getTweetText();
				if(textForm[0] == null) {
					//なにも登録されていない場合
					showToast("定型文が登録されていません。");
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
					.setTitle("どれを消す？")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(getTweetText()[item] == null || getTweetText()[item].length() == 0){
								//nothing
							} else {
								//選択されたものの削除
								db.delete( "TWEETFORM", 
										"tweettext = ?",
										new String[]{ "" + getTweetText()[item] } );
								showToast("削除したよ！");
							}
						}
					}).show();

				}


			}
		});

		//確認ボタン
		Button checkButton = (Button)findViewById(R.id.check_button);
		checkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//登録されている定型文を取得
				String textForm[] = getTweetText();
				if(textForm[0] == null) {
					//なにも登録されていない場合
					showToast("定型文が登録されていません。");
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
					.setTitle("登録定型文一覧")
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
	 * DBから登録されている定型文を取得するメソッド
	 * @return textFrom 登録されているテキスト
	 */
	private String[] getTweetText(){
		String[] textForm = new String[5]; //DBに登録されている文字列
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
	 * ダイアログ
	 * @param text ダイアログに表示させたいテキスト
	 */
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
