package com.learn.csvToDBTable.service;

import com.learn.csvToDBTable.helper.CSVHelper;
import com.learn.csvToDBTable.model.CountryCSVModel;
import com.learn.csvToDBTable.repository.CountryCSVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CountryCSVService {
    @Autowired
    private CountryCSVRepository countryCSVRepository;

    public void saveRecordsInDB(MultipartFile file) throws IOException {
        // Headers are correct
        if(CSVHelper.isMatchingHeaders(file.getInputStream())) {
            List<CountryCSVModel> countries = CSVHelper.csvToCountryRecordChunk(file.getInputStream(), -1, -1, false, false);
            countryCSVRepository.saveAll(countries);
        } else{
            // Headers and it's data are jumbled
            InputStream correctedStream = CSVHelper.rearrangeCsvColumnsForChunkRecords(file.getInputStream(), -1, -1, false);
            List<CountryCSVModel> countries = CSVHelper.csvToCountryRecordChunk(correctedStream, -1, -1, true, false);
            countryCSVRepository.saveAll(countries);
        }
    }

    public void saveRecordChunksInDB(MultipartFile file, int firstRecord, int lastRecord) throws IOException {
        if(CSVHelper.isMatchingHeaders(file.getInputStream())) {
            List<CountryCSVModel> countries = CSVHelper.csvToCountryRecordChunk(file.getInputStream(), firstRecord, lastRecord, false, true);
            countryCSVRepository.saveAll(countries);
        } else{
            InputStream correctedStream = CSVHelper.rearrangeCsvColumnsForChunkRecords(file.getInputStream(), firstRecord, lastRecord, true);
            List<CountryCSVModel> countries = CSVHelper.csvToCountryRecordChunk(correctedStream, firstRecord, lastRecord, true, true);
            countryCSVRepository.saveAll(countries);
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
