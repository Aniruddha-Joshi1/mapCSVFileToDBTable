package com.learn.csvToDBTable.helper;

import com.learn.csvToDBTable.model.CountryCSVModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CSVHelper {
    public static final String type = "text/csv";

    public static boolean isCSV(MultipartFile file){
        return type.equals(file.getContentType());
    }

    public static List<CountryCSVModel> csvToCountry(InputStream is){
        // try with resource
        try(InputStreamReader fileReader = new InputStreamReader(is, "UTF-8")){
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).setIgnoreHeaderCase(true).build());
            List<CountryCSVModel> countries = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for(CSVRecord csvRecord : csvRecords){
                CountryCSVModel country = new CountryCSVModel(
                  csvRecord.get("code"),
                  csvRecord.get("Symbol"),
                  csvRecord.get("Name")
                );
                countries.add(country);
            }
            return countries;
        } catch (IOException e){
            throw new RuntimeException("Failed to parse the csv file - " + e.getMessage());
        }
    }
}
