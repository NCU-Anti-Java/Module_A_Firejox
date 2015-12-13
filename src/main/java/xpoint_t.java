import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by firejox on 2015/12/8.
 */
@XmlRootElement (name = "xPoint")
@XmlAccessorType (XmlAccessType.FIELD)
public class xpoint_t {
    private int x;
    private int y;

    xpoint_t() {
        x = 0;
        y = 0;
    }

    xpoint_t(int x, int y) {
        this.x = x;
        this.y = y;
    }

    xpoint_t(xpoint_t p) {
        set_pos(p);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public final void set_pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final void set_pos (xpoint_t pos) {
        if (pos == null)
            set_pos(0, 0);
        else
            set_pos(pos.x, pos.y);
    }


    @Override
    public boolean equals (Object obj) {
        if (obj instanceof xpoint_t) {
            xpoint_t p = (xpoint_t) obj;
            return (x == p.x && y == p.y);
        }

        return false;
    }

    @Override
    public int hashCode() {
        long a = x >= 0 ? 2 * x: -2 * x -1;
        long b = y >= 0 ? 2 * y: -2 * y -1;

        long c = (a >= b ? (a * a + a + b) : (a + b * b)) / 2;

        return (int)(
                ((x < 0) && (y < 0) || (x >= 0) && (y >= 0)) ? c : -c -1);
    }


    static xpoint_t add(xpoint_t a, xpoint_t b) {
        return new xpoint_t(a.x() + b.x(), a.y() + b.y());
    }

    static xpoint_t mul(xpoint_t a, int b) {
        return new xpoint_t(a.x() * b, a.y() * b);
    }
}
