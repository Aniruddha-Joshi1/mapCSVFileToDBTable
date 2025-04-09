package com.learn.csvToDBTable.repository;

import com.learn.csvToDBTable.model.CountryCSVModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryCSVRepository extends JpaRepository<CountryCSVModel, UUID> {
}
