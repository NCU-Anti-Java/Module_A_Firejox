package prototype_A;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/**
 * Created by firejox on 2015/12/8.
 */

@XmlRootElement (name = "Virtual_Object")
@XmlAccessorType (XmlAccessType.FIELD)
public abstract class dynamic_object_t {
    private xpoint_t center;

    dynamic_object_t() {
        center = new xpoint_t(0,0);
    }
    protected dynamic_object_t(int x, int y) {
        center = new xpoint_t(x, y);
    }

    protected final void set_center (int x, int y) {
        center.set_pos(x, y);
    }

    protected final void set_center (xpoint_t pos) { center.set_pos (pos); }

    public final xpoint_t get_center () {
        return new xpoint_t(center);
    }

    abstract void update_position ();

    @Override
    public final String toString() {
        StringWriter sw = new StringWriter();

        JAXB.marshal(this.getClass().cast(this), sw);

        return sw.toString();
    }

}

