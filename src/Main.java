import java.io.File;

public class Main {
    public static void main(String[] args) {
        Files file = new Files();
        Data data=new Data(false,0,"","",0.0f,true,-1);
        Input input = new Input();
        Error error = new Error();

        int mainMenuOption;
        String menuText= "Please, choose an option\n[1]- ADD\n[2]- PRINT ACTIVE ENTRIES\n[3]- SEARCH\n[8]- DELETE ALL\n[9]- EXIT";

        do {
            data=new Data(false,0,"","",0.0f,true,0);
            mainMenuOption = input.readInt(menuText);
            switch (mainMenuOption) {
                case 1 ->{
                    data.request();
                }
                case 2 -> {
                    File fileName=new File("data.dat");
                    file.printAllEntries(fileName);
                    fileName=new File("bucket.dat");
                    file.printAllEntries(fileName);

                    //old print all
                    /*File fileName=new File("data.dat");
                    if(fileName.exists()){
                        file.printAll(fileName);
                    }
                    else error.print("Error! empty file");

                    fileName=new File("bucket.dat");
                    if(fileName.exists()){
                        file.printAll(fileName);
                    }
                    else error.print("Error! empty bucket");*/

                }
                case 3 -> {
                    long overflow;
                    int idToFind = input.readInt("Please, input the value you want to find");
                    File fileName = new File("data.dat");
                    overflow = file.find(fileName, file.getTotalSize(), idToFind);
                    fileName = new File("bucket.dat");
                    if (overflow != -1) {
                        fileName = new File("bucket.dat");
                        file.findOverflow(fileName, file.getBucketSize(), idToFind);
                    }
                    else{
                        error.print("No entries active in "+fileName.getName());
                    }
                }
                /*case 4 -> {
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
                }*/
                case 8->{
                    int opc=0;
                    do {
                        opc = input.readInt("Are you sure you want to delete all entries?\n[1] - Yes,delete all\t\t[2] - Cancel");

                        switch (opc) {
                            case 1-> {
                                file.initialize();
                                System.out.println("All entries deleted");
                                System.out.println("\n");
                            }

                            case 2-> {
                                System.out.println("Operation canceled");
                            }

                            default -> {
                                error.print("Invalid option! Try again");
                            }
                        }
                    }while (opc<1||opc>2);
                }
                case 9->{}

                default -> error.print("ERROR, NOT AN OPTION!");
            }
        } while (mainMenuOption != 9);
        input.close();
    }
}

