import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
public class BluetoothControl extends Utility{
	private int errorRequest = NOERROR; //エラー通知を管理（初期値はエラー無し）
	private int message = NOMESSAGE;//通常のメッセージの通知を管理（初期値はなにもなし）
	private String accesscode;//Bluetoothに使われる
	private CommPortIdentifier portID;
	private SerialPort port;
	private OutputStream out;
	private InputStream in;
	private int roombaStatus;
	BluetoothControl(String accesscode) {
		this.accesscode = accesscode;
		try {//ココらへんは結構適当にやっている。あまり意味が無い。。。
			File file = new File("/var/lock/※");
			if (file.exists()){
				if (file.delete()){
				}else{
			}
			}else{
			}
//			CommPort.disableReceiveTimeout();
			this.portID = CommPortIdentifier.getPortIdentifier(this.accesscode);
			this.port = (SerialPort)portID.open("RCCSystem", 60000); //waiting5000ms
			port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			this.out = port.getOutputStream();
			this.in = port.getInputStream();
			Thread.sleep(300);
			out.write(128);
			Thread.sleep(300);
			roombaStatus = getStatus();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("初期化に失敗しました。Bluetoothが起動されているかを確認し、再度実行をお願い致します");
			System.exit(-1);
		}
		//bluetoothMac
	}
	public int getErrorRequest(){
		return this.errorRequest;
	}
	public int getMessage(){
		return this.message;
	}
	public int getRoombaStatus(){
		return this.roombaStatus;
	}
	public void setErrorRequest(int request){
		this.errorRequest = request;
	}
	public void setmessage(int mes){
		this.message = mes;
	}
	public int transmitOrder(int status,int order) throws Exception{
		int newStatus = status;
		if(order == NONRESPONSE){
			if(status == CLEANING && getBattery() <= DOCKGATE){
				try {
					Thread.sleep(8000);
					out.write(128);
					Thread.sleep(300);
					out.write(132);
					Thread.sleep(300);
					out.write(130);
					Thread.sleep(300);
					out.write(143);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("バッテリーが少ないため、Roombaに清掃を終了する命令を送りました。");
				newStatus = BACKING;
				
			}
			return newStatus;
		}
		else if(order == START){
			if(status ==POWEROFF){
				try {
					out.write(128);
					Thread.sleep(300);
					out.write(131);
					Thread.sleep(300);
					out.write(136);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Roombaに清掃を開始する命令を送りました。");
				newStatus = POWERREADY;
			}
			else if(status == POWERREADY){
			}
			else if(status == CLEANING || status == BACKING) errorRequest =ALREADYSTARTED;
			else if(status == CHARGING) errorRequest = NOENERGY; 
		}
		else if(order == FINISH){
			if(status == POWERREADY ||status == CLEANING){
				try {
					out.write(128);
					Thread.sleep(300);
					out.write(132);
					Thread.sleep(300);
					out.write(130);
					Thread.sleep(300);
					out.write(143);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Roombaに清掃を終了する命令を送りました。");
				newStatus = BACKING;
			}
			else if(status == BACKING)errorRequest = ALREADYBACKED;
			else if(status == CHARGING)errorRequest = ALREADYFINISHED;
			else if(status == POWEROFF)errorRequest = NOTCLEANEDYET;
		}
		else if(order == UNKNOWN) errorRequest = UNKNOWNWORD;
		else if(order == OTHERMAN) message = WHOAREYOU;
		return newStatus;
	}
	public int checkStatus(int status) throws Exception{
		int newStatus=status;
//		in = port.getInputStream();
//		 byte[] buffer = new byte[1024];
//		 int numRead = in.read(buffer);
		 System.out.println("Battery:"+getBattery()+"%");
//		 System.out.println("System:"+getBStatus());
		 if(status == CHARGING && getBattery()>=BATTERYGATE)newStatus =POWEROFF;
		else if(status == ERRORSTOP)errorRequest = ERRORSTATUS;
		else if(status == POWERREADY){
			if(getStatus() == 0){
				System.out.println("Roombaが清掃を始めたことを通知しました。");
				newStatus = CLEANING;
				roombaStatus = 0;
				message = STARTED;
			}
		}
		else if(status == CLEANING || status == BACKING){
			if(getStatus() == 2){
				if(status == CLEANING && getBattery() >=BATTERYGATE){
					try {
						Thread.sleep(10000);
						out.write(128);
						Thread.sleep(300);
						out.write(131);
						Thread.sleep(300);
						out.write(135);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("あまりにも短すぎるためRoombaに再度清掃を開始する命令を送りました。");
				}
				else{
					System.out.println("Roombaが掃除を終わったことを通知しました。");
					message = FINISHED;
					roombaStatus = 2;
					newStatus = CHARGING;

				}
			}
		}
		return newStatus;
	}
	public int getBattery() throws Exception{
		return (int)(((double)getBattery1() / (double)getBattery2()) *100);
		
	}
    public int getBattery1() throws Exception
  {
      int temp;//シリアル通信できたもの
      int capacity = 0;//バッテリ容量
      out.write((byte)142);  
      out.write((byte)25);   //左モーターの番号
   
      while( in.available() < 2 )//2byte来るまで無限ループ
      {        
      }
   
      if( in.available() >= 2 )//
      {
          temp = in.read();  capacity = temp * 256;
          temp = in.read();  capacity += temp;
          
      }
      return capacity;
  }
    public int getBattery2() throws Exception
  {
      int temp;//シリアル通信できたもの
      int capacity = 0;//バッテリ容量
      out.write((byte)142);  
      out.write((byte)26);   //左モーターの番号
   
      while( in.available() < 2 )//2byte来るまで無限ループ
      {        
      }
   
      if( in.available() >= 2 )//
      {
          temp = in.read();  capacity = temp * 256;
          temp = in.read();  capacity += temp;
          
      }
      return capacity;
  }
    public int getStatus() throws Exception
    {
        int temp;//シリアル通信できたもの
        int capacity = 0;//バッテリ容量
        out.write((byte)142);  
        out.write((byte)34); 
     
        while( in.available() < 1 )//2byte来るまで無限ループ
        {
        }
     
        if( in.available() >= 1 )//
        {
            temp = in.read();  capacity = temp;
            
        }
     
        return capacity;
    }

}
