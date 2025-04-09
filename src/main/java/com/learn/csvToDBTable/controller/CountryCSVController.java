package com.learn.csvToDBTable.controller;

import com.learn.csvToDBTable.dto.ResponseMessage;
import com.learn.csvToDBTable.helper.CSVHelper;
import com.learn.csvToDBTable.model.CountryCSVModel;
import com.learn.csvToDBTable.service.CountryCSVService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class CountryCSVController {
    @Autowired
    private CountryCSVService countryCSVService;

    @PostMapping("/saveRecords")
    public ResponseEntity<ResponseMessage<String>> saveRecords(@RequestParam("file")MultipartFile file){
        ResponseMessage<String> resp = new ResponseMessage<>();
        if(CSVHelper.isCSV(file)){
            try{
                countryCSVService.saveRecordsInDB(file);
                String s = resp.getMessage();
                System.out.println(s);
                resp.setMessage("Uploaded the file into the DB successfully: "+ file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.OK).body(resp);
            } catch (Exception e){
                resp.setMessage("Could not save the file into the DB: "+file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            }
        }
        resp.setMessage("File you uploaded is not a CSV file, please upload a CSV file!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
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
