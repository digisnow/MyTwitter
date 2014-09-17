/**
 * ’èŒ^•¶‚ğ•Û‚·‚éƒNƒ‰ƒX
 * @author yoshiaki_tajima
 */

package in.android.mytwitter;

public class TweetTextForm {
    //private int _id;
    private String tweetText;
    private String tweetCount;
    private String tweetTime;

    public TweetTextForm(String tweetText, String tweetCount, String tweetTime){
        //this._id = _id;
        this.tweetText = tweetText;
        this.tweetCount = tweetCount;
        this.tweetTime = tweetTime;
    }

    //	public int get_id() {
    //		return _id;
    //	}

    public String getTweetText() {
        return tweetText;
    }

    public String getTweetCount() {
        return tweetCount;
    }

    public String getTweetTime() {
        return tweetTime;
    }

}
