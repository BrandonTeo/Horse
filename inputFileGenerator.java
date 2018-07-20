import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


//This program generates an input instance based on the size we give it
public class inputFileGenerator {
    public static void main(String[] args) {
        int size = 8;
        try {
            FileWriter writer = new FileWriter("4.in", true);
            writer.write(8 + "");
            writer.write("\r\n");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i == j) {
                        Random r = new Random();
                        int low = 0;
                        int high = 99;
                        int result = r.nextInt(high-low) + low;
                        writer.write(result + " ");
                    } else if (Math.random() < 0.5) {
                        writer.write(0 + " ");
                    } else {
                        writer.write(1 + " ");
                    }
                } 
                writer.write("\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

