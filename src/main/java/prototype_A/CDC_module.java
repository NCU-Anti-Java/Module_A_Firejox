package prototype_A;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;


/**
 * Created by firejox on 2015/12/7.
 */
public class CDC_module {

    private static final WeakReference<virtual_character_t> de_ch =
            new WeakReference<virtual_character_t>(null);

    private static final WeakReference<item_t> de_item =
            new WeakReference<item_t>(null);

    private game_map_t gp = null;

    private Vector<item_t> items = new Vector<>();
    private Vector<item_t> update_items = new Vector<>();

    private Vector<virtual_character_t> ch_arr = new Vector<>();

    private Map<Integer, WeakReference<virtual_character_t>> va_chs =
            new HashMap<>();

    private Map<xpoint_t, WeakReference<item_t>> item_map = new HashMap<>();


    private long prev_t = System.currentTimeMillis();


    private Thread update_thread = new Thread(this::update_virtual_character);


    /**
     * update_virtual_character - update virtual characters
     * */
    private void update_virtual_character() {
        try {
            while (true) {
                for (virtual_character_t ch : ch_arr)
                    ch.update_position();

                long cur_t = System.currentTimeMillis();

                if (prev_t + 500 > cur_t) {

                    Thread.sleep(prev_t + 500 - cur_t);

                    prev_t = prev_t + 500;
                } else {
                    prev_t = cur_t;

                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * addVirtualCharacter - add virtual character for `client_no`
     * @param client_no - the client number
     *
     * */
    public synchronized void addVirtualCharacter(int client_no)
            throws GameMapNotLoadError,
            NoAvailableCharacterException, ClientHasAddedException {


        if (va_chs.containsKey(client_no))
            throw new ClientHasAddedException();

        if (gp == null)
            throw new GameMapNotLoadError();

        virtual_character_t v_ch;

        v_ch = gp.register_a_client(client_no);

        WeakReference<virtual_character_t> wva_ch;

        ch_arr.add(v_ch);

        va_chs.put(client_no,
                new WeakReference<virtual_character_t>(v_ch));

    }


    /**
     * addItem - add item when game map load
     * @param name - the name of item
     * @param index - the index of item
     * @param shared - mark item is shared.
     * @param x - the x pos of item
     * @param y - the y pos of item
     *
     * */
    public void addItem(String name, int index,
                        boolean shared, int x, int y)
            throws GameMapNotLoadError {

        if (gp == null)
            throw new GameMapNotLoadError();

        item_t item = new item_t(name, index, shared, x, y);

        items.add(item);
        update_items.add(item);

        item_map.put(item.get_center(),
                new WeakReference<item_t>(item));
    }


    /**
     * updateDirection - update client direction
     * @param client_no - the client number
     * @param move_code - the new direction
     * */
    public void updateDirection(int client_no, int move_code)
            throws InvalidClientException {

        WeakReference<virtual_character_t> ch = va_chs
                .getOrDefault(client_no, de_ch);

        if (ch.get() == null)
            throw new InvalidClientException();

        ch.get().set_dir(move_code);
    }

    /**
     * getItem - client `client` try to get item
     * @param client_no - the client number
     * */
    public synchronized void getItem(int client_no)
            throws InvalidClientException,
                    ItemHasOwnedByOtherException,
                    NoSuchElementException {

        WeakReference<virtual_character_t> ch = va_chs
                .getOrDefault(client_no, de_ch);

        if (ch.get() == null)
            throw new InvalidClientException();

        virtual_character_t v_ch = ch.get();


        item_t item = item_t
                .ref(item_map
                        .getOrDefault(
                                v_ch.grab_limit_position(),
                                de_item)
                        .get());

        v_ch.grab_item(item);

        update_items.add(item);


    }


    /**
     * getUpdateInfo - return the dynamic object info
     * */
    public Vector<dynamic_object_t> getUpdateInfo() {
        Vector<dynamic_object_t> objs = new Vector<>(update_items);
        objs.addAll(ch_arr);
        update_items.clear();

        return objs;
    }


    /**
     * load_game_map - load the game map
     * */
    public void load_game_map(String str) {

        gp = game_map_t.load_from_string(str);

        items.clear();
        item_map.clear();
        va_chs.clear();

        for (item_t item : gp.get_items()) {
            addItem(item.get_name(),
                    item.get_index(),
                    item.get_shared(),
                    item.get_center().x(),
                    item.get_center().y());
        }

    }

    /**
     * startUpdatingThread - start the updating thread
     * */
    public void startUpdatingThread()
            throws GameMapNotLoadError, NoClientConnectError {

        if (gp == null || items.isEmpty())
            throw new GameMapNotLoadError();

        if (va_chs.isEmpty())
            throw new NoClientConnectError();

        update_thread.start();
    }

    /**
     * get_update_thread - get the update thread
     * */
    public Thread get_update_thread() {
        return update_thread;
    }

}
