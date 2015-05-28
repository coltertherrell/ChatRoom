import java.io.Serializable;


public class ChatMessage implements Serializable
{
	static final int WHO = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String msg;
	
	ChatMessage(int type, String msg)
	{
		this.type = type;
		this.msg = msg;
	}
	
	int getType()
	{
		return type;
	}
	
	String getMessage()
	{
		return msg;
	}

}
