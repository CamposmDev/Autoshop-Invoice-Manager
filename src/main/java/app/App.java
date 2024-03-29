package app;

import controller.AppController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.Model;
import model.database.DB;
import model.ui.DialogFactory;
import model.ui.FX;

import java.io.IOException;

/**
 * What flags a work order to be added to {currOWOs}?
 * From the work order table controller, if the user double-clicks of clicks the edit button when a work order
 * is currently selected then before switching to work order workspace the program will add that selected work order
 * to {currOWOs}.
 *
 * What flags a work order to be removed from {currOWOs}?
 * If the user clicks either two buttons to 'save and close' or 'cancel and close' then the work order is removed
 * from {currOWOs}. Those are the only two ways to exit the workspace. But, since we have tabs now to display a
 * work order workspace we have to think about whether the user will close the tab itself. So, if the user does close
 * the work order workspace tab then the program will consider that as 'cancel and close' and remove the work order
 * from {currOWOs}.
 *
 * The list {currOWOs} could be saved on secondary storage, all the list would be only the id of the work order similar
 * to recent work orders. If the user closes (exit) the program, then {currOWOs} will be saved. And on start of the
 * program, {currOWOs} will be loaded into memory and automatically open the work orders in the process of being edited.
 *
 * The App class gives us access to the AppController which will allow us to modify the tab pane.
 * We can use that to append tabs of work orders to edit.
 *
 * Create a global list of integers called {currOWOs} which will contain work order ids to keep track of which
 * work orders we're currently editing.
 * Note: New work order ids will not be added to {currOWOs}
 *
 * Note: If a new work order is created, then it will open a new tab and will disable the menu item "New Work Order'
 * Why? Because then it will lead to conflicts to assigning work order ids when saving a new work order.
 * If a current work order is being edited then there's no need to disable the 'New Work Order' menu item.
 *
 * If the user tries to delete work order and if its id is in {currOWOs} then show an error message.
 *
 * Create a global list of work orders called {currOWOs} which is a list of integers.
 *
 * How many work orders should {currOWOs} keep track of?
 * As of now, I think it would be best to leave it as many as the user's local machine can handle.
 * @see controller.WorkOrderTableController
 * @see controller.WorkOrderWorkspaceController
 *
 *
 */
public class App extends Application {
    private static AppController controller;

    public static AppController get() {
        return controller;
    }

    @Override
    public void start(Stage stage) throws IOException {
        /* catch uncaught exceptions and give the user the choice to report it */
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            Exception ex = new Exception(throwable);
            DialogFactory.initError(ex);
        });
        FXMLLoader fxml = FX.load("App.fxml");
        stage = fxml.load();
        controller = fxml.getController();
        /* Restore previous session */
        controller.openCurrentWorkOrders();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * @brief Shutdowns the application and saves the model and closes the connection with the database.
     */
    public static void exit() {
        System.out.println("Exiting program...");
        Model.get().save();
        DB.get().close();
        Platform.exit();
    }
}
