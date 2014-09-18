/**
 * 通常のつぶやきをするクラス
 * @author yoshiaki_tajima
 */

package in.android.mytwitter;

import static in.android.mytwitter.Constants.*;
import static in.android.mytwitter.ConstantMessages.*;

import java.io.File;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TweetActivity extends Activity {

    private EditText mInputText;
    private Twitter mTwitter;

    /** 投稿文字数のテキスト */
    private TextView tv;
    /** リプライ先のテキスト */
    private TextView tv2;
    /** Tweetボタン */
    private Button tweetButton;
    /** 画像添付ボタン */
    private Button pictButton;
    /** 戻るボタン */
    private Button backButton;
    /** リプライ先のユーザ名 */
    private String replyToScreenName;
    /** リプライ先の本文 */
    private String replyToText;



    //private final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT; 
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //レイアウト
        //課題：現状ここだけレイアウトをjavaで作成しているので後で外だしする予定
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //メッセージ
        TextView textView1 = new TextView(this);
        textView1.setText("つぶやけ");
        layout.addView(textView1);

        //投稿内容
        mInputText = new EditText(this);
        InputFilter[] filter = new InputFilter[1];
        mInputText.setHint(WHAT_DO_YOU_DO);
        filter[0] = new InputFilter.LengthFilter(TWEET_MAX_LENGTH);
        mInputText.setFilters(filter);
        mInputText.addTextChangedListener(new TweetTextWatcher());
        layout.addView(mInputText);

        //現在の文字数（初期表示は140）
        tv = new TextView(this);
        tv.setText(TWEET_MAX_LENGTH);
        layout.addView(tv);

        //リプライ先の本文
        tv2 = new TextView(this);
        tv2.setText("");
        layout.addView(tv2);

        //戻るボタン
        backButton = new Button(this);
        backButton.setText(BACK);
        layout.addView(backButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        //画像添付ボタン
        pictButton = new Button(this);
        pictButton.setText(PICTURE);
        layout.addView(pictButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        //Tweetボタン
        tweetButton = new Button(this);
        tweetButton.setText(TWEET);
        layout.addView(tweetButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));


        mTwitter = TwitterUtils.getTwitterInstance(this);

        //Bundleがnullじゃないときは、reply/retweetモード
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String mode             = bundle.getString("mode");
            replyToScreenName       = bundle.getString("screenName");
            replyToText                     = bundle.getString("text");

            if(mode.equals("Reply")){
                mInputText.setText("@" + replyToScreenName + " ");
                tv2.setText(replyToText);
            }else if(mode.equals("Retweet")){
                mInputText.setText("RT @" + replyToScreenName + ": " + replyToText);
                tv2.setText(replyToText);
            }else{

            }
        }
        //通常のtweetのとき
        else{
            replyToScreenName       = null;
            replyToText             = null;
        }

        //戻るボタンを押したとき
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //画像ボタンを押したとき
        pictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });

        //つぶやくボタンを押したとき
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });


    }


    /**
     * ツイートするメソッド
     * 
     */
    private void tweet() {
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
                    showToast(COMPLETE_TWEET);
                    finish();
                } else {
                    showToast(MISSING_TWEET);
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }
    private void showToast(String text) {
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
    }



    /**
     * 投稿テキストを監視し、現在の文字数を表示するインナークラス
     *
     */
    class TweetTextWatcher implements TextWatcher {
        /**
         * テキストが変更されたとき、残り文字数の表示を更新する
         * @param s 
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int remain = TWEET_MAX_LENGTH - mInputText.getText().length();
            tv.setText(Integer.toString(remain));
            if(remain < 0){
                tv.setTextColor(Color.RED);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO 自動生成されたメソッド・スタブ
        }
        @Override
        public void afterTextChanged(Editable s) {
            // TODO 自動生成されたメソッド・スタブ
        }
    }
}
