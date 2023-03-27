import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Result {

    public static void generateFiles(String input_file_name) throws IOException {
        int dotIndex = input_file_name.lastIndexOf(".");
        String fileName = null;
        if (dotIndex >= 0) {
            fileName = input_file_name.substring(0, dotIndex);
            String extension = input_file_name.substring(dotIndex + 1);
            if (!extension.equals("csv")) {
                System.out.println("Invalid file: " + input_file_name);
                return;
            }
        } else {
            System.out.println("No extension found for file: " + input_file_name);
            return;
        }

        List<String[]> data = csvReader(input_file_name);

        try {
            averageQty(data, "0_" + fileName + ".csv");
            popularBrand(data, "1_" + fileName + ".csv");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void popularBrand(List<String[]> data, String outputFileName) throws IOException {
        Map<String, Map<String, Integer>> brandOrders = data.stream()
                .collect(Collectors.groupingBy(row -> row[2], // group by product
                        Collectors.groupingBy(row -> row[4], // group by brand
                                Collectors.summingInt(row -> Integer.parseInt(row[3]))))); // sum orders

        Map<String, String> result = brandOrders.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // product
                        entry -> {
                            Map<String, Integer> productOrders = entry.getValue();
                            return productOrders.entrySet().stream()
                                    .max(Map.Entry.comparingByValue()) // find max orders
                                    .map(Map.Entry::getKey)
                                    .orElse("");
                        }
                ));

        BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFileName));
        result.forEach((key, value) -> {
            try {
                bw.write(key + "," + value + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
    }

    public static void averageQty(List<String[]> data, String outputFileName) throws IOException {
        int numOfRecord = data.size();
        Map<String, Double> result = data.stream()
                .collect(Collectors.groupingBy(
                        row -> row[2],
                        Collectors.summingDouble(row -> Double.parseDouble(row[3]))
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / numOfRecord
                ));


        BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFileName));
        result.forEach((key, value) -> {
            try {
                bw.write(key + "," + value + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
    }

    public static List<String[]> csvReader(String input_file_name){

        String splitBy = ",";
        List<String[]> data = null;
        try {
            data = Files.lines(Paths.get(input_file_name))
                    .map(line -> line.split(splitBy))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

}
