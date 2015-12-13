package prototype_A;

import com.sun.istack.internal.NotNull;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by firejox on 2015/12/9.
 */
@XmlRootElement(name = "Game_Map")
@XmlAccessorType(XmlAccessType.FIELD)
public class game_map_t {


    private Vector<item_t> items = new Vector<item_t>();

    private Stack<virtual_character_t> avail_ch = new Stack<virtual_character_t>();


    /**
     * get_items - get the virtual items in the map.
     *
     * @return - the virtual item array.
     * */
    final Vector<item_t> get_items() {
        return items;
    }


    /**
     * register_a_client - register a client number `client_no` to
     *  get a virtual character from the map before game start. If
     *  there is no available character to use, it will throw
     *  prototype_A.NoAvailableCharacterException. Otherwise, the available
     *  character will return.
     *
     * @param client_no - the client number
     *
     * @return - the available character with `client_no`
     *
     * @throws NoAvailableCharacterException - mark no available
     *  character in current map.
     * */
    final virtual_character_t register_a_client (int client_no)
                throws NoAvailableCharacterException {

        virtual_character_t v_ch;

        try {
            v_ch = avail_ch.pop();
        } catch (EmptyStackException e) {
            throw new NoAvailableCharacterException();
        }

        v_ch.set_client_no(client_no);

        return v_ch;
    }


    /**
     * unregistr_a_client - if a client leave before game start,
     *  it will call this method to unregister client.
     * @param v_ch - the virtual character it passed.
     *
     * */
    final void unregister_a_client (@NotNull virtual_character_t v_ch) {
        assert v_ch != null : "virtual character should not be null!";

        v_ch.reset();
        avail_ch.push(v_ch);
    }

    /**
     * add_item - add virtual item into `items` for map create
     * @param name - the name of item
     * @param index - the index of item
     * @param shared - item is shared
     * @param x - the x pos of item
     * @param y - the y pos of item
     *
     * @see item_t
     *
     * */
    public final void add_item (String name, int index,
                                boolean shared, int x, int y) {
        Optional<String> option_name = Optional.ofNullable(name);

        items.add(new item_t(option_name.orElse(""),
                index, shared, x, y));
    }


    /**
     * add_a_character - add a character into the map for map create
     * @param x - the x pos of character
     * @param y - the y pos of character
     *
     * @see virtual_character_t
     * */
    public final void add_a_character (int x, int y) {
        avail_ch.push(new virtual_character_t(x, y));
    }


    /**
     * convert map information to string by marshaling
     *
     * @return - the string of map information.
     * */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();

        JAXB.marshal(this, sw);

        return sw.toString();
    }

    /**
     * load the map by unmarshaling the string
     * @param s - the string of map information
     *
     * @return - the game map
     * */
    static public final game_map_t load_from_string (String s){
        StringReader sr = new StringReader(s);

        return JAXB.unmarshal(sr, game_map_t.class);
    }

    static public final game_map_t random_build (int item_num, int ch_num) {

        if (item_num < 0 || ch_num < 0)
            throw new IllegalArgumentException();

        SecureRandom r = new SecureRandom();
        byte arr[] = new byte[15];

        game_map_t m = new game_map_t();

        for (int i = 0; i < item_num; i++) {
            r.nextBytes(arr);

            m.add_item(Base64.getEncoder().encodeToString(arr),
                    i,r.nextBoolean(),r.nextInt(20),r.nextInt(20));
        }

        for (int i = 0; i < ch_num; i++) {
            m.add_a_character(r.nextInt(20), r.nextInt(20));
        }

        return m;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof game_map_t) {
            game_map_t tmp = (game_map_t) obj;

            return tmp.items.equals(items) &&
                    tmp.avail_ch.equals(avail_ch);
        }
        return false;
    }

}
