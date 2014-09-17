/**
 * メイン画面（TL）を表示するクラス
 * @author yoshiaki_tajima
 */

//返信やりツイート、お気に入り機能等は未実装
//あとTLの取得をボタンじゃなくて引っ張ってできるあれにしたいけど未実装

package in.android.mytwitter;

import static in.android.mytwitter.Constants.*;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ListActivity {


    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    //private int selectPos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //selectPos = 0;
        if(!TwitterUtils.hasAccessToken(this)) {
            //認証情報がなければ認証へ
            Intent intent = new Intent(this, TwitterOauthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //認証されているならTLを取得する
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }
    }

    //メニューボタン
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 下のボタン群のメソッド
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            //更新ボタン
            reloadTimeLine();
            //課題：このままだと取得できない場合も表示される
            showToast("タイムラインを取得しました");
            return true;
        case R.id.menu_tweet:
            //ツイート画面へ
            Intent intent = new Intent(this, TweetActivity.class);
            startActivity(intent);
            return true;
        case R.id.tweet_form:
            //定型文ツイートボタン
            FormTweetDialog();
            return true;
        case R.id.tweet_form_set:
            //定型文設定ボタン
            Intent intent2 = new Intent(this, TweetEdit.class);
            startActivity(intent2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DBから登録されている定型文を取得するメソッド
     * @return textFrom 登録されているテキスト
     */
    private String[] getTweetText(){
        String[] textForm = new String[TEXTFORM_MAX]; //DBに登録されている文字列
        DBManager helper = new DBManager(MainActivity.this);
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
     * 定型文ツイートを選択したときのメソッド
     * 
     */
    private void FormTweetDialog(){
        String textForm[] = getTweetText();
        if(textForm[0] == null) {
            //なにも登録されていない場合
            showToast("定型文が登録されていません。");
        } else {
            String[] items = new String[TEXTFORM_MAX + 1];
            int i= 0;
            while(i < textForm.length && textForm[i].length() != 0 && textForm[i] != null){
                items[i] = textForm[i];
                i++;
            }
            for(i = 0; i < TEXTFORM_MAX + 1; i++){
                if(items[i] == null)items[i] = "";
            }
            items[5] = "やっぱりやめる";

            new AlertDialog.Builder(MainActivity.this)
            .setTitle("なにつぶやく？")
            .setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    selectList(item);
                }
            }).show();

        }

    }

    /**
     * 定型文のリストを選択したときの動作
     * 
     */
    private void selectList(int item) {
        if(getTweetText()[item] == null || getTweetText()[item].length() == 0 || item == TEXTFORM_MAX){
            //nothing
        } else {
            tweet(getTweetText()[item]);
        }
    }


    /**
     * リストのアイテム（ツイート）がクリックされたときのコールバック。
     * ダイアログを表示し、Reply,Retweet,Favoriteへ飛ぶ。
     * @param l リストビュー
     * @param v 
     * @param position クリックされたアイテムの位置
     * @param id 
     */

    //  ふぁぼとかりついーととかそのあたりの動き
    //  とりあえず後回し
    //	@Override
    //	protected void onListItemClick(ListView l, View v, int position, long id){
    //
    //		selectPos = position;
    //
    //		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //		builder.setIcon(R.drawable.icon)
    //		.setTitle("Select Your Action")
    //		.setMessage("ついーと内容")
    //		.setItems(R.string.string_array, new DialogInterface.OnClickListener() {
    //
    //			/**
    //			 * ダイアログのアイテムがクリックされた時の処理
    //			 * @param dialog
    //			 * @param which どのアイテムがクリックされたか
    //			 */
    //			public void onClick(DialogInterface dialog, int which) {
    //
    //				dialog.dismiss();
    //
    //				Intent intent = null;
    //				Status status = tl.get(selectPos);
    //				String screenName 	= status.getUser().getScreenName();
    //				String text 		= status.getText();
    //				long userId			= status.getUser().getId();
    //				long statusId 		= status.getId();
    //
    //				switch(which){
    //				case 0:	//Reply
    //					intent = new Intent(MainActivity.this, TweetActivity.class);
    //					intent.putExtra("mode",			"Reply");
    //					intent.putExtra("statusId", 	statusId);
    //					intent.putExtra("screenName", 	screenName);
    //					intent.putExtra("text", 		text);
    //					Log.v("test", Long.toString(statusId));
    //					Log.v("test", screenName);
    //					startActivity(intent);
    //					break;
    //				case 1:	//Retweet
    //					intent = new Intent(MainActivity.this, TweetActivity.class);
    //					intent.putExtra("mode",			"Retweet");
    //					intent.putExtra("statusId", 	statusId);
    //					intent.putExtra("screenName", 	screenName);
    //					intent.putExtra("text", 		text);
    //					Log.v("test", Long.toString(statusId));
    //					Log.v("test", screenName);
    //					startActivity(intent);
    //					break;
    //				case 2: //fav
    //					MainActivity dtl = MainActivity.this;
    //					TwitterApplication app = (TwitterApplication)dtl.getApplication();
    //					Twitter twitter = app.getTwitter();
    //					try{	
    //						twitter.createFavorite(statusId);
    //					}catch(TwitterException e){
    //						e.printStackTrace();
    //						new AlertDialog.Builder(dtl).setTitle("Error").setMessage("Favorite error").create().show();
    //					}
    //					break;
    //				case 3:	//profile
    //
    //					int following = status.getUser().getFriendsCount();
    //					int followed  = status.getUser().getFollowersCount();
    //					String url = status.getUser().getProfileImageURL().toString();
    //					String place = status.getUser().getLocation();
    //					String bio = status.getUser().getDescription();
    //					String fullname = status.getUser().getName();
    //
    //					intent = new Intent(MainActivity.this, ProfileActivity.class);
    //					intent.putExtra("userId", userId);
    //					intent.putExtra("screenName", screenName);
    //					intent.putExtra("fullname", fullname);
    //					intent.putExtra("following", following);
    //					intent.putExtra("followed", followed);
    //					intent.putExtra("url", url);
    //					intent.putExtra("place", place);
    //					intent.putExtra("bio", bio);
    //
    //					startActivity(intent);
    //					break;
    //				default:	//other(cancel)
    //					dialog.cancel();
    //					break;
    //				}
    //
    //			}
    //		});
    //
    //		builder.create().show();
    //
    //	}


    /**
     * ツイートするメソッド
     * @param tweet_form つぶやくテキスト
     */
    private void tweet(String tweet_form) {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    showToast("ツイートが完了しました！");
                    reloadTimeLine();
                } else {
                    showToast("ツイートに失敗しました。。。");
                }
            }
        };
        task.execute(tweet_form);
    }

    /**
     * タイムラインの更新
     */
    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        task.execute();
    }

    /**
     * ダイアログ
     * @param text
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
