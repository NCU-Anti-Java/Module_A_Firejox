package prototype_A;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by firejox on 2015/12/13.
 */
public class game_map_t_Test {
    game_map_t gp;

    @Before
    public void set_up() throws Throwable {
        gp = game_map_t.random_build(20, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_random_build_with_Invalid_arguments() {
        game_map_t.random_build(-1, -1);
    }



    @Test
    public void test_marshal() {
        String str = new String("");
        str = gp.toString();

        System.err.println(str);
        Assert.assertTrue(!str.equals(""));
    }

    @Test
    public void test_unmarshal() {
        game_map_t tmp = game_map_t.load_from_string(gp.toString());

        Assert.assertTrue(tmp.equals(gp));
    }

    @Test
    public void test_marshal_unmarshal () {
        game_map_t tmp = game_map_t.load_from_string(gp.toString());

        Assert.assertTrue(tmp.toString().equals(gp.toString()));
    }



}
