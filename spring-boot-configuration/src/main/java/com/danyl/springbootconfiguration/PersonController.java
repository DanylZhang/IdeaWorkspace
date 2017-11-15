package com.danyl.springbootconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties(Person.class)
public class PersonController {

    @Autowired
    private Person person;

    @GetMapping
    public Person person(){
        return person;
    }
}