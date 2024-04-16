import java.io.File;

public class Main {
    public static void main(String[] args) {
        Files file = new Files();
        Data data=new Data(false,0,"","",0.0f,true,0);
        Input input = new Input();
        Error error = new Error();

        int mainMenuOption;
        String menuText= "Please, choose an option\n[1]- CREATE REGISTRY\n[2]- PRINT ALL\n[3]- ADD\n[4]- PRINT ACTIVE ENTRIES\n[9]- EXIT";

        do {
            data=new Data(false,0,"","",0.0f,true,0);
            mainMenuOption = input.readInt(menuText);
            switch (mainMenuOption) {
                case 1 ->{
                    file.initialize();
                    System.out.println("File created");
                    System.out.println("\n");
                }
                case 2 -> {
                    File fileName=new File("data.dat");
                    if(fileName.exists()){
                        file.printAll(fileName);
                    }
                    else error.print("Error! empty file");

                    fileName=new File("bucket.dat");
                    if(fileName.exists()){
                        file.printAll(fileName);
                    }
                    else error.print("Error! empty bucket");

                }
                case 3 -> {
                    data.request();
                }
                case 4 -> {
                    File fileName=new File("data.dat");
                    file.printAllEntries(fileName);
                    fileName=new File("bucket.dat");
                    file.printAllEntries(fileName);
                }
                case 9->{}

                default -> error.print("ERROR, NOT AN OPTION!");
            }
        } while (mainMenuOption != 9);
        input.close();
    }
}

