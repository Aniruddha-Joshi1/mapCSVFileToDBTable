package com.learn.csvToDBTable.helper;

import com.learn.csvToDBTable.controller.CountryCSVController;
import com.learn.csvToDBTable.model.CountryCSVModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CSVHelper {
    public static final String type = "text/csv";
    public static final String[] EXPECTED_HEADERS = {"Code", "Symbol", "Name"};
    public static final List<String> expectedHeaders = Arrays.stream(EXPECTED_HEADERS).toList();
    private static final Logger logger = LoggerFactory.getLogger(CSVHelper.class);

    public static boolean isCSV(MultipartFile file){
        return type.equals(file.getContentType());
    }

    public static int numberOfRows(InputStream is){
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String data;
            // Exclude headers while counting the number of lines
            int count = -1;
            while((data = fileReader.readLine())!=null && !data.equals("")){
                count++;
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException("Cannot count the number of rows");
        }
    }

    public static boolean isMatchingHeaders(InputStream is){
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
            String headerLine = fileReader.readLine();
            List<String> actualHeaders = Arrays.stream(headerLine.split(","))
                    .map(String::trim)
                    .toList();
            return expectedHeaders.equals(actualHeaders);
        } catch (IOException e){
            throw new RuntimeException("Cannot check for matching headers");
        }
    }

    public static List<CountryCSVModel> csvToCountryRecordChunk(InputStream correctedInputStream, int firstRecord, int lastRecord, boolean rearrangedColumns, boolean isSaveAsChunks) {
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(correctedInputStream))){
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.RFC4180.builder().setHeader()
                            .setSkipHeaderRecord(true)
                            .setTrim(true)
                            .setIgnoreEmptyLines(true)
                            .setIgnoreHeaderCase(true)
                            .setQuote('"')
                            .setIgnoreSurroundingSpaces(true)
                            .build()

            );
            logger.info("CSV Parsed successfully");
            List<CountryCSVModel> countries = new ArrayList<>();
            int index = 0;
            if(!rearrangedColumns && isSaveAsChunks){
                for(CSVRecord csvRecord:csvParser){
                    if(index>=firstRecord && index<=lastRecord){
                        CountryCSVModel country = new CountryCSVModel(
                                csvRecord.get("Code"),
                                csvRecord.get("Symbol"),
                                csvRecord.get("Name")
                        );
                        countries.add(country);
                    }
                    index++;
                }
                logger.info("Countries, btw first and last record index, added in the ArrayList succesfully");
            } else{
                for(CSVRecord csvRecord:csvParser){
                    CountryCSVModel country = new CountryCSVModel(
                            csvRecord.get("Code"),
                            csvRecord.get("Symbol"),
                            csvRecord.get("Name")
                    );
                    countries.add(country);
                }
                logger.info("All the countries added successfully");
            }
            return countries;
        } catch (IOException e){
            throw new RuntimeException("Failed to parse the CSV: " + e.getMessage());
        }
    }

    // In case the headers are not ordered, we use this to create a temp file with the correct headers and their corresponding data
    public static InputStream rearrangeCsvColumnsForChunkRecords(InputStream originalInputStream, int firstRecord, int lastRecord, boolean isSaveAsChunks) throws IOException{
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(originalInputStream, StandardCharsets.UTF_8))){
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IOException("CSV file is empty.");
            CSVParser parser = new CSVParser(reader,
                    CSVFormat.RFC4180.builder().setHeader(headerLine.split(","))
                            .setSkipHeaderRecord(false)
                            .setTrim(true)
                            .setIgnoreHeaderCase(true)
                            .setIgnoreEmptyLines(true)
                            .setQuote('"')
                            .setIgnoreSurroundingSpaces(true)
                            .build());
            logger.info("CSV Parsed successfully for rearranging the columns");
            Path tempFile = Files.createTempFile("corrected-recordChunk-csv", ".csv");
            System.out.println("Temporary directory: " + System.getProperty("java.io.tmpdir"));
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180);
                printer.printRecord(EXPECTED_HEADERS);

                // Reorder fields for each record
                if(isSaveAsChunks){
                    int index = 0;
                    for (CSVRecord record : parser) {
                        if(index>=firstRecord && index<=lastRecord){
                            List<String> reorderedFields = Arrays.stream(EXPECTED_HEADERS)
                                    .map(header -> record.get(header))
                                    .toList();
                            printer.printRecord(reorderedFields);
                        }
                        index++;
                    }
                } else{
                    for(CSVRecord record : parser){
                        List<String> reorderedFields = Arrays.stream(EXPECTED_HEADERS)
                                .map(header -> record.get(header))
                                .toList();
                        printer.printRecord(reorderedFields);
                    }
                }
            }
            logger.info("Created a temporary file successfully");
            return Files.newInputStream(tempFile);
        } catch(IOException e){
            throw new RuntimeException("Cannot rearrange the columns: " + e.getMessage());
        }
    }

//    public static List<CountryCSVModel> csvToCountry(InputStream correctedInputStream) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(correctedInputStream, StandardCharsets.UTF_8))) {
//            CSVParser csvParser = new CSVParser(reader,
//                    CSVFormat.RFC4180.builder().setHeader()
//                            .setSkipHeaderRecord(true)
//                            .setTrim(true)
//                            .setIgnoreHeaderCase(true)
//                            .setIgnoreEmptyLines(true)
//                            .setQuote('"')
//                            .setIgnoreSurroundingSpaces(true)
//                            .build());
//            List<CountryCSVModel> countries = new ArrayList<>();
////            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
//            // changed from csvRecords to csvParser because according to documentation, we can use csvParser instead of storing all
//            // records in memory using Iterable<CSVRecord>
//            for(CSVRecord csvRecord : csvParser){
//                CountryCSVModel country = new CountryCSVModel(
//                        csvRecord.get("Code"),
//                        csvRecord.get("Symbol"),
//                        csvRecord.get("Name")
//                );
//                countries.add(country);
//            }
//            return countries;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse CSV: " + e.getMessage());
//        }
//    }

//    public static InputStream rearrangeCsvColumns(InputStream originalInputStream) throws IOException {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(originalInputStream, StandardCharsets.UTF_8))) {
//            // Once you read the first line, it can never be read again. First line -> headers
//            String headerLine = reader.readLine();
//            if (headerLine == null) throw new IOException("CSV file is empty.");
//            // Now that we have read the headers, it will store all the data except headers. So if we set the skipHeaderRecord as true then it will skip the data row which is just below
//            // the headers, so we should set it as false.
//            // If I don't set any headers - then the parser uses he first data row as the headers and in the for loop when we looping through the expected headers we have -> "Code",
//            // "Symbol", "Name" but it will be expecting headers of the first data row.
//            CSVParser parser = new CSVParser(reader,
//                    CSVFormat.RFC4180.builder().setHeader(headerLine.split(","))
//                            .setSkipHeaderRecord(false)
//                            .setTrim(true)
//                            .setIgnoreHeaderCase(true)
//                            .setIgnoreEmptyLines(true)
//                            .setQuote('"')
//                            .setIgnoreSurroundingSpaces(true)
//                            .build());
//            Path tempFile = Files.createTempFile("corrected-csv", ".csv");
//            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
//                // If we use the below thing, then incase of actually having ',' inside the data when we have to use double quotes to wrap it, it will consider this comma as well (which
//                // is wrapped inside the double quotes). We don't want that to happen.
//
////                writer.write(String.join(",", EXPECTED_HEADERS));
//
//                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.RFC4180);
//                printer.printRecord(EXPECTED_HEADERS);
//
//                // Reorder fields for each record
//                for (CSVRecord record : parser) {
//                    List<String> reorderedFields = Arrays.stream(EXPECTED_HEADERS)
//                            .map(header -> record.get(header))
//                            .toList();
//                    printer.printRecord(reorderedFields);
//                }
//            }
//            return Files.newInputStream(tempFile);
//        }
//    }
}
