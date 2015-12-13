package prototype_A;

import javafx.scene.layout.Pane;
import prototype_B.TCPC_module;
import prototype_D.DO_module;

/**
 * Created by firejox on 2015/12/13.
 */
public interface UI_module_interface {

    void link_DO_module(DO_module mod);
    void link_TCPC_module(TCPC_module mod);
    Pane get_Pane();

}
