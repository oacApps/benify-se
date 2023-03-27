import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args)   throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String input_file_name = bufferedReader.readLine();

        Result.generateFiles(input_file_name);

        bufferedReader.close();
    }
}
