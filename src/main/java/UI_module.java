/**
 * Created by firejox on 2015/12/7.
 */


import com.sun.istack.internal.NotNull;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import prototype_B.TCPC_module;
import prototype_D.DO_module;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


interface keycode_operation {
    void handle() ;
}


class module_operation {

    //     TCP Client Module keycode operation
    static keycode_operation TCPC(TCPC_module mod, int move_code) {
        assert mod != null : "module can not be null";

        return ()-> mod.inputMoves(move_code);
    }

    //     Dynamic Object Module keycode operation
    static keycode_operation DO(DO_module mod) {
        assert mod != null : "module can not be null";

        return mod::keyGETPressed;
    }
}


public class UI_module extends Application implements
        EventHandler<KeyEvent>, UI_module_interface {

    //the map of operation
    private Map<KeyCode, keycode_operation> op = new EnumMap<>(KeyCode.class);
    private Scene scene;
    private Pane root;



    private static final keycode_operation default_op = ()->{};


    /**
     * link_DO_module - link the Dynamic Object Module
     * @param mod - Dynamic Object Module
     * */
    @Override
    public void link_DO_module(@NotNull DO_module mod) {
        assert (mod != null) : "DO module should not be null";

        this.op.put(KeyCode.ENTER, module_operation.DO(mod));
    }


    /**
     * link_TCPC_module - link the TCP Client Module
     * @param mod - TCP Client Module
     *
     * */
    @Override
    public void link_TCPC_module(@NotNull TCPC_module mod) {
        assert (mod != null) : "TCPC module should not be null";

        System.out.println(mod);

        // build the keycode mapping

        this.op.put(KeyCode.UP,
                module_operation.TCPC(mod, TCPC_module.TURNNORTH));

        this.op.put(KeyCode.DOWN,
                module_operation.TCPC(mod, TCPC_module.TURNSOUTH));

        this.op.put(KeyCode.RIGHT,
                module_operation.TCPC(mod, TCPC_module.TURNEAST));

        this.op.put(KeyCode.LEFT,
                module_operation.TCPC(mod, TCPC_module.TURNWEST));

    }


    /**
     * keycode_operate - handle according to the keycode
     *  if the keycode does not in the map then do nothing.
     *
     * @param code - the keycode which the User pressed.
     * */
    void keycode_operate (KeyCode code) {
        this.op.getOrDefault(code, default_op).handle();
    }


    /**
     * handle - to get the key press event
     *
     * @param event - key event
     * */
    public void handle(KeyEvent event) {
        keycode_operate(event.getCode());
    }


    /**
     * @see javafx.application.Application
     *
     * */
    @Override
    public void start(Stage primaryStage) throws Exception {

        init_UI(primaryStage);
    }

    /**
     * init_UI - load the GUI structure from window.fxml
     * @param primaryStage - for show up the window
     *
     *
     * Note. All the GUI structure is in window.fxml
     * */
    private void init_UI(Stage primaryStage) throws IOException {
        root = (Pane)FXMLLoader.load(getClass().getResource("window.fxml"));

        scene = new Scene(root);

        scene.setOnKeyPressed(this);

        primaryStage.setScene(scene);


        primaryStage.show();

    }


    @Override
    public Pane get_Pane() {
        return root;
    }


}
