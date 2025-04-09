package com.learn.csvToDBTable.service;

import com.learn.csvToDBTable.helper.CSVHelper;
import com.learn.csvToDBTable.model.CountryCSVModel;
import com.learn.csvToDBTable.repository.CountryCSVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CountryCSVService {
    @Autowired
    private CountryCSVRepository countryCSVRepository;

    public void saveRecordsInDB(MultipartFile file) {
        try{
            // We convert to input stream because it is efficient than loading the whole file as bytes into the memory
            // With input stream we can read and process the file incremantally like line by line or record by record
            // instead of waiting for the entire file to be loaded
            List<CountryCSVModel> countries = CSVHelper.csvToCountry(file.getInputStream());
            countryCSVRepository.saveAll(countries);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the records in the DB - " + e.getMessage());
        }
    }

    public List<CountryCSVModel> getAllRecords() {
        try {
           return countryCSVRepository.findAll();
        } catch (Exception e){
            throw new RuntimeException("Failed to retrieve the records from the DB");
        }
    }
}
