import twitter4j.*;
import twitter4j.auth.AccessToken;
import java.util.regex.*;

public class TwitterControl extends Utility{
	//クロールタイム
	final private long NOMALTIMER = 6000;//通常
	final private long CAUTIONTIMER = 10000;//エラー時は時間を多めにとる
	//twitterのアクセス情報
	String CONSUMERKEY    = ""; //Twitterから、実際にRoomba用に使うアカウントを取得し、そこから情報を取得する
	String CONSUMERSECRET = ""; //Twitterから、実際にRoomba用に使うアカウントを取得し、そこから情報を取得する
	String ACCESSTOKEN    = ""; //Twitterから、実際にRoomba用に使うアカウントを取得し、そこから情報を取得する
	String ACCESSSECRET   = ""; //Twitterから、実際にRoomba用に使うアカウントを取得し、そこから情報を取得する
	private TwitterFactory factory;
	private Twitter twitter;
	private String username;
	private Query query;
	private long time;
	private long timer;
	TwitterControl(String username){
		this.username=username;
		this.factory = new TwitterFactory();
		this.twitter = factory.getInstance();
		this.query = new Query("@");//@をつけるのを推奨　Roombaアカウントの名前を検索
		this.time = System.currentTimeMillis();
		this.timer = this.NOMALTIMER;
		this.initializeTwitter();
	}
	public void initializeTwitter(){
		this.twitter.setOAuthConsumer(this.CONSUMERKEY,this.CONSUMERSECRET);
		this.twitter.setOAuthAccessToken(new AccessToken(this.ACCESSTOKEN,this.ACCESSSECRET));
	}
	public Twitter getTwitter(){
		return this.twitter;
	}
	public String getUsername(){
		return this.username;
	}
	public int searchTwitter(long x,int state){//twitterサーチを行うかを判断する
		this.timer = this.timer-x;
		if(this.timer <= 0){
			this.timer = this.NOMALTIMER; 
			return searchTwitter(state);
		}
		return 0;
	}
	public int searchTwitter(int state){//twitter
		int flag = NONRESPONSE;
		try{
			QueryResult result = this.twitter.search(this.query);
			for (Status status : result.getTweets()) {
				if(status.getCreatedAt().getTime()<time)break;
				System.out.print("リプライを受け取りました。\n内容：");
				System.out.println(status.getText());
				if(this.isMatchText(status.getUser().getScreenName(),this.username)){
					flag = this.languageProcessing(status.getText(),state);
					break;
				}
				else flag = OTHERMAN;
			}
			if(flag != 0)time = System.currentTimeMillis();
		}catch(Exception e){
			System.err.println("：twitterへのアクセスに失敗しました。");
			this.timer = this.CAUTIONTIMER;
			return NONRESPONSE;
		}
		return flag;
	}
	public boolean isMatchText(String string,String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		if (m.matches()){
			return true;
		}
		return false;
	}
	public int languageProcessing(String string,int state){
		//自然言語処理
		boolean isStart = false,isFinish = false;
		String cleanRegex ="(掃除|ソウジ|そうじ|掃じ|掃ジ|そう除|ソウ除|そーじ|ソージ|清掃|せいそう|セイソウ|清ソウ|清そう|セイ掃|せい掃|綺麗|きれい|キレイ)";
		String cleanCleaningIndependentVerbRegex = "(アクション|して|やって|よろしく|お願い|しろ|スタート|はじめ|ゴー|開始|なう|ナウ|動く|動け|動いて|うごいて|うごけ|片付け|ポチッ|ぽちっ|よーし|うごく|"+
												   "走り|走って|走れ|はしれ|はしって|はしり|拾って|ひろって|出て|出ろ|動き|うごき|やっといて|しといて|ピカ|ピッカ|よいしょ|くる|クル|清潔|オン)";
		String cleanCleaningEnglishRegex = "(go|start|now|clean|action|sweep|addetto|on|pulizie|Schoonmaakster|Schoonmaakster|Limpiador|◯|○|◎|"+
										   "Reiniger|ok)";
		String cleanStopCleaningIndependentVerbRegex = "(中止|いいよ|やめて|ストップ|フィニッシュ|エンド|おわり|お休み|黙って|満足|解除|終わり|終了|もういい|止ま|終わって|ポチ|ぽち|どうも|オフ)";
		String cleanStopCleaningEnglishRegex = "(don|finish|stop|off|end|dock|back|finde|fnde|final|fine|×|☓|✕)";
		string = string.replaceAll("( |　|\t|\n|@DDRoomba)", "").toLowerCase();//空白・空行除去&小文字変換
		isFinish = (this.isMatchText(string,".*?"+cleanStopCleaningIndependentVerbRegex+".*?")||
				this.isMatchText(string,".*?"+cleanStopCleaningEnglishRegex+".*?")||
				this.isMatchText(string,"0"));
		isStart = (this.isMatchText(string,".*?"+cleanRegex+".*?")||
				this.isMatchText(string,".*?"+cleanCleaningIndependentVerbRegex+".*?")||
				this.isMatchText(string,".*?"+cleanCleaningEnglishRegex+".*?")||
				this.isMatchText(string,"1"));
		if(isFinish && isStart){
			if(state == POWEROFF ||state == POWERREADY || state == CHARGING){
				System.out.println("開始・中止の双方の意味にも取れます\nRoombaは現在停止状態のため、掃除開始の命令と判断しました。Roombaに掃除開始命令をします。");
				return START;
			}
			else{
				System.out.println("開始・中止の双方の意味にも取れます\nRoombaは現在運行状態のため、掃除中止の命令と判断しました。Roombaに掃除中止命令をします。");
				return FINISH;
			}
			
		}
		else if(isFinish){
			System.out.println("掃除中止の命令と判断しました。Roombaに掃除中止命令をします。");
			return FINISH;
		}
		else if(isStart){
			System.out.println("掃除開始の命令と判断しました。Roombaに掃除開始命令をします。");
			return START;
			
		}
		else return UNKNOWN;
	}
	public void postTweet(String message){
		try{
			this.twitter.updateStatus("@" + this.username + " "+Utility.nowTime()+" "+message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public int postTweet(int message){
		switch(message){
		case STARTED:postTweet("清掃を開始しました");System.out.println("清掃を開始したことを投稿しました。");break;
		case FINISHED:postTweet("清掃を終了しました");System.out.println("掃除を終了したことを投稿しました。");break;
		}
		message = NOMESSAGE;
		return message;
	}
	public int postErrorTweet(int message){
		switch(message){
		case ALREADYSTARTED:postTweet("すでに掃除を始めています");break;
		case ALREADYBACKED:postTweet("すでに終了段階に入っています");break;
		case ALREADYFINISHED:postTweet("すでに掃除は終わりました");break;
		case NOTCLEANEDYET:postTweet("まだ清掃をしていません");break;
		case NOENERGY:postTweet("清掃を行う電力がありません");break;
		case ERRORSTATUS:postTweet("何か問題が発生いたしました");break;
		case UNKNOWNWORD:postTweet("言葉を判断することができませんでした");break;
		}
		message = NOERROR;
		return message;
	}
	
	
}

