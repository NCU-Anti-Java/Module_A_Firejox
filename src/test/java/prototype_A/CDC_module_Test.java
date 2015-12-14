package prototype_A;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by firejox on 2015/12/12.
 */
public class CDC_module_Test {
    CDC_module module;
    game_map_t gp;
    boolean e1, e2;
    CyclicBarrier barrier;

    @Before
    public void set_up() throws Exception{
        module = new CDC_module();
        gp = game_map_t.random_build(20, 4);
        e1 = e2 = false;
        barrier = new CyclicBarrier(2);
    }

    @After
    public void clean_up() throws Exception {
        if (module.get_update_thread().isAlive())
            module.get_update_thread().interrupt();

        module = null;
        gp = null;
    }


    @Test(expected = GameMapNotLoadError.class)
    public void test_start_thread_before_load_map() {
        module.startUpdatingThread();
    }

    @Test(expected = GameMapNotLoadError.class)
    public void test_add_virtual_character_before_load() throws Throwable {
        module.addVirtualCharacter(new SecureRandom().nextInt());
    }

    @Test
    public void test_add_virtual_character() throws Throwable {
        gp = game_map_t.random_build(0, 1);
        module.load_game_map(gp.toString());

        module.addVirtualCharacter(0);

        Vector<dynamic_object_t> vos = module.getUpdateInfo();

        Assert.assertTrue(
                !vos.isEmpty() &&
                vos.firstElement() instanceof virtual_character_t
        );
    }

    @Test(expected = ClientHasAddedException.class)
    public void test_same_client_repeat_add() throws Throwable {
        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);
        module.addVirtualCharacter(1);
    }

    @Test(expected = NoAvailableCharacterException.class)
    public void test_add_virtual_chracter_is_full() throws Throwable{
        module.load_game_map(gp.toString());

        for (int i = 0; i < 5; i++)
            module.addVirtualCharacter(i);
    }

    @Test(expected = NoClientConnectError.class)
    public void test_start_thread_without_any_character() {
        module.load_game_map(gp.toString());

        module.startUpdatingThread();
    }

    @Test
    public void test_start_thread() throws Throwable {
        module.load_game_map(gp.toString());

        for (int i = 0; i < 4; i++)
            module.addVirtualCharacter(i);

        module.startUpdatingThread();

        Assert.assertTrue(module.get_update_thread().isAlive());

    }

    @Test(expected = GameMapNotLoadError.class)
    public void test_add_item_without_load_map() {
        module.addItem(null, 1, false, 1, 0);
    }

    @Test
    public void test_add_item() {
        gp = game_map_t.random_build(1, 0);

        module.load_game_map(gp.toString());

        Vector<dynamic_object_t> vos = module.getUpdateInfo();

        Assert.assertTrue(
                !vos.isEmpty() &&
                vos.firstElement() instanceof item_t);
    }



    @Test(expected = InvalidClientException.class)
    public void test_update_client_with_Invalid_client() throws Throwable {
        module.updateDirection(1, 10);

    }

    @Test
    public void test_update_client_direction_valid() throws Throwable {
        gp = game_map_t.random_build(0, 1);

        module.load_game_map(gp.toString());
        module.addVirtualCharacter(1);

        module.updateDirection(1, virtual_character_t.face_south);

        virtual_character_t ch = (virtual_character_t)module
                .getUpdateInfo().firstElement();

        Assert.assertTrue(ch.get_dir() == virtual_character_t.face_south);

    }

    @Test
    public void test_update_client_direction_change() throws Throwable {
        gp = game_map_t.random_build(0, 1);

        module.load_game_map(gp.toString());
        module.addVirtualCharacter(1);

        module.updateDirection(1, virtual_character_t.face_south);
        module.updateDirection(1, virtual_character_t.face_east);

        virtual_character_t ch = (virtual_character_t)module
                .getUpdateInfo().firstElement();

        Assert.assertTrue(ch.get_dir() == virtual_character_t.face_east);
    }

    @Test(expected = InvalidClientException.class)
    public void test_get_item_by_Invalid_clinet_number() throws Throwable {
        module.load_game_map(gp.toString());

        module.getItem(new SecureRandom().nextInt());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_get_item_not_found() throws Throwable {

        gp = game_map_t.random_build(0, 1);

        module.load_game_map(gp.toString());
        module.addVirtualCharacter(1);

        module.getItem(1);
    }

    @Test(expected = NoSuchElementException.class)
    public void test_get_item_not_found_with_items() throws Throwable {
        gp = game_map_t.random_build(0, 0);

        gp.add_a_character(0, 0);
        for (int i = 5;i < 10; i++)
            gp.add_item(String.valueOf(i), i, false, i, i);


        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);
        module.getItem(1);
    }

    @Test
    public void test_get_item() throws Throwable {
        gp = game_map_t.random_build(0, 0);

        gp.add_item("0", 0, false, 1, 1);
        gp.add_a_character(1, 0);

        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);

        module.updateDirection(1, virtual_character_t.face_south);

        module.getItem(1);

        virtual_character_t v_ch = (virtual_character_t)
                (module.getUpdateInfo().lastElement());

        item_t item = v_ch.get_items().firstElement();

        Assert.assertTrue(item.equals(
                        item_t.ref(
                                new item_t("0", 0, false, 1, 1))));
    }

    @Test
    public void test_get_item_shared () throws Throwable {
         gp = game_map_t.random_build(0, 0);

        gp.add_item("0", 0, true, 1, 1);
        gp.add_a_character(1, 0);

        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);

        module.updateDirection(1, virtual_character_t.face_south);

        module.getItem(1);

        virtual_character_t v_ch = (virtual_character_t)
                (module.getUpdateInfo().lastElement());

        item_t item = v_ch.get_items().firstElement();

        Assert.assertTrue(item.equals(
                        item_t.ref(
                                new item_t("0", 0, true, 1, 1))));

    }

    @Test (expected = ItemHasOwnedByOtherException.class)
    public void test_two_client_get_same_item_shared () throws Throwable {
        gp = game_map_t.random_build(0, 0);
        gp.add_item("0", 0, true, 1, 1);

        gp.add_a_character(1, 0);
        gp.add_a_character(1, 2);

        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);
        module.addVirtualCharacter(2);

        module.updateDirection(1, virtual_character_t.face_north);
        module.updateDirection(2, virtual_character_t.face_south);

        module.getItem(1);
        module.getItem(2);
    }


    /**
     * Use barrier to create race condition
     * */
    @Test
    public void test_two_client_get_same_item_shared_multi_thread ()
        throws Throwable {

        gp = game_map_t.random_build(0, 0);
        gp.add_item("0", 0, true, 1, 1);

        gp.add_a_character(1, 0);
        gp.add_a_character(1, 2);

        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);
        module.addVirtualCharacter(2);

        module.updateDirection(1, virtual_character_t.face_north);
        module.updateDirection(2, virtual_character_t.face_south);


        Thread t1 = new Thread(()->{
            try {
                Thread.sleep(500);
                barrier.await();
                module.getItem(1);
            } catch (Exception e) {
                e1 = e.getClass() == ItemHasOwnedByOtherException.class;
                e.printStackTrace();
                System.err.println("thread 1");
            }
        });
        t1.start();

        Thread t2 = new Thread(()->{
            try {
                Thread.sleep(100);
                barrier.await();
                module.getItem(2);
            } catch (Exception e) {
                e2 = e.getClass() == ItemHasOwnedByOtherException.class;
                e.printStackTrace();
                System.err.println("thread 2");
            }
        });
        t2.start();

        t1.join();
        t2.join();

        Assert.assertTrue((e1 && !e2)|| (e2 && !e1));

    }

    @Test
    public void test_two_client_get_item_unshared () throws Throwable {

        gp = game_map_t.random_build(0, 0);
        gp.add_item("0", 0, false, 1, 1);

        gp.add_a_character(1, 0);
        gp.add_a_character(1, 2);

        module.load_game_map(gp.toString());

        module.addVirtualCharacter(1);
        module.addVirtualCharacter(2);

        module.updateDirection(1, virtual_character_t.face_north);
        module.updateDirection(2, virtual_character_t.face_south);

        module.getItem(1);
        module.getItem(2);

        Assert.assertTrue(true);

    }


}
