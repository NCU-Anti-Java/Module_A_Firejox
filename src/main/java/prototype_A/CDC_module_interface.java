package prototype_A;

import java.util.Vector;

/**
 * Created by firejox on 2015/12/14.
 */
public interface CDC_module_interface  {

    void addVirtualCharacter(int client_no) throws Throwable;
    void addItem(String name, int index,
                 boolean shared, int x, int y) throws Throwable;
    void updateDirection(int client_no, int move_code) throws Throwable;
    void getItem(int client_no) throws Throwable;
    Vector<dynamic_object_t> getUpdateInfo() throws Throwable;
    void startUpdatingThread() ;
}
