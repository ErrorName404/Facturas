import java.io.File;

public class Main {
    public static void main(String[] args) {
        Files file = new Files();
        Data data=new Data(false,0,"","",0.0f,true,-1);
        Input input = new Input();
        Error error = new Error();

        int mainMenuOption;
        String menuText= "Please, choose an option\n[1]- CREATE REGISTRY\n[2]- PRINT ALL\n[3]- ADD\n[4]- PRINT ACTIVE ENTRIES\n[5]- SEARCH\n[9]- EXIT";

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
                case 5-> {
                    long overflow;
                    int idToFind = input.readInt("Please, input the value you want to find");
                    File fileName = new File("data.dat");
                    overflow = file.find(fileName, file.getTotalSize(), idToFind);
                    if (overflow != -1) {
                        fileName = new File("bucket.dat");
                        file.findOverflow(fileName, file.getBucketSize(), idToFind);
                    }
                    else{
                        error.print("No entries active in "+fileName.getName());
                    }
                }
                case 9->{}

                default -> error.print("ERROR, NOT AN OPTION!");
            }
        } while (mainMenuOption != 9);
        input.close();
    }
}

