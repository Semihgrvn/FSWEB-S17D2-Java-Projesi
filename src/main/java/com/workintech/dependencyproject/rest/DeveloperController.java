package com.workintech.dependencyproject.rest;

import com.workintech.dependencyproject.mapping.DeveloperResponse;
import com.workintech.dependencyproject.model.Developer;
import com.workintech.dependencyproject.model.JuniorDeveloper;
import com.workintech.dependencyproject.model.MidDeveloper;
import com.workintech.dependencyproject.model.SeniorDeveloper;
import com.workintech.dependencyproject.tax.Taxable;
import com.workintech.dependencyproject.validation.DeveloperValidation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.PushBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private Map<Integer, Developer> developers;
    private Taxable taxable;

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @Autowired
    public DeveloperController(@Qualifier("developerTax") Taxable taxable) {
        this.taxable = taxable;
    }

    @GetMapping("/")
    public List<Developer> get() {
        return developers.values().stream().toList();
    }

    @GetMapping("/{id}")
    public DeveloperResponse getById(@PathVariable int id) {
        if (!DeveloperValidation.isIdValid(id)) {
            return new DeveloperResponse(null, "Id is not valid", 400);
        }

        if(!developers.containsKey(id)){
            return new DeveloperResponse(null, "Id is not exist", 400);
        }
        return new DeveloperResponse(developers.get(id), "Success", 200);

    }

    @PostMapping("/")
    public DeveloperResponse save(@RequestBody Developer developer) {

        Developer savedDeveloper = createDeveloper(developer);
        if (savedDeveloper == null) {
            return new DeveloperResponse(null,
                    "null", 400);
        }
        if (developers.containsKey(developer.getId())) {
            return new DeveloperResponse(null,
                    "Developer with given id already exist: " + developer.getId(), 400);
        }
       if (!DeveloperValidation.isDeveloperValid(developer)) {
           return new DeveloperResponse(null,
                   "Developer credentials are not valid", 400);
       }
        developers.put(developer.getId(), savedDeveloper);
        return new DeveloperResponse(developers.get(developer.getId()), "success",201 );
    }

    @PutMapping("/{id}")
    public DeveloperResponse update(@PathVariable int id, @RequestBody Developer developer) {
        if (!developers.containsKey(id)) {
            return new DeveloperResponse(null,
                    "Developer with given id is not exist: " + id, 400);
        }
        developer.setId(id);
        Developer updatedDeveloper = createDeveloper(developer);

        if(updatedDeveloper == null){
            return new DeveloperResponse(null, "Id is not valid", 400);
        }

        if (!DeveloperValidation.isDeveloperValid(developer)) {
            return new DeveloperResponse(null,
                    "Developer credentials are not valid", 400);
        }

        developers.put(id, updatedDeveloper);
        return new DeveloperResponse(developers.get(id), "Success",200);
    }

    @DeleteMapping("/{id}")
    public DeveloperResponse delete(@PathVariable int id) {
        if (!developers.containsKey(id)) {
return new DeveloperResponse(null, "Developer with given id is not exist: "+ id , 400);
        }
        Developer developer = developers.get(id);
        developers.remove(id);
        return new DeveloperResponse(developer, "Success", 200);
    }


    private Developer createDeveloper(Developer developer) {
        Developer savedDeveloper;
        if (developer.getExperience().name().equalsIgnoreCase("junior")) {
            savedDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - developer.getSalary() * taxable.getSimpleTaxRate(),
                    developer.getExperience());

        } else if (developer.getExperience().name().equalsIgnoreCase("mid")) {
            savedDeveloper = new MidDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - developer.getSalary() * taxable.getMiddleTaxRate(),
                    developer.getExperience());

        } else if (developer.getExperience().name().equalsIgnoreCase("senior")) {

            savedDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(),
                    developer.getSalary() - developer.getSalary() * taxable.getUpperTaxRate(),
                    developer.getExperience());

        } else {
            savedDeveloper = null;
        }
        return savedDeveloper;
    }

}


