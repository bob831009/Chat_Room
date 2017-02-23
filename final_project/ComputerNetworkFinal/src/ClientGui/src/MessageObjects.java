import java.io.Serializable;

public class MessageObjects implements Serializable {

	protected static final long serialVersionUID = 11121222000L;
	int err_signal;
	int offline_signal;
	// 
	String tag;
	String id;
	String pw;
	String from_client;
	String to_client;
	String message;
	String online_id;
	String err_message;
	
	// handle file
	String file_path;
	String file_name;
	int file_length;
	byte [] file_bytearray;
	public MessageObjects(String tmp_tag){
		tag = tmp_tag;
		err_signal = 0;
		offline_signal = 0;
	}
	public String getMessage(){
		return this.message;
	}
}