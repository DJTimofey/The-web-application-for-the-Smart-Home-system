package com.smarthome.smarthome.rilevation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/rilevation")
public class RilevationController
{
    private final RilevationService rilevationService;

    @Autowired
    public RilevationController(RilevationService rilevationService){
        this.rilevationService = rilevationService;
    }

    @GetMapping(path= "/temperature", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Rilevation> getLastTemperature()
    {
        Rilevation r = rilevationService.getLastTemperaturRilevation();

        return new ResponseEntity<>(r, HttpStatus.OK);
    }

}