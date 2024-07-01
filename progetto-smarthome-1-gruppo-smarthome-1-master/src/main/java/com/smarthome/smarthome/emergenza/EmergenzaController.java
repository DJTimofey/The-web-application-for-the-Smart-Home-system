package com.smarthome.smarthome.emergenza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/emergenze")
public class EmergenzaController
{
    private final EmergenzaService emergenzaService;

    @Autowired
    public EmergenzaController(EmergenzaService emergenzaService){this.emergenzaService = emergenzaService;}

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Emergenza>> getAllEmergenze()
    {
        List<Emergenza> le = emergenzaService.getAllEmergenze();

        return new ResponseEntity<>(le, HttpStatus.OK);
    }

    @GetMapping(path = "pending", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Emergenza>> getPendingEmergenze()
    {
        List<Emergenza> le = emergenzaService.getPendingEmergenze();

        return new ResponseEntity<>(le, HttpStatus.OK);
    }

    @PutMapping(path = "{emergenzaId}")
    public ResponseEntity<List<Emergenza>> updateEmergenza(@PathVariable("emergenzaId") Long emergenzaId)
    {
        emergenzaService.updateEmergenzaStatus(emergenzaId, true);

        List<Emergenza> le = emergenzaService.getPendingEmergenze();

        return new ResponseEntity<>(le, HttpStatus.OK);
    }
}
