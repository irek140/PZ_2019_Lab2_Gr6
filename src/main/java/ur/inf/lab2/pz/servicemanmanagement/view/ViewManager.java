package ur.inf.lab2.pz.servicemanmanagement.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ur.inf.lab2.pz.servicemanmanagement.config.StageConfig;
import ur.inf.lab2.pz.servicemanmanagement.view.exception.PlaceForComponentException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ViewManager {

    private ApplicationContext context;
    private StageConfig stageConfig;
    private Stage stage;

    private Parent actualLayout;
    private Layout actualLayoutType;

    public void init(Stage stage) throws IOException {
        this.stage = stage;

        Scene scene = new Scene(
                getView(stageConfig.getInitialView().getFxmlPath()),
                stageConfig.getMinWidth(),
                stageConfig.getMinHeight());
        this.stage.setScene(scene);

        this.stage.setTitle(stageConfig.getAppName());
        this.stage.setMinWidth(stageConfig.getMinWidth());
        this.stage.setMinHeight(stageConfig.getMinHeight());
        this.stage.setMaximized(stageConfig.getMaximized());
        this.stage.setResizable(stageConfig.getIsResizeable());
        this.stage.setFullScreen(stageConfig.getFullScreenEnabled());

        switchLayout(Layout.START, ViewComponent.LOGIN);

        this.stage.show();
    }

    private Node findChildNodeById(Parent parent, String id) {
        List<Node> nodes = parent.getChildrenUnmodifiable()
                .stream()
                .filter(node -> id.equals(node.getId()))
                .collect(Collectors.toList());

        if (nodes.size() == 0) {
            nodes = parent.getChildrenUnmodifiable()
                    .stream()
                    .flatMap(node -> ((Parent) node).getChildrenUnmodifiable().stream())
                    .filter(node -> id.equals(node.getId()))
                    .collect(Collectors.toList());

            return nodes.size() != 0 ? nodes.get(0) : null;

        } else return nodes.get(0);

    }

    public void loadComponent(ViewComponent component) throws IOException {
        AnchorPane placeForComponent = (AnchorPane) findChildNodeById(actualLayout, actualLayoutType.getMainComponentId());

        if (placeForComponent != null) {
            placeForComponent.getChildren().clear();

            Parent componentView = getView(component.getFxmlPath());
            fillToAnchorPane(componentView);

            placeForComponent.getChildren().add(componentView);
        } else throw new PlaceForComponentException("Nie mozna zaladować komponentu " + component.getFxmlPath());
    }

    public void switchLayout(Layout layout, ViewComponent initialComponent) throws IOException {
        Parent layoutComponent = getView(layout.getFxmlPath());
        Parent childComponent = getView(initialComponent.getFxmlPath());

        Node placeForComponent = findChildNodeById(layoutComponent, layout.getMainComponentId());

        if (placeForComponent != null) {

            fillToAnchorPane(childComponent);
            ((AnchorPane) placeForComponent).getChildren().add(childComponent);

        } else throw new PlaceForComponentException("W komponencie: "
                + layout.getFxmlPath()
                + " - nie ma zdefiniowanego konteneru o id: "
                + layout.getMainComponentId());

        actualLayout = layoutComponent;
        actualLayoutType = layout;
        stage.getScene().setRoot(layoutComponent);
    }

    private void fillToAnchorPane(Parent childComponent) {
        AnchorPane.setTopAnchor(childComponent, 0.0);
        AnchorPane.setRightAnchor(childComponent, 0.0);
        AnchorPane.setBottomAnchor(childComponent, 0.0);
        AnchorPane.setLeftAnchor(childComponent, 0.0);
    }

//    public void show(ViewType viewType) throws IOException {
//        Parent view = getView(viewType.getFXMLName());
//        stage.getScene().setRoot(view);
//    }

//    public void showEmptyPanelWithLogin() throws IOException {
//        AnchorPane emptyLayout = (AnchorPane) getView(ViewType.EMPTY_LAYOUT.getFXMLName());
//        AnchorPane cardContainer = (AnchorPane) emptyLayout.getChildren()
//                .stream()
//                .filter(node -> "cardContainer".equals(node.getId()))
//                .collect(Collectors.toList()).get(0);
//
//        AnchorPane placeForComponent = (AnchorPane) cardContainer.getChildren().stream().filter(node -> "placeForComponent".equals(node.getId()))
//                .collect(Collectors.toList()).get(0);
//
//        AnchorPane loginComponent = (AnchorPane) getView(ViewType.LOGIN.getFXMLName());
//
//        AnchorPane.setTopAnchor(loginComponent, 0.0);
//        AnchorPane.setBottomAnchor(loginComponent, 0.0);
//        AnchorPane.setLeftAnchor(loginComponent, 0.0);
//        AnchorPane.setRightAnchor(loginComponent, 0.0);
//
//        placeForComponent.getChildren().add(loginComponent);
//
//        stage.getScene().setRoot(emptyLayout);
//    }
//
//    public void showManagerPanelWithInitDashboard() throws IOException {
//        AnchorPane layout = (AnchorPane) getView(ViewType.MANAGER_PANEL_LAYOUT.getFXMLName());
//        List<Node> placeForComp = layout.getChildren()
//                .stream()
//                .filter(node -> "placeForComponent".equals(node.getId()))
//                .collect(Collectors.toList());
//
//        AnchorPane placeForComponent = (AnchorPane) placeForComp.get(0);
//        AnchorPane dashboard = (AnchorPane) getView(ViewType.MANAGER_DASHBOARD.getFXMLName());
//        AnchorPane.setTopAnchor(dashboard, 0.0);
//        AnchorPane.setBottomAnchor(dashboard, 0.0);
//        AnchorPane.setLeftAnchor(dashboard, 0.0);
//        AnchorPane.setRightAnchor(dashboard, 0.0);
//
//        placeForComponent.getChildren().add(dashboard);
//
//
//        AnchorPane sidenavContainer = (AnchorPane) layout.getChildren()
//                .stream()
//                .filter(node -> "sideNavContainer".equals(node.getId()))
//                .collect(Collectors.toList())
//                .get(0);
//
//
//        List<Node> collect = sidenavContainer.getChildren()
//                .stream()
//                .filter(node -> "placeForSidenav".equals(node.getId()))
//                .collect(Collectors.toList());
//        AnchorPane placeForSidenav = (AnchorPane) collect.get(0);
//        AnchorPane managerSideNav = (AnchorPane) getView(ViewType.MANAGER_SIDENAV.getFXMLName());
//
//        AnchorPane.setTopAnchor(managerSideNav, 0.0);
//        AnchorPane.setBottomAnchor(managerSideNav, 0.0);
//        AnchorPane.setLeftAnchor(managerSideNav, 0.0);
//        AnchorPane.setRightAnchor(managerSideNav, 0.0);
//
//        placeForSidenav.getChildren().add(managerSideNav);
//
//        stage.getScene().setRoot(layout);
//
//    }

    public void openDialog(ViewComponent viewComponent) throws IOException {
        try {
            Parent dialog = getView(viewComponent.getFxmlPath());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(dialog));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Parent getView(String viewName) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        loader.setLocation(getClass().getResource("/fxml/" + viewName + ".fxml"));

        return (Parent) loader.load();
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setStageConfig(StageConfig stageConfig) {
        this.stageConfig = stageConfig;
    }
}
