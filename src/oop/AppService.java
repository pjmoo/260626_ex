package oop;

public class AppService {
    private AppProvider appProvider;

    public AppService(AppProvider appProvider) {
        this.appProvider = appProvider;
    }

    //    public void run() throws Exception {
    public void run() {
        System.out.println("AppService.run");
        try {
            appProvider.run();
        } catch (Exception e) {
            System.out.println("e.getClass() = " + e.getClass());
            System.out.println("AppService.run.exception.catch");
        }
        System.out.println("AppService.run.complete");
    }
}