package prototype_A;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Vector;

/**
 * Created by firejox on 2015/12/8.
 */

@XmlRootElement (name = "Virtual_Item")
@XmlAccessorType (XmlAccessType.FIELD)
public class item_t extends dynamic_object_t {
    private String name; // item name
    private int index; //the index of item

    private boolean shared; //if item is shared then the reference count
                            // should not greater than 1

    private int ref_count; //the reference count of item

    item_t() {
        super(0,0);
        name = new String ("");
    }

    item_t(String name, int index, boolean shared, int x, int y) {
        super(x, y);

        Optional<String> s_opt = Optional.ofNullable(name);

        this.name = s_opt.orElse(new String(""));
        this.index = index;
        this.shared = shared;
        this.ref_count = 0;
    }

    boolean is_shared () {
        return shared;
    }

    int get_ref_count () {
        return ref_count;
    }

    void update_position () {}

    public String get_name() {
        return new String(name);
    }

    public int get_index() {
        return index;
    }

    public boolean get_shared() {
        return shared;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof item_t) {
            item_t o_item = (item_t)obj;

            return o_item.name.equals(name) &&
                    o_item.index == index &&
                    o_item.shared == o_item.shared &&
                    o_item.get_center().equals(get_center()) &&
                    o_item.ref_count == ref_count;
        }

        return false;
    }

    static public synchronized item_t ref(item_t item)
                throws ItemHasOwnedByOtherException, NoSuchElementException {

        if (item == null)
            throw new NoSuchElementException();

        if (item.shared && item.ref_count == 1)
            throw new ItemHasOwnedByOtherException();

        item.ref_count++;

        return item;
    }

    static public synchronized item_t unref(item_t item)
                throws NoSuchElementException {
        if (item == null)
            throw new NoSuchElementException();

        item.ref_count --;
        return null;
    }

}

class item_vector_t extends Vector<item_t> {
    item_vector_t() {
        super();
    }

    public void clear() {
        for (item_t i : this) {
            item_t.unref(i);
        }
        super.clear();
    }

    public void finalize() throws Throwable {
        clear();
        super.finalize();
    }

}

