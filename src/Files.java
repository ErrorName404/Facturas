import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class Files {
    private final int totalNumEntries = 100;
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
            System.out.print("----------------------------------------------------------------------------------------------------------\n");
            long i=0;
            long fileLength=file.length();
            while (i<fileLength) {
                idInFile = file.readInt();
                dateInFile = file.readUTF();
                clientInFile = file.readUTF();
                totalInFile = file.readFloat();
                isActiveInFile = file.readBoolean();//change later to false
                destInFile= file.readLong();
                System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                i=i+oneEntryBytes;
            }
            System.out.println("----------------------------------------------------------------------------------------------------------\n");
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
                long pointerPreviusDest=oneEntryBytes - destBytes;
                error.print("Overflow");
                foundID=0;
                long pointerBucket=0;
                boolean dataIsSaved=false;
                bucket.seek(pointerBucket);
                foundID = bucket.readInt();
                bucket.seek(pointerBucket);
                if (foundID == 0) {
                    dataPointer = (dataPointer + oneEntryBytes - destBytes);
                    file.seek(dataPointer);
                    file.writeLong(pointerBucket);
                    bucket.writeInt(data.getID());
                    bucket.writeUTF(data.getDate());
                    bucket.writeUTF(data.getClient());
                    bucket.writeFloat(data.getTotal());
                    bucket.writeBoolean(data.getIsActive());
                    bucket.writeLong(data.getDist());
                    dataIsSaved=true;
                }
                else {
                    pointerBucket = pointerBucket + oneEntryBytes;
                    while (pointerBucket < bucket.length()) {
                        bucket.seek(pointerBucket);
                        foundID = bucket.readInt();
                        if (foundID == 0) {
                            bucket.seek(pointerPreviusDest);
                            bucket.writeLong(pointerBucket);
                            bucket.seek(pointerBucket);
                            bucket.writeInt(data.getID());
                            bucket.writeUTF(data.getDate());
                            bucket.writeUTF(data.getClient());
                            bucket.writeFloat(data.getTotal());
                            bucket.writeBoolean(data.getIsActive());
                            bucket.writeLong(data.getDist());
                            dataIsSaved=true;
                            pointerBucket=bucketSize;
                        } else if (foundID == data.getID()) {
                            pointerPreviusDest = pointerBucket + oneEntryBytes - destBytes;
                        }
                        pointerBucket = pointerBucket + oneEntryBytes;
                    }
                }
                if(dataIsSaved=false) {
                   error.print("Bucket Full");
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
            System.out.print("----------------------------------------------------------------------------------------------------------\n");
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
            System.out.println("----------------------------------------------------------------------------------------------------------\n");
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


    public long find(File fileName,int totalEntries, int idToFind) {
        Error error= new Error();
        Input input= new Input();
        boolean anyEntryActive=false;
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile=-1;
        RandomAccessFile file=null;
        int pointer=oneEntryBytes*((idToFind%(totalEntries-1))-1);
        error.print("Main Area");
        try {
            file= new RandomAccessFile(fileName,"rw");
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------------------------------\n");
            file.seek(pointer);
            idInFile=file.readInt();
            dateInFile = file.readUTF();
            clientInFile = file.readUTF();
            totalInFile = file.readFloat();
            isActiveInFile = file.readBoolean();
            destInFile= file.readLong();
            if(idToFind==idInFile){
                System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                anyEntryActive=true;
            }
            System.out.println("----------------------------------------------------------------------------------------------------------\n");
            if(anyEntryActive==false){
                error.print("Data not found in "+fileName.getName());
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
        return destInFile;
    }
    public void findOverflow(File fileName,int totalEntries, int idToFind) {
        Error error= new Error();
        Input input= new Input();
        int idInFile;
        String dateInFile;
        String clientInFile;
        float totalInFile;
        boolean isActiveInFile;
        long destInFile;
        RandomAccessFile file=null;
        boolean anyEntryActive=false;
        int pointer=0;
        error.print("Bucket");
        try {
            file= new RandomAccessFile(fileName,"rw");
            System.out.printf("\n| %-10s | %-10s | %-38s | %-11s |%n", "ID", "Date", "Client","Total");
            System.out.print("----------------------------------------------------------------------------------------------------------\n");
            do{
                file.seek(pointer);
                idInFile=file.readInt();
                if(idToFind==idInFile){
                    dateInFile = file.readUTF();
                    clientInFile = file.readUTF();
                    totalInFile = file.readFloat();
                    isActiveInFile = file.readBoolean();
                    destInFile= file.readLong();
                    System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                    anyEntryActive=true;
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
                            System.out.printf("| %-10s | %-10s | %-38s | %-11.2f |%n", idInFile, dateInFile, clientInFile,totalInFile);
                        }
                        pointer = pointer + oneEntryBytes;
                    }
                }
                pointer = pointer + oneEntryBytes;
            }while(pointer < file.length());
            System.out.println("----------------------------------------------------------------------------------------------------------\n");
            if(anyEntryActive==false){
                error.print("Data not found in "+fileName.getName());
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

    public boolean checkEmpty(File fileName){
        int idInFile;
        String nameInFile;
        int grade1InFile;
        int grade2InFile;
        int grade3InFile;
        int grade4InFile;
        float finalGradeInFile;
        boolean isActiveInFile;
        long destInFile;
        RandomAccessFile file=null;
        try {
            file= new RandomAccessFile(fileName,"r");
            long i=oneEntryBytes;
            while (i<fileSize) {
                idInFile = file.readInt();
                nameInFile = file.readUTF();
                grade1InFile = file.readInt();
                grade2InFile = file.readInt();
                grade3InFile = file.readInt();
                grade4InFile = file.readInt();
                finalGradeInFile = file.readFloat();
                isActiveInFile = file.readBoolean();//change later to false
                destInFile= file.readLong();
                if (idInFile!=0) {
                    return true;
                }
                i=i+oneEntryBytes;
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
        return false;
    }
    public int getTotalSize(){ return totalNumEntries; }
    public int getBucketSize(){ return numEntriesBucket; }

}