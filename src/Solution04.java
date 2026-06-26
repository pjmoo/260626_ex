import oop.App;
import oop.AppProvider;
import oop.AppService;

public class Solution04 {
    public static void main(String[] args) {
        AppProvider appProvider = new AppProvider();
        AppService appService = new AppService(appProvider);
        App app = new App(appService);
        app.run();
    }
}
