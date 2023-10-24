import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

public class JSONtoCSV {
    public static void main(String[] args) {
        convert("import.json", "output.csv");
    }

    public static void convert(String jsonFilePath, String csvFilePath) {
        try {
            // Чтение JSON файла
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(new FileReader(jsonFilePath)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("rows");

            // Создание CSV файла с кодировкой Windows-1251 и разделителем ";"
            FileOutputStream fos = new FileOutputStream(csvFilePath);
            Writer csvWriter = new OutputStreamWriter(fos, Charset.forName("Windows-1251"));
            CSVWriter writer = new CSVWriter(csvWriter, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

            // Запись заголовков
            String[] headers = {
                    "customerLocationAddress",
                    "customerLocationName",
                    "latitude",
                    "longitude",
                    "contactNumber",
                    "id",
                    "contactEmail",
                    "webSite",
                    "additionalTimePerVisit",
                    "fixedOperationDuration",
                    "dropDurationPerUnit",
                    "pickupDurationPerUnit"
            };
            writer.writeNext(headers);

            // Запись данных
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject dataObject = jsonArray.get(i).getAsJsonObject();
                JsonObject addressObject = dataObject.getAsJsonObject("address");

                String customerLocationAddress = getFieldValue(addressObject, "address", "");
                String customerLocationName = getFieldValue(addressObject, "name", "");
                String latitude = String.valueOf(getDoubleField(addressObject, "latitude", 0.0));
                String longitude = String.valueOf(getDoubleField(addressObject, "longitude", 0.0));
                String contactNumber = getFieldValue(dataObject, "phone", "");
                String id = getFieldValue(dataObject, "referenceNumber", "");
                String contactEmail = getFieldValue(dataObject, "email", "");
                String webSite = getFieldValue(dataObject, "website", "");

                String additionalTimePerVisit = getNonZeroValue(dataObject, "additionalTimePerVisit", 60);
                String fixedOperationDuration = getNonZeroValue(dataObject, "fixedOperationDuration", 60);
                String dropDurationPerUnit = getNonZeroValue(dataObject, "dropDurationPerUnit", 60000);
                String pickupDurationPerUnit = getNonZeroValue(dataObject, "pickupDurationPerUnit", 60000);

                String[] values = {
                        customerLocationAddress,
                        customerLocationName,
                        latitude,
                        longitude,
                        contactNumber,
                        id,
                        contactEmail,
                        webSite,
                        additionalTimePerVisit,
                        fixedOperationDuration,
                        dropDurationPerUnit,
                        pickupDurationPerUnit
                };

                writer.writeNext(values);
            }

            writer.close();
            csvWriter.close();
            System.out.println("Преобразование JSON в CSV завершено успешно.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNonZeroValue(JsonObject jsonObject, String fieldName, int divisor) {
        if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()) {
            int value = jsonObject.get(fieldName).getAsInt();
            if (value != 0) {
                return String.valueOf(value / divisor);
            }
        }
        return "";
    }

    private static String getFieldValue(JsonObject jsonObject, String fieldName, String defaultValue) {
        if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()) {
            return jsonObject.get(fieldName).getAsString();
        }
        return defaultValue;
    }

    private static double getDoubleField(JsonObject jsonObject, String fieldName, double defaultValue) {
        if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()) {
            return jsonObject.get(fieldName).getAsDouble();
        }
        return defaultValue;
    }
}
