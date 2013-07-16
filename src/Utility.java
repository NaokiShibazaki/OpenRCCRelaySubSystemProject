import java.util.Calendar;

public class Utility {
	//Twitterのリプライに対するフラグ
	final static public int OTHERMAN = -1;//利用者以外のリプライ
	final static public int NONRESPONSE = 0;//リプライを受け取っていない
	final static public int START = 1;//清掃開始命令
	final static public int FINISH = 2;//清掃中止命令
	final static public int UNKNOWN = 3;//判断不能
	//Roombaの状態に関するフラグ
	final static public int POWEROFF = 0;//清掃していない、清掃命令待ち
	final static public int POWERREADY = 1;//清掃命令を受け、清掃を開始するまでの間
	final static public int CLEANING = 2;//清掃中
	final static public int BACKING = 3;//清掃中止命令を受け、Dockに戻るまでの間
	final static public int CHARGING = 4;//充電中（清掃不可）
	final static public int ERRORSTOP = 5;//エラー停止（未実装）
	//Twitterの投稿に関するフラグ
	final static public int NOMESSAGE = 0;//投稿内容なし
	final static public int STARTED = 1;//清掃開始通知
	final static public int FINISHED = 2;//清掃終了通知
	final static public int WHOAREYOU = 3;//ユーザーでないことを返す（未実装）
	//Twitterのエラー投稿に関するフラグ
	final static public int NOERROR = 0;//エラー無し
	final static public int ALREADYSTARTED = 1;//清掃を開始しているにもかかわらず、清掃命令を出した
	final static public int ALREADYBACKED = 2;//清掃中止命令しているにもかかわらず、清掃中止命令を出した
	final static public int ALREADYFINISHED = 3;//清掃終了しているにもかかわらず、清掃中止命令を出した（現状は充電中の時に使われる）
	final static public int NOTCLEANEDYET = 4;//清掃を開始していないにもかかわらず、清掃中止命令を出した（現状は清掃命令待ちの時に使われる）
	final static public int NOENERGY = 5;//充電中に清掃命令をだした
	final static public int ERRORSTATUS = 6;//エラー停止
	final static public int UNKNOWNWORD = 7;//コマンドがわからない
	//Roombaのバッテリー管理に関するフラグ
	final static public int BATTERYGATE = 80;//起動開始時に求める最低限のバッテリー充電量（％）
	final static public int DOCKGATE = 15;//清掃を中止し、Dockに戻る命令を出すバッテリー充電量（％）
	public static String nowTime(){//現在時刻を出力する
		Calendar cal1 = Calendar.getInstance();  
	    int year = cal1.get(Calendar.YEAR);        
	    int month = cal1.get(Calendar.MONTH) + 1;  
	    int day = cal1.get(Calendar.DATE);         
	    int hour = cal1.get(Calendar.HOUR_OF_DAY); 
	    int minute = cal1.get(Calendar.MINUTE);    
	    int second = cal1.get(Calendar.SECOND);    
	    StringBuffer dow = new StringBuffer();
	    switch (cal1.get(Calendar.DAY_OF_WEEK)) {  
	      case Calendar.SUNDAY: dow.append("日曜日"); break;
	      case Calendar.MONDAY: dow.append("月曜日"); break;
	      case Calendar.TUESDAY: dow.append("火曜日"); break;
	      case Calendar.WEDNESDAY: dow.append("水曜日"); break;
	      case Calendar.THURSDAY: dow.append("木曜日"); break;
	      case Calendar.FRIDAY: dow.append("金曜日"); break;
	      case Calendar.SATURDAY: dow.append("土曜日"); break;
	    }

	    //
	   String string=year + "/" + month + "/" + day + " " + dow
	                       + " " + hour + ":" + minute + ":" + second;
	   return string;
	}
	
}
