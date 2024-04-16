public class Error {
    public void print(String message){
        int length=(message.length())+4;
        String dashes="";
        for(int i=0;i<length;i++){
            dashes+="-";
        }
        System.out.println(dashes);
        System.out.println("| "+message+" |");
        System.out.println(dashes+"\n");
    }
}
