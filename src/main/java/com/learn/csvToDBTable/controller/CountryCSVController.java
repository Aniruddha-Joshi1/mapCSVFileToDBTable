package com.learn.csvToDBTable.controller;

import com.learn.csvToDBTable.appConstants.AppConstants;
import com.learn.csvToDBTable.dto.ResponseMessage;
import com.learn.csvToDBTable.helper.CSVHelper;
import com.learn.csvToDBTable.model.CountryCSVModel;
import com.learn.csvToDBTable.service.CountryCSVService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import static com.learn.csvToDBTable.helper.CSVHelper.isCSV;

@RestController
public class CountryCSVController {
    @Autowired
    private CountryCSVService countryCSVService;

    @PostMapping("/saveRecords")
    public ResponseEntity<ResponseMessage<String>> saveRecords(@RequestPart("file") MultipartFile file) {
        ResponseMessage<String> resp = new ResponseMessage<>();
        // check if csv or not
        if (!isCSV(file)) {
            resp.setMessage("Please upload a CSV file!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
        try{
            countryCSVService.saveRecordsInDB(file);
            resp.setMessage("File uploaded successfully: " + file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (Exception e) {
            resp.setMessage("Error processing file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @PostMapping("saveRecordsInChunks")
    public ResponseEntity<ResponseMessage<String>> saveRecordsInChunks(@RequestPart("file") MultipartFile file,
                                                                       @RequestParam int firstRecord,
                                                                       @RequestParam int lastRecord){
        ResponseMessage<String> resp = new ResponseMessage<>();
        try{
            int diff = lastRecord - firstRecord;
            int numberOfRows = CSVHelper.numberOfRows(file.getInputStream());
            if(numberOfRows<lastRecord) throw new RuntimeException("Last record is greater than number of rows available");
            if(firstRecord<0) throw new RuntimeException("First Record cannot be less than 0");
            if(diff<= AppConstants.MAX_CHUNK_SIZE){
                countryCSVService.saveRecordChunksInDB(file, firstRecord, lastRecord);
                resp.setMessage(String.format("Record Number %d to %d uploaded successfully", firstRecord, lastRecord));
                return ResponseEntity.status(HttpStatus.OK).body(resp);
            } else{
                throw new RuntimeException("Cannot accept more than 50 records");
            }
        } catch (Exception e){
            resp.setMessage("Error in processing file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @GetMapping("/retrieveRecords")
    public ResponseEntity<ResponseMessage<List<CountryCSVModel>>> getAllRecords(){
        ResponseMessage<List<CountryCSVModel>> resp = new ResponseMessage<>();
        try {
            List<CountryCSVModel> countries = countryCSVService.getAllRecords();
            resp.setData(countries);
            resp.setMessage("Retrieved data successfully");
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (Exception e){
            System.out.println(e.getStackTrace());
            throw new RuntimeException("Failed to retrieve all the records from the DB");
        }
    }
}
