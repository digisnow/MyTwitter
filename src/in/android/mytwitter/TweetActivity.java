/**
 * �ʏ�̂Ԃ₫������N���X
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

    /** ���e�������̃e�L�X�g */
    private TextView tv;
    /** ���v���C��̃e�L�X�g */
    private TextView tv2;
    /** Tweet�{�^�� */
    private Button tweetButton;
    /** �摜�Y�t�{�^�� */
    private Button pictButton;
    /** �߂�{�^�� */
    private Button backButton;
    /** ���v���C��̃��[�U�� */
    private String replyToScreenName;
    /** ���v���C��̖{�� */
    private String replyToText;



    //private final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT; 
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //���C�A�E�g
        //�ۑ�F���󂱂��������C�A�E�g��java�ō쐬���Ă���̂Ō�ŊO��������\��
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //���b�Z�[�W
        TextView textView1 = new TextView(this);
        textView1.setText("�Ԃ₯");
        layout.addView(textView1);

        //���e���e
        mInputText = new EditText(this);
        InputFilter[] filter = new InputFilter[1];
        mInputText.setHint(WHAT_DO_YOU_DO);
        filter[0] = new InputFilter.LengthFilter(TWEET_MAX_LENGTH);
        mInputText.setFilters(filter);
        mInputText.addTextChangedListener(new TweetTextWatcher());
        layout.addView(mInputText);

        //���݂̕������i�����\����140�j
        tv = new TextView(this);
        tv.setText(TWEET_MAX_LENGTH);
        layout.addView(tv);

        //���v���C��̖{��
        tv2 = new TextView(this);
        tv2.setText("");
        layout.addView(tv2);

        //�߂�{�^��
        backButton = new Button(this);
        backButton.setText(BACK);
        layout.addView(backButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        //�摜�Y�t�{�^��
        pictButton = new Button(this);
        pictButton.setText(PICTURE);
        layout.addView(pictButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        //Tweet�{�^��
        tweetButton = new Button(this);
        tweetButton.setText(TWEET);
        layout.addView(tweetButton,  new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));


        mTwitter = TwitterUtils.getTwitterInstance(this);

        //Bundle��null����Ȃ��Ƃ��́Areply/retweet���[�h
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
        //�ʏ��tweet�̂Ƃ�
        else{
            replyToScreenName       = null;
            replyToText             = null;
        }

        //�߂�{�^�����������Ƃ�
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //�摜�{�^�����������Ƃ�
        pictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });

        //�Ԃ₭�{�^�����������Ƃ�
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });


    }


    /**
     * �c�C�[�g���郁�\�b�h
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
     * ���e�e�L�X�g���Ď����A���݂̕�������\������C���i�[�N���X
     *
     */
    class TweetTextWatcher implements TextWatcher {
        /**
         * �e�L�X�g���ύX���ꂽ�Ƃ��A�c�蕶�����̕\�����X�V����
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
            // TODO �����������ꂽ���\�b�h�E�X�^�u
        }
        @Override
        public void afterTextChanged(Editable s) {
            // TODO �����������ꂽ���\�b�h�E�X�^�u
        }
    }
}
