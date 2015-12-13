package prototype_B;

/**
 * Created by firejox on 2015/12/8.
 */
public abstract class TCPC_module {
    public static final int TURNNORTH = 1;
    public static final int TURNEAST = 2;
    public static final int TURNWEST = 3;
    public static final int TURNSOUTH = 4;

    public abstract void inputMoves(int move_code) ;
}
