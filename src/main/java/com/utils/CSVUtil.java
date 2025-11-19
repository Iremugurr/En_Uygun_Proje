package com.utils;


import com.models.FlightData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CSVUtil {
    private static final Logger logger = LogManager.getLogger(CSVUtil.class);

    public static String writeFlightDataToCSV(List<FlightData> flightDataList, String route) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = route.replace(" - ", "_") + "_" + timestamp + ".csv";
        String filePath = ConfigReader.getCsvOutputFolder() + "/" + fileName;

        try {
            new File(filePath).getParentFile().mkdirs();

            FileWriter fileWriter = new FileWriter(filePath);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                    .withHeader("Departure Time", "Arrival Time", "Airline", "Price",
                            "Connection", "Duration"));

            for (FlightData flight : flightDataList) {
                csvPrinter.printRecord(
                        flight.getDepartureTime(),
                        flight.getArrivalTime(),
                        flight.getAirlineName(),
                        flight.getPrice(),
                        flight.getConnectionInfo(),
                        flight.getDuration()
                );
            }

            csvPrinter.flush();
            csvPrinter.close();

            logger.info("Flight data written to CSV: " + filePath);
            return filePath;

        } catch (IOException e) {
            logger.error("Failed to write CSV file: " + e.getMessage());
            return null;
        }
    }
}