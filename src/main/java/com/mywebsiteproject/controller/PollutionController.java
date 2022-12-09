package com.mywebsiteproject.controller;


import com.mywebsiteproject.service.PollutionService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@RequestMapping("/pollution")
public class PollutionController {
    private static final Logger LOGGER = Logger.getLogger(PollutionController.class.getName());

    JSONObject response;
    private final PollutionService pollutionService;

    PollutionController(PollutionService pollutionService) {
        this.pollutionService = pollutionService;
    }

    @GetMapping("/current/city")
    // http://localhost:8080/pollution/current/city?city=New%20York&state=New%20York&country=US
    public JSONObject getCurrentPollutionDataByCityStateCountry(
            @RequestParam(value = "city") String city,
            @RequestParam(value = "state") String state,
            @RequestParam(value = "country") String country
    ) {
        try {
            response = pollutionService.getCurrentPollutionDataByCityStateCountry(city, state, country);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "/current/city response: {0}", response);
        return response;
    }

}