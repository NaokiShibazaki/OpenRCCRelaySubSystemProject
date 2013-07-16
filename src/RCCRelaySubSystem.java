

public class RCCRelaySubSystem extends Utility{
	private TwitterControl twitter = null;
	private int status;
	private BluetoothControl bluetooth = null;
	private long time;
	public void Initialize(String username,String accesscode) throws Exception{
		System.out.println("team Dramatic Discovery\nRemoteControlCleaningSystem\n起動中・・・");
		try{
		this.twitter = new TwitterControl(username);
		this.bluetooth = new BluetoothControl(accesscode);
		this.time = System.currentTimeMillis();
		if(this.bluetooth.getBattery()>=BATTERYGATE)this.status = POWEROFF;
		else this.status = CHARGING;
//		this.status = CLEANING;
		}catch(Exception e){
			System.err.println("初期化に失敗しました。再度実行をお願い致します");
		}
		System.out.println("遠隔清掃システム起動成功！スタートします。");
	}
	public void run() {
		while(true){
			try{
				RCCOrder();
//				System.out.println(bluetooth.getMessage()+":"+bluetooth.getErrorRequest());
				imformToUser();
//				System.out.println("Timer:"+cleanNeedsTime+" status:"+status+" Battery:"+debugBattery);
				Thread.sleep(100);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void RCCOrder() throws Exception{
		long diff = System.currentTimeMillis() - this.time;
		status = bluetooth.checkStatus(this.status);
		status = bluetooth.transmitOrder(status,twitter.searchTwitter(diff,status));
		this.time = System.currentTimeMillis();
	}
	public void imformToUser(){//
		bluetooth.setmessage(twitter.postTweet(bluetooth.getMessage()));
		bluetooth.setErrorRequest(twitter.postErrorTweet(bluetooth.getErrorRequest()));
	}
	
}
