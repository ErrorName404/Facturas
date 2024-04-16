public class Data {
    private boolean exists=false;
    private int id=0;
    private String date="";
    private String client="";
    private float total=-1.1f;
    private boolean isActive=true;
    private long dist=0;

    public Data(boolean exists, int id, String date, String client, float total, boolean isActive, long dist){
        this.exists=exists;
        this.id=id;
        this.date=date;
        this.client=client;
        this.total=total;
        this.isActive=isActive;
        this.dist=dist;
    }

    public boolean getExists(){ return exists; }
    public int getID(){ return id; }
    public String getDate(){ return date; }
    public String getClient(){ return client; }
    public float getTotal(){ return total; }
    public boolean getIsActive(){ return isActive; }
    public long getDist(){ return dist; }



    public void setExists(boolean newExists){ this.exists=newExists; }
    public void setID(int newID){ this.id=newID; }
    public void setDate(String newDate){ this.date=newDate; }
    public void setClient(String newClient){ this.client=newClient; }
    public void setTotal(float newTotal){ this.total=newTotal; }
    public void setIsActive(boolean newIsActive){
        this.isActive=newIsActive;
    }
    public void setDist(long newDist){
        this.dist=newDist;
    }

    public void request(){
        Input input = new Input();
        Data data=new Data(false,0,"","",1.1f,true,0);
        Files file= new Files();
        do {
            data=new Data(false,0,"","",1.1f,true,0);
            data.setID(input.readInt("Please, input the bill ID")) ;
            if(data.getID()==0) {
                System.out.println("\n------------------------------------------------------\n" +
                        "| Error! ID number 0 is reserved, try a different one |" +
                        "\n------------------------------------------------------\n");
            }
            if(data.getID()<0) {
                System.out.println("\n------------------------------------------------------\n" +
                        "| Error! ID cannot be a negative number, try a different one |" +
                        "\n------------------------------------------------------\n");
            }
        } while (data.getID()==0||data.getID()<0);

        do{
            data.setDate(input.readString("Please, input the date of the bill"));
            if(data.getDate().isEmpty()){
                System.out.println("\n------------------------------------------------------------\n" +
                        "| The date can't be empty, try a different one |" +
                        "\n------------------------------------------------------------\n");
            }
            if(data.getDate().length()>10){
                System.out.println("\n------------------------------------------------------------\n" +
                        "| The date can't be longer than 10 characters |" +
                        "\n------------------------------------------------------------\n");
            }
            else if(data.getDate().length()>0&&data.getDate().length()<10){
                String auxDate=data.getDate();
                do {
                    auxDate+=" ";
                }while (auxDate.length()<10);
                data.setDate(auxDate);
                System.out.println(data.getDate());
            }
        }while(data.getDate().isEmpty()||data.getDate().length()>10);

        do{
            data.setClient(input.readString("Please, input the name of the client"));
            if(data.getClient().isEmpty()){
                System.out.println("\n------------------------------------------------------------\n" +
                        "| The name can't be empty, try a different one |" +
                        "\n------------------------------------------------------------\n");
            }
            if(data.getClient().length()>38){
                System.out.println("\n------------------------------------------------------------\n" +
                        "| The name can't be longer than 38 characters |" +
                        "\n------------------------------------------------------------\n");
            }
            else if(data.getClient().length()>0&&data.getClient().length()<38){
                String auxClient=data.getClient();
                do {
                    auxClient+=" ";
                }while (auxClient.length()<38);
                data.setClient(auxClient);
            }
        }while(data.getClient().isEmpty()||data.getClient().length()>38);

        do{
            data.setTotal(input.readFloat("Please, input the total of the bill"));
            if(data.getTotal()<0){
                System.out.println("\n------------------------------------------------------------\n" +
                        "| The grade can't be a negative number, try a different one |" +
                        "\n------------------------------------------------------------\n");
            }
        }while(data.getTotal()<0);

        data.setIsActive(true);

        data.setDist(0);

        file.write(data);
    }
}
