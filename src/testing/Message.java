package testing;

public class Message {

    public static final String HOVER = "\\hover:";

    public static final String UNIT_SELECT = "\\unitselect:";
    public static final String UNIT_MOVE = "\\unitmove:";
    public static final String UNIT_ATTACK = "\\unitattack:";
    public static final String UNIT_DIR = "\\unitdir:";

    public static final String END_TURN = "\\endturn";

    public static boolean isCommand(Object message) {
	return message.equals(Message.HOVER) || message.equals(Message.UNIT_SELECT) || message.equals(Message.UNIT_MOVE)
		|| message.equals(Message.UNIT_ATTACK) || message.equals(Message.UNIT_DIR)
		|| message.equals(Message.END_TURN);
    }
}