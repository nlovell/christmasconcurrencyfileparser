import parser.FileParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class runner {
    public static void main(String args[]){
        String fileToParse;
        FileParser fp =  FileParser.getInstance();
        fp.parseFile("C:/test/example.txt", true, true);
    }

    static String consoleReader(String reason) {
        //Read console
        try  {
            System.out.println("Console Input | " + reason);
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }
}
