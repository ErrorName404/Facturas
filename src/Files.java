import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class Files {
    private final int totalNumEntries = 50;
    private final int idBytes=4;
    private final int dateBytes=12;
    private final int clientBytes=40;//38 spaces + 2 UTF
    private final int totalBytes=4;
    private final int isActiveBytes=1;
    private final int destBytes=8;
    private final int oneEntryBytes=idBytes+dateBytes+clientBytes+totalBytes+isActiveBytes+destBytes;
    private final int fileSize = oneEntryBytes * totalNumEntries;
    private final int numEntriesBucket =(int)Math.ceil((double)totalNumEntries / 4);//to round up 25% of total entries
    private final int bucketSize = oneEntryBytes * numEntriesBucket;
    public void initialize() {
        int emptyID = 0;
        String emptyDate = "          ";//10 characters
        String emptyClient = "                                      ";//38 characters
        float emptyTotal = 0.0f;
        boolean emptyIsActive = false;
        long emptyDest = -1;

        RandomAccessFile file=null;
        RandomAccessFile bucket=null;

        int i=1;

        try {
            file= new RandomAccessFile("data.dat","rw");
            bucket= new RandomAccessFile("bucket.dat","rw");
            file.setLength(fileSize);
            do{
                file.writeInt(emptyID);
                file.writeUTF(emptyDate);
                file.writeUTF(emptyClient);
                file.writeFloat(emptyTotal);
                file.writeBoolean(emptyIsActive);
                file.writeLong(emptyDest);
                i++;
            }while (i<=totalNumEntries);
            i=1;
            bucket.setLength(bucketSize);
            do{
                bucket.writeInt(emptyID);
                bucket.writeUTF(emptyDate);
                bucket.writeUTF(emptyClient);
                bucket.writeFloat(emptyTotal);
                bucket.writeBoolean(emptyIsActive);
                bucket.writeLong(emptyDest);
                i++;
            }while(i<=numEntriesBucket);
        } catch (IOException e) {
            //System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                file.close();
                bucket.close();
            } catch (Exception e) {
            }
        }

    }

    public void printAll(File fileName) {
        Error error= new Error();
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile;
        RandomAccessFile file=null;
        try {
            file= new RandomAccessFile(fileName,"rw");
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------\n");
            long pointer=0;
            long fileLength=file.length();
            while (pointer<fileLength) {
                idInFile = file.readInt();
                dateInFile = file.readUTF();
                clientInFile = file.readUTF();
                totalInFile = file.readFloat();
                isActiveInFile = file.readBoolean();//change later to false
                destInFile= file.readLong();
                System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                pointer=pointer+oneEntryBytes;
            }
            System.out.println("----------------------------------------------------------------------------------\n");
        } catch (Exception e) {
            System.out.println("Error: "+e);

        } finally {
            try {
                file.close();

            } catch (Exception b) {
                System.out.println("Error: "+b);
            }
        }
    }

    public void write(Data data){
        Error error=new Error();
        RandomAccessFile file=null;
        RandomAccessFile bucket=null;

        long dataPointer=0;
        int i=1;
        File dataName = new File("data.dat");
        File bucketName = new File("bucket.dat");
        int foundID;
        long foundDest;
        try {
            file= new RandomAccessFile("data.dat","rw");
            bucket= new RandomAccessFile("bucket.dat","rw");
            if(dataName.exists()){}
            else{
                initialize();
            }
            dataPointer=oneEntryBytes*((data.getID()%(totalNumEntries-1))-1);
            file.seek(dataPointer);
            foundID= file.readInt();
            file.seek(dataPointer+ oneEntryBytes - destBytes);
            foundDest= file.readLong();
            file.seek(dataPointer);
            if(foundID==0){
                file.writeInt(data.getID());
                file.writeUTF(data.getDate());
                file.writeUTF(data.getClient());
                file.writeFloat(data.getTotal());
                file.writeBoolean(data.getIsActive());
                file.writeLong(data.getDist());
            }
            else {
                //long pointerPreviusDest=oneEntryBytes - destBytes;
                error.print("Overflow");
                foundID = 0;
                long pointerBucket = 0;

                //boolean dataIsSaved=false;

                if (foundDest == -1) {
                    while (pointerBucket < bucket.length()) {
                        bucket.seek(pointerBucket);
                        foundID = bucket.readInt();
                        bucket.seek(pointerBucket);
                        if (foundID == 0) {
                            file.seek(dataPointer + oneEntryBytes - destBytes);
                            file.writeLong(pointerBucket);
                            bucket.writeInt(data.getID());
                            bucket.writeUTF(data.getDate());
                            bucket.writeUTF(data.getClient());
                            bucket.writeFloat(data.getTotal());
                            bucket.writeBoolean(data.getIsActive());
                            bucket.writeLong(data.getDist());
                            pointerBucket = bucketSize;
                        }
                        pointerBucket = pointerBucket + oneEntryBytes;
                    }
                } else {
                    long pointerNextWrite=0;
                    pointerBucket = foundDest;
                    long previusDest = foundDest;
                    while (pointerBucket < bucket.length()) {
                        bucket.seek(pointerBucket);
                        foundID = bucket.readInt();
                        bucket.seek(pointerBucket + oneEntryBytes - destBytes);
                        foundDest = bucket.readLong();
                        bucket.seek(pointerBucket);
                        if (foundDest == -1) {
                            pointerNextWrite = pointerBucket+oneEntryBytes;
                            previusDest = pointerBucket;
                            pointerBucket = bucketSize;
                        } else {
                            previusDest = pointerBucket;
                            pointerBucket = foundDest;
                        }
                    }
                    pointerBucket=pointerNextWrite;
                    boolean dataSaved=false;
                    while (pointerBucket < bucket.length()) {
                        bucket.seek(pointerBucket);
                        foundID = bucket.readInt();
                        bucket.seek(pointerBucket);
                        if (foundID == 0) {
                            bucket.seek(previusDest + oneEntryBytes - destBytes);
                            bucket.writeLong(pointerBucket);
                            bucket.seek(pointerBucket);
                            bucket.writeInt(data.getID());
                            bucket.writeUTF(data.getDate());
                            bucket.writeUTF(data.getClient());
                            bucket.writeFloat(data.getTotal());
                            bucket.writeBoolean(data.getIsActive());
                            bucket.writeLong(data.getDist());
                            pointerBucket = bucketSize;
                            dataSaved=true;
                        }
                        pointerBucket = pointerBucket + oneEntryBytes;
                    }
                    if (!dataSaved) {
                        error.print("Bucket Full");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                file.close();
                bucket.close();
            } catch (Exception e) {
            }
        }

    }

    public void printAllEntries(File fileName) {
        Error error= new Error();
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile;
        RandomAccessFile file=null;
        boolean anyEntryActive=false;
        try {
            file= new RandomAccessFile(fileName,"rw");
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------\n");
            long i=0;
            while (i<file.length()) {
                idInFile = file.readInt();
                dateInFile = file.readUTF();
                clientInFile = file.readUTF();
                totalInFile = file.readFloat();
                isActiveInFile = file.readBoolean();
                destInFile= file.readLong();
                if(idInFile!=0&&isActiveInFile==true){
                    System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                    anyEntryActive=true;
                }
                i=i+oneEntryBytes;
            }
            System.out.println("----------------------------------------------------------------------------------\n");
            if(anyEntryActive==false){
                error.print("No entries active in "+fileName.getName());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);

        } finally {
            try {
                file.close();

            } catch (Exception b) {
                System.out.println("Error: "+b);
            }
        }
    }


    public boolean find(File fileName,int totalEntries, int idToFind, boolean silenced) {
        Error error= new Error();
        Input input= new Input();
        boolean exists=false;
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile=-1;
        RandomAccessFile file=null;
        int pointer=oneEntryBytes*((idToFind%(totalEntries-1))-1);
        if(!silenced) error.print("Main Area");
        try {
            file= new RandomAccessFile(fileName,"rw");
            if(!silenced) System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            if(!silenced) System.out.print("----------------------------------------------------------------------------------\n");
            file.seek(pointer);
            idInFile=file.readInt();
            dateInFile = file.readUTF();
            clientInFile = file.readUTF();
            totalInFile = file.readFloat();
            isActiveInFile = file.readBoolean();
            destInFile= file.readLong();
            if(idToFind==idInFile&&isActiveInFile){
                if(!silenced) System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                exists=true;
            }
            else if(idToFind==idInFile&&!isActiveInFile){
                System.out.println("Entry Deactivated!");
                exists=true;
            }
            if(!silenced) System.out.println("----------------------------------------------------------------------------------\n");
            if(!exists){
                if(!silenced)error.print("Data not found in "+fileName.getName());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);

        } finally {
            try {
                file.close();

            } catch (Exception b) {
                System.out.println("Error: "+b);
            }
        }
        return exists;
    }
    public boolean findOverflow(File fileName,int totalEntries, int idToFind, boolean silenced) {
        Error error= new Error();
        Input input= new Input();
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile;
        RandomAccessFile file=null;
        boolean exists=false;
        int pointer=0;
        if(!silenced)error.print("Bucket");
        try {
            file= new RandomAccessFile(fileName,"rw");
            if(!silenced)System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            if(!silenced)System.out.print("----------------------------------------------------------------------------------\n");
            do{
                file.seek(pointer);
                idInFile=file.readInt();
                file.seek(pointer+oneEntryBytes-destBytes-isActiveBytes);
                isActiveInFile=file.readBoolean();
                file.seek(pointer);
                if(idToFind==idInFile&&isActiveInFile){
                    dateInFile = file.readUTF();
                    clientInFile = file.readUTF();
                    totalInFile = file.readFloat();
                    isActiveInFile = file.readBoolean();
                    destInFile= file.readLong();
                    if(!silenced)System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                    exists=true;
                    pointer = pointer + oneEntryBytes;
                    while (pointer < file.length()&&destInFile!=-1) {
                        file.seek(pointer);
                        idInFile = file.readInt();
                        if (idInFile==idToFind) {
                            dateInFile = file.readUTF();
                            clientInFile = file.readUTF();
                            totalInFile = file.readFloat();
                            isActiveInFile = file.readBoolean();
                            destInFile= file.readLong();
                            if(!silenced)System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                            pointer=fileSize;
                        }
                        pointer = pointer + oneEntryBytes;
                    }
                }
                else if(idToFind==idInFile&&!isActiveInFile){
                    System.out.println("Entry Deactivated!");
                    exists=true;
                    pointer=fileSize;
                }
                pointer = pointer + oneEntryBytes;
            }while(pointer < file.length());
            if(!silenced)System.out.println("----------------------------------------------------------------------------------\n");
            if(!exists){
                if(!silenced)error.print("Data not found in "+fileName.getName());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);

        } finally {
            try {
                file.close();

            } catch (Exception b) {
                System.out.println("Error: "+b);
            }
        }
        return exists;
    }

    public void deactivate(Data data) {
        Error error = new Error();
        String menuText = "\nAre you sure you want to deactivate this entry?\n[1]- YES\n[2]- CANCEL\n";
        Input input = new Input();

        RandomAccessFile file = null;
        RandomAccessFile bucket = null;
        long dataPointer = 0;
        int i = 1;
        File dataName = new File("data.dat");
        File bucketName = new File("bucket.dat");
        int foundID;
        long foundDest;
        boolean foundActive;
        int opc;
        int IdToFind = data.requestID();
        try {
            file = new RandomAccessFile("data.dat", "rw");
            bucket = new RandomAccessFile("bucket.dat", "rw");
            if (dataName.exists()) {
            } else {
                initialize();
            }
            dataPointer = oneEntryBytes * ((IdToFind % (totalNumEntries - 1)) - 1);
            file.seek(dataPointer);
            foundID = file.readInt();
            file.seek(dataPointer+oneEntryBytes- destBytes - isActiveBytes);
            foundActive = file.readBoolean();
            file.seek(dataPointer + oneEntryBytes - destBytes);
            foundDest = file.readLong();
            file.seek(dataPointer);

            if (foundID == IdToFind && foundActive) {
                do {
                    opc = input.readInt(menuText);
                    switch (opc) {
                        case 1 -> {
                            file.seek(dataPointer + oneEntryBytes - destBytes - isActiveBytes);
                            file.writeBoolean(false);
                            error.print("ENTRY DEACTIVATED!");
                        }
                        case 2 -> {
                            error.print("DEACTIVATION CANCELED!");
                        }
                        default -> error.print("ERROR, NOT AN OPTION!");
                    }
                } while (opc < 1||opc>2);
            } else if (foundID == IdToFind && !foundActive) {
                error.print("Already deactivated!");
            } else {
                //long pointerPreviusDest=oneEntryBytes - destBytes;
                //error.print("Overflow");
                foundID = 0;
                long pointerBucket = 0;

                //boolean dataIsSaved=false;

                if (foundDest == -1) {
                    error.print("Entry not found");
                } else {
                    long pointerNextWrite = 0;
                    pointerBucket = foundDest;
                    //long previusDest = foundDest;
                    while (pointerBucket < bucket.length()) {
                        bucket.seek(pointerBucket);
                        foundID = bucket.readInt();
                        bucket.seek(pointerBucket + oneEntryBytes - destBytes);
                        foundDest = bucket.readLong();
                        bucket.seek(pointerBucket);
                        if (foundID == IdToFind && foundActive) {
                            opc = input.readInt(menuText);
                            do {
                                switch (opc) {
                                    case 1 -> {
                                        bucket.seek(pointerBucket + oneEntryBytes - destBytes - isActiveBytes);
                                        bucket.writeBoolean(false);
                                        error.print("ENTRY DEACTIVATED!");
                                        pointerBucket = bucketSize;
                                    }
                                    case 2 -> {
                                        error.print("DEACTIVATION CANCELED!");
                                    }
                                    default -> error.print("ERROR, NOT AN OPTION!");
                                }
                            } while (opc < 1||opc > 2);
                            pointerBucket = bucketSize;
                        } else if (foundID == IdToFind && !foundActive) {
                            error.print("Already deactivated!");
                            pointerBucket = bucketSize;
                        } else {
                            if (foundDest == -1) {
                                error.print("Entry not found");
                                pointerBucket = bucketSize;
                            } else {
                                pointerBucket = foundDest;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                file.close();
                bucket.close();
            } catch (Exception e) {
            }
        }
    }
        public void modify(Data data){
            Error error = new Error();
            String menuText = "\nPlease, choose the field you want to modify\n[1]- DATE\n[2]- NAME\n[3]- TOTAL";
            Input input = new Input();

            RandomAccessFile file = null;
            RandomAccessFile bucket = null;
            long dataPointer = 0;
            int i = 1;
            File dataName = new File("data.dat");
            File bucketName = new File("bucket.dat");
            int foundID;
            long foundDest;
            int opc;
            int IdToFind = data.requestID();
            try {
                file = new RandomAccessFile("data.dat", "rw");
                bucket = new RandomAccessFile("bucket.dat", "rw");
                if (dataName.exists()) {
                } else {
                    initialize();
                }
                dataPointer = oneEntryBytes * ((IdToFind % (totalNumEntries - 1)) - 1);
                file.seek(dataPointer);
                foundID = file.readInt();
                file.seek(dataPointer + oneEntryBytes - destBytes);
                foundDest = file.readLong();
                file.seek(dataPointer);

                if (foundID == IdToFind) {
                    opc = input.readInt(menuText);
                    do {
                        switch (opc) {
                            case 1 -> {
                                String newDate = data.requestDate();
                                file.seek(dataPointer + idBytes);
                                file.writeUTF(newDate);
                            }
                            case 2 -> {
                                String newName = data.requestName();
                                file.seek(dataPointer + idBytes + dateBytes);
                                file.writeUTF(newName);
                            }
                            case 3 -> {
                                float newTotal = data.requestTotal();
                                file.seek(dataPointer + idBytes + dateBytes + clientBytes);
                                file.writeFloat(newTotal);
                            }
                            default -> error.print("ERROR, NOT AN OPTION!");
                        }
                    } while (opc < 1 || opc > 3);
                    error.print("Data updated!");
                } else {
                    //long pointerPreviusDest=oneEntryBytes - destBytes;
                    //error.print("Overflow");
                    foundID = 0;
                    long pointerBucket = 0;

                    //boolean dataIsSaved=false;

                    if (foundDest == -1) {
                        error.print("Entry not found");
                    } else {
                        long pointerNextWrite = 0;
                        pointerBucket = foundDest;
                        //long previusDest = foundDest;
                        while (pointerBucket < bucket.length()) {
                            bucket.seek(pointerBucket);
                            foundID = bucket.readInt();
                            bucket.seek(pointerBucket + oneEntryBytes - destBytes);
                            foundDest = bucket.readLong();
                            bucket.seek(pointerBucket);
                            if (foundID == IdToFind) {
                                opc = input.readInt(menuText);
                                do {
                                    switch (opc) {
                                        case 1 -> {
                                            String newName = data.requestName();
                                            file.seek(dataPointer + idBytes);
                                            file.writeUTF(newName);
                                        }
                                        case 2 -> {
                                            String newDate = data.requestDate();
                                            file.seek(dataPointer + idBytes + clientBytes);
                                            file.writeUTF(newDate);
                                        }
                                        case 3 -> {
                                            float newTotal = data.requestTotal();
                                            file.seek(dataPointer + idBytes + clientBytes + dateBytes);
                                            file.writeFloat(newTotal);
                                        }
                                        default -> error.print("ERROR, NOT AN OPTION!");
                                    }
                                } while (opc < 1 || opc > 3);
                                error.print("Data updated!");
                                pointerBucket = bucketSize;
                            } else {
                                if (foundDest == -1) {
                                    error.print("Entry not found");
                                    pointerBucket = bucketSize;
                                } else {
                                    pointerBucket = foundDest;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    file.close();
                    bucket.close();
                } catch (Exception e) {
                }
            }

    }
    public void delete(Data data){
        RandomAccessFile historical=null;
        RandomAccessFile file=null;
        RandomAccessFile bucket=null;

        Error error= new Error();
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile;

        int emptyID = 0;
        String emptyDate = "          ";//10 characters
        String emptyClient = "                                      ";//38 characters
        float emptyTotal = 0.0f;
        boolean emptyIsActive = false;
        long emptyDest = -1;
        boolean anyEntryDeactivated=false;
        long historicalPointer;
        try {
            historical= new RandomAccessFile("historical.dat","rw");
            file= new RandomAccessFile("data.dat","rw");
            bucket = new RandomAccessFile("bucket.dat","rw");
            long pointer=0;
            error.print("Data.dat");
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------\n");
            while (pointer<file.length()) {
                file.seek(pointer);
                idInFile = file.readInt();
                dateInFile = file.readUTF();
                clientInFile = file.readUTF();
                totalInFile = file.readFloat();
                isActiveInFile = file.readBoolean();
                destInFile= file.readLong();

                if(!isActiveInFile&&idInFile!=0) {

                    historicalPointer = historical.length();
                    historical.seek(historicalPointer);
                    historical.writeInt(idInFile);
                    historical.writeUTF(dateInFile);
                    historical.writeUTF(clientInFile);
                    historical.writeFloat(totalInFile);
                    historical.writeBoolean(isActiveInFile);
                    historical.writeLong(destInFile);

                    System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile, totalInFile);

                    file.seek(pointer);
                    file.writeInt(emptyID);
                    file.writeUTF(emptyDate);
                    file.writeUTF(emptyClient);
                    file.writeFloat(emptyTotal);
                    file.writeBoolean(emptyIsActive);
                    file.writeLong(emptyDest);
                    anyEntryDeactivated = true;
                }
                pointer = pointer + oneEntryBytes;
            }
            System.out.println("----------------------------------------------------------------------------------\n");
            if(!anyEntryDeactivated){
                error.print("No inactive entries in data.dat");
            }
            error.print("Bucket.dat");
            pointer=0;
            anyEntryDeactivated=false;
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------\n");
            while (pointer<bucket.length()) {
                bucket.seek(pointer);
                idInFile = bucket.readInt();
                dateInFile = bucket.readUTF();
                clientInFile = bucket.readUTF();
                totalInFile = bucket.readFloat();
                isActiveInFile = bucket.readBoolean();
                destInFile= bucket.readLong();

                if(!isActiveInFile&&idInFile!=0) {

                    historicalPointer = historical.length();
                    historical.seek(historicalPointer);
                    historical.writeInt(idInFile);
                    historical.writeUTF(dateInFile);
                    historical.writeUTF(clientInFile);
                    historical.writeFloat(totalInFile);
                    historical.writeBoolean(isActiveInFile);
                    historical.writeLong(destInFile);

                    System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile, totalInFile);

                    bucket.seek(pointer);
                    bucket.writeInt(emptyID);
                    bucket.writeUTF(emptyDate);
                    bucket.writeUTF(emptyClient);
                    bucket.writeFloat(emptyTotal);
                    bucket.writeBoolean(emptyIsActive);
                    bucket.writeLong(emptyDest);
                    anyEntryDeactivated = true;
                }
                pointer = pointer + oneEntryBytes;
            }
            System.out.println("----------------------------------------------------------------------------------\n");
            if(!anyEntryDeactivated){
                error.print("No inactive entries in bucket.dat");
            }
            else error.print("Entries deleted");
        } catch (Exception e) {
            System.out.println("Error: "+e);

        } finally {
            try {
                file.close();
                bucket.close();
                historical.close();
            } catch (Exception b) {
                System.out.println("Error: "+b);
            }
        }
    }
    public int getTotalSize(){ return totalNumEntries; }
    public int getBucketSize(){ return numEntriesBucket; }

}