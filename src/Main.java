import service.FileImportService;

public class Main {

    public static void main(String[] args) {
        FileImportService fileImportService = new FileImportService();
        fileImportService.load("3");
    }
}
