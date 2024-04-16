//By Cirilo418@gmail.com
//This little class is free of use and modification
//v1.0
//general class to read inputs
//Don't forget to call close() in the parent class when you don't need scanner anymore
//v1.1   2023/03/06
//Fix the bug related with reading a number (or boolean) and right after a String causing that the buffered \n to skip the String read
   //happens also when no S.O.P is called between the two readings
//clear buffer realocated
import java.util.Scanner;

public class Input {
   
   //inicialize the scanner
   private Scanner input = new Scanner(System.in);
   //to keep a register of the last input type, helps to avoid a bug when reading string from a previus int that skips the line
   private String lastInputType="firstTime";
   //method to capture integers, recives a string to display asking for the data type needed
   public int readInt(String text){
      //Declare variable to save the input
      int num=0;
      //To break the loop
      boolean correct=false;
      //Loop to asure that the corect type of entry is given
      do{
         //try in case of wrong type of date is given
         try{
            //print the message that the parent class passed
            System.out.println(text);
            //read
            num=input.nextInt();
            //this line is only executed when the previus was successfull, if not breaks the try and jumps to catch
            //if true change the variable to exit the loop
            correct=true;
         }
         //if the reading of data produces a error excute the next lines, catch the exception in the variable e
         catch(Exception e){
            //clean the buffer because this type of data doesnt catch the enter key and loops if not cleared
            clearBuffer();
            //execute the method to print a error massage
            unexpectedTypeData(); 
         }
      //if the flag to the captured input is false, try again capture it     
      }while(correct==false);
      //update the last type of data input
      lastInputType="int";
      //Return the captured integer
      return num;
   }
   
   //method to capture short numbers
   public short readShort(String text){   
      short num=0;
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            num=input.nextShort();
            correct=true;
         }
         catch(Exception e){
            clearBuffer();
            unexpectedTypeData(); 
         }
      }while(correct==false);
      lastInputType="String";
      return num;
   }
   //to capture float numbers
   public float readFloat(String text){
      float num=0;
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            num=input.nextFloat();
            correct=true;
         }
        catch(Exception e){
            clearBuffer();
            unexpectedTypeData(); 
         }
      }while(correct==false);
      lastInputType="float";
      return num;
   }
   
   //to capture double numbers
   public double readDouble(String text){
      double num=0;
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            num=input.nextDouble();
            correct=true;
         }
         catch(Exception e){
            clearBuffer();
            unexpectedTypeData(); 
         }
      }while(correct==false);
      lastInputType="double";
      return num;
   } 
   
   //to read characters
   public char readChar(String text){
      //checks if the last input was an integer to clear the buffer
      if(lastInputType=="int" || lastInputType=="short" || lastInputType=="float" || lastInputType=="double" || lastInputType=="boolean" ){
         clearBuffer();
      }
      char character=' ';
      //this variable is needed for later verification of lenght
      String string=" ";
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            //read a string of data
            string=input.nextLine();
            //verify if only one caracter was given
            if(string.length() > 1) {
               //if false print an error message
               System.out.println("Input too long!");
               //trow an exception to exit the try
               throw new Exception();        
            }
            //if the verificaion was true, convert the single caracter in string to a character type
            character=string.charAt(0);
            correct=true;
         }
         catch(Exception e){
            unexpectedTypeData(); 
         }
      }while(correct==false);
      lastInputType="int";
      return character;
   }
   
   public String readString(String text){
      if(lastInputType=="int" || lastInputType=="short" || lastInputType=="float" || lastInputType=="double" || lastInputType=="boolean" ){
         clearBuffer();
      }
      String string=" ";
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            string = input.nextLine();
            correct=true;
         }
         catch(Exception e){
            unexpectedTypeData();
         }
      }while(correct==false);
      lastInputType="String";
      return string;
   }
   
   public boolean readBoolean(String text){
      boolean bool=false;
      boolean correct=false;
      do{
         try{
            System.out.println(text);
            bool = input.nextBoolean();
            correct=true;
         }
         catch(Exception e){
            unexpectedTypeData();
            input.nextLine();
         }
      }while(correct==false);
      lastInputType="boolean";
      return bool;
   }  

   //method to print a error message in case of invalid type of data is capturated
   private void unexpectedTypeData(){
      System.out.println("\n------------------------\n| Error! Invalid entry |\n------------------------\n");
   } 
   
   //to clear the buffer
   private void clearBuffer(){
      input.nextLine();
   }
   //method to close the stream of scanner
   //to be called from the parent class
   public void close(){
      input.close();  
   }
}