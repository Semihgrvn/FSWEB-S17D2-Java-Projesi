package com.workintech.dependencyproject.validation;


import com.workintech.dependencyproject.mapping.DeveloperResponse;
import com.workintech.dependencyproject.model.Developer;

public class DeveloperValidation {

    public static boolean isIdValid(int id) {
        return id > 0;
    }

    public static boolean isDeveloperValid(Developer developer){
       return isIdValid(developer.getId()) &&
               developer.getName() != null && !developer.getName().isEmpty() &&
               developer.getSalary()> 25000;
    }

}
