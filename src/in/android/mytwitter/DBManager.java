/**
 * ��^���o�^��DB�iSQLite�j�ڑ��p�N���X
 * @author yoshiaki_tajima
 */

package in.android.mytwitter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
	//�R���X�g���N�^��`
	public DBManager(Context context) {
		//SQLiteOpenHelper�̃R���X�g���N�^�Ăяo��
		super(context, "sampleDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//SQL����`
		String sql = "CREATE TABLE TWEETFORM(" +
//					"_id integer primary key," +
					" tweettext text not null," +
					"tweetcount text not null," +
					"tweettime text not null);";
		
		//int _id PK ID
		//text tweettext not null�@��^��
		//int tweetcount not null default 0�@�Ԃ₢����
		//text tweettime not null�@�O�ɂԂ₢������
		
		//SQL���s
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}
	
}
