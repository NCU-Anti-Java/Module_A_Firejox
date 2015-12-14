package prototype_A;

import prototype_B.TCPC_module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by firejox on 2015/12/8.
 */


@XmlRootElement (name = "Virtual_Character")
@XmlAccessorType (XmlAccessType.FIELD)
public class virtual_character_t extends dynamic_object_t {

    public static final int face_north = TCPC_module.TURNNORTH;
    public static final int face_east  = TCPC_module.TURNEAST;
    public static final int face_west  = TCPC_module.TURNWEST;
    public static final int face_south = TCPC_module.TURNSOUTH;

    private static Map<Integer, xpoint_t> xy_move = new HashMap<Integer, xpoint_t>();
    private static final xpoint_t default_move = new xpoint_t(0, 0);


    /**
     * initial the xy move condition with the four direction.
     *
     * */
    static {
        xy_move.put(face_north, new xpoint_t(0, -1));
        xy_move.put(face_east, new xpoint_t(1, 0));
        xy_move.put(face_south, new xpoint_t(0, 1));
        xy_move.put(face_west, new xpoint_t(-1, 0));
    }

    private Integer client_no = null;
    private AtomicInteger dir = new AtomicInteger(0);
    private int velocity = 0;
    private int grab_range = 1;

    private item_vector_t items = new item_vector_t();

    virtual_character_t() {
        super(0, 0);
    }

    virtual_character_t(int x, int y) {
        super(x, y);

    }

    /**
     * set_client_no - set the client number.
     * @param client_no - the client number.
     *
     * */
    synchronized void set_client_no (int client_no) {
        this.client_no = client_no;
    }

    /**
     * reset - reset the status of character.
     * */
    void reset () {
        this.client_no = null;
        this.items.clear();
        this.dir.lazySet(0);
        this.velocity = 0;
    }

/**
 * the below method should not called when {@link client_no}
 *  is not set yet or after {@link #reset()} method and before
 *  {@link #set_client_no(int client_no)}.
 * */

    /**
     * set_dir -set character direction
     * @param move_code - the code represents the character moving direction.
     * */
    synchronized void set_dir (int move_code) {
        assert client_no != null : "The client number should be set!";
        dir.set(move_code);
    }

    public int get_dir () {
        return dir.get();
    }

    /**
     * grab_item - get a item
     * @param item - the virtual item grabbed.
     * */
    void grab_item (item_t item) {
        assert client_no != null : "The client number should be set!";
        items.add(item);
    }

    public Vector<item_t> get_items() {
        return new Vector<item_t>(items);
    }



    /**
     * set_velocity - set the velocity of character
     * @param velocity - the velocity of character
     *
     * */
    synchronized void set_velocity (int velocity) {
        assert client_no != null : "The client number should be set!";
        this.velocity = velocity;
    }

    /**
     * update_position - update the position of character
     * */
    @Override
    synchronized void update_position () {
        assert client_no != null : "The client number should be set!";

        set_center(peek_position());
    }


    /**
     * peek_position - this will return the position that
     *  character next move.
     *
     * @return - the next step position.
     * */
    synchronized xpoint_t peek_position () {
        assert client_no != null : "The client number should be set!";

        return xpoint_t.add(get_center(),
                xpoint_t.mul (
                        xy_move.getOrDefault(dir.get(), default_move),
                        velocity));
    }


    /**
     * grab_limit_position - this will return the limit position
     *  represents that character can grab item.
     *
     * @return - position of limit range.
     * */
    synchronized xpoint_t grab_limit_position () {
        assert client_no != null : "The client number should be set!";

        return xpoint_t.add(get_center(),
                xpoint_t.mul (
                        xy_move.getOrDefault(dir.get(), default_move),
                        grab_range));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof virtual_character_t) {
            virtual_character_t ch = (virtual_character_t)obj;

            if (ch.client_no == null)
                return client_no == null;

            return (ch.client_no == null ?
                        client_no == null :
                        ch.client_no.equals(client_no)) &&
                    ch.dir.get() == dir.get() &&
                    ch.grab_range == grab_range &&
                    ch.items.equals(items);
        }

        return false;
    }

}

class item_vector_t extends Vector<item_t> {
    item_vector_t() {
        super();
    }

    public void clear() {
        this.forEach(item_t::unref);
        super.clear();
    }

    public void finalize() throws Throwable {
        clear();
        super.finalize();
    }

}