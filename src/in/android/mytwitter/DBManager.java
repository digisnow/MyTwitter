/**
 * 定型文登録のDB（SQLite）接続用クラス
 * @author yoshiaki_tajima
 */

package in.android.mytwitter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
	//コンストラクタ定義
	public DBManager(Context context) {
		//SQLiteOpenHelperのコンストラクタ呼び出し
		super(context, "sampleDB", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//SQL文定義
		String sql = "CREATE TABLE TWEETFORM(" +
//					"_id integer primary key," +
					" tweettext text not null," +
					"tweetcount text not null," +
					"tweettime text not null);";
		
		//int _id PK ID
		//text tweettext not null　定型文
		//int tweetcount not null default 0　つぶやいた数
		//text tweettime not null　前につぶやいた時間
		
		//SQL実行
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
	}
	
}
