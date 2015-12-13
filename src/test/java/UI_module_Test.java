import com.sun.javafx.robot.FXRobot;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import prototype_B.TCPC_module;
import prototype_D.DO_module;

/**
 * Created by firejox on 2015/12/9.
 */

public class UI_module_Test {
    UI_module module;
    FXRobot robot;
    TCPC_module tcpc_module;
    DO_module do_module;
    static Thread t;

    volatile Integer test_move_code = null;
    volatile Boolean test_key_get_pressed = null;


    @Before
    public void set_up() throws Exception {

        module = new UI_module();

        test_move_code = null;
        test_key_get_pressed = null;

    }


    @After
    public void clean_up() throws Exception {
        module = null;
    }

    void link_up_TCPC_module() {
        tcpc_module = new TCPC_module() {
            @Override
            public void inputMoves(int move_code) {
                test_move_code = move_code;
            }
        };

        module.link_TCPC_module(tcpc_module);
    }

    void link_up_DO_module() {
        do_module = new DO_module() {
            @Override
            public void keyGETPressed() {
                test_key_get_pressed = true;
            }
        };

        module.link_DO_module(do_module);
    }

    void link_up_modules() {
        link_up_TCPC_module();
        link_up_DO_module();
    }

    @Test(expected = AssertionError.class)
    public void test_link_TCPC_module() {
        module.link_TCPC_module(null);
    }

    @Test(expected = AssertionError.class)
    public void set_DO_module_null() {
        module.link_DO_module(null);
    }

    @Test
    public void test_keycode_UP() {
        link_up_modules();

        module.keycode_operate(KeyCode.UP);
        Assert.assertTrue(test_move_code == TCPC_module.TURNNORTH
                && test_key_get_pressed == null);
    }

    @Test
    public void test_keycode_RIGHT() {
        link_up_modules();

        module.keycode_operate(KeyCode.RIGHT);

        Assert.assertTrue(test_move_code == TCPC_module.TURNEAST
                && test_key_get_pressed == null);
    }

    @Test
    public void test_keycode_LEFT() {
        link_up_modules();

        module.keycode_operate(KeyCode.LEFT);

        Assert.assertTrue(test_move_code == TCPC_module.TURNWEST
                && test_key_get_pressed == null);
    }

    @Test
    public void test_keycode_DOWN() {
        link_up_modules();

        module.keycode_operate(KeyCode.DOWN);

        Assert.assertTrue(test_move_code == TCPC_module.TURNSOUTH
                && test_key_get_pressed == null);
    }

    @Test
    public void test_key_get_press() {
        link_up_modules();

        module.keycode_operate(KeyCode.ENTER);

        Assert.assertTrue(test_key_get_pressed && test_move_code == null);
    }


    @Test
    public void test_key_press_other_KeyCODE() {
        link_up_modules();

        for (KeyCode k : KeyCode.values()) {
            switch (k) {
                case LEFT:
                case RIGHT:
                case DOWN:
                case UP:
                case ENTER:
                    break;
                default:
                    module.keycode_operate(k);
            }

        }

        module.keycode_operate(null);

        Assert.assertTrue(test_key_get_pressed == null && test_move_code == null);

    }

    @Test
    public void test_key_code_without_link_modules() {

        for (KeyCode k : KeyCode.values())
            module.keycode_operate(k);

        Assert.assertTrue(test_key_get_pressed == null && test_move_code == null);
    }


}
