/**
 * ���C����ʁiTL�j��\������N���X
 * @author yoshiaki_tajima
 */

//�ԐM���c�C�[�g�A���C�ɓ���@�\���͖�����
//����TL�̎擾���{�^������Ȃ��Ĉ��������Ăł��邠��ɂ��������ǖ�����

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
            //�F�؏�񂪂Ȃ���ΔF�؂�
            Intent intent = new Intent(this, TwitterOauthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //�F�؂���Ă���Ȃ�TL���擾����
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }
    }

    //���j���[�{�^��
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * ���̃{�^���Q�̃��\�b�h
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            //�X�V�{�^��
            reloadTimeLine();
            //�ۑ�F���̂܂܂��Ǝ擾�ł��Ȃ��ꍇ���\�������
            showToast("�^�C�����C�����擾���܂���");
            return true;
        case R.id.menu_tweet:
            //�c�C�[�g��ʂ�
            Intent intent = new Intent(this, TweetActivity.class);
            startActivity(intent);
            return true;
        case R.id.tweet_form:
            //��^���c�C�[�g�{�^��
            FormTweetDialog();
            return true;
        case R.id.tweet_form_set:
            //��^���ݒ�{�^��
            Intent intent2 = new Intent(this, TweetEdit.class);
            startActivity(intent2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DB����o�^����Ă����^�����擾���郁�\�b�h
     * @return textFrom �o�^����Ă���e�L�X�g
     */
    private String[] getTweetText(){
        String[] textForm = new String[TEXTFORM_MAX]; //DB�ɓo�^����Ă��镶����
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
     * ��^���c�C�[�g��I�������Ƃ��̃��\�b�h
     * 
     */
    private void FormTweetDialog(){
        String textForm[] = getTweetText();
        if(textForm[0] == null) {
            //�Ȃɂ��o�^����Ă��Ȃ��ꍇ
            showToast("��^�����o�^����Ă��܂���B");
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
            items[5] = "����ς��߂�";

            new AlertDialog.Builder(MainActivity.this)
            .setTitle("�ȂɂԂ₭�H")
            .setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    selectList(item);
                }
            }).show();

        }

    }

    /**
     * ��^���̃��X�g��I�������Ƃ��̓���
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
     * ���X�g�̃A�C�e���i�c�C�[�g�j���N���b�N���ꂽ�Ƃ��̃R�[���o�b�N�B
     * �_�C�A���O��\�����AReply,Retweet,Favorite�֔�ԁB
     * @param l ���X�g�r���[
     * @param v 
     * @param position �N���b�N���ꂽ�A�C�e���̈ʒu
     * @param id 
     */

    //  �ӂ��ڂƂ�����[�ƂƂ����̂�����̓���
    //  �Ƃ肠�������
    //	@Override
    //	protected void onListItemClick(ListView l, View v, int position, long id){
    //
    //		selectPos = position;
    //
    //		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //		builder.setIcon(R.drawable.icon)
    //		.setTitle("Select Your Action")
    //		.setMessage("���[�Ɠ��e")
    //		.setItems(R.string.string_array, new DialogInterface.OnClickListener() {
    //
    //			/**
    //			 * �_�C�A���O�̃A�C�e�����N���b�N���ꂽ���̏���
    //			 * @param dialog
    //			 * @param which �ǂ̃A�C�e�����N���b�N���ꂽ��
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
     * �c�C�[�g���郁�\�b�h
     * @param tweet_form �Ԃ₭�e�L�X�g
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
                    showToast("�c�C�[�g���������܂����I");
                    reloadTimeLine();
                } else {
                    showToast("�c�C�[�g�Ɏ��s���܂����B�B�B");
                }
            }
        };
        task.execute(tweet_form);
    }

    /**
     * �^�C�����C���̍X�V
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
                    showToast("�^�C�����C���̎擾�Ɏ��s���܂����B�B�B");
                }
            }
        };
        task.execute();
    }

    /**
     * �_�C�A���O
     * @param text
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
