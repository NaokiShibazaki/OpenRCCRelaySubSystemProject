
public class RelayMain{

	public static void main(String[] args) throws Exception {
		RCCRelaySubSystem relay = new RCCRelaySubSystem();
		relay.Initialize("UserName","Bluetoothパス");//UserNameにこのシステムを使うユーザーのTwitterアカウントを入れ、
													//BluetoothパスにBluetoothにSPP通信するための場所を伝える
		
		try{

			relay.run();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
}
