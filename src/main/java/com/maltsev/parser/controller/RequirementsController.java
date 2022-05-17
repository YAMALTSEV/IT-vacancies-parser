package com.maltsev.parser.controller;

import com.maltsev.parser.model.Frameworks;
import com.maltsev.parser.model.Languages;
import com.maltsev.parser.model.Requirements;
import com.maltsev.parser.repository.FrameworksRepos;
import com.maltsev.parser.repository.LanguageRepos;
import com.maltsev.parser.repository.RequirementsRepos;
import com.maltsev.parser.service.frameworks.iFrameworks;
import com.maltsev.parser.service.programmingLanguage.iLanguages;
import com.maltsev.parser.service.requirements.iRequirements;
import com.maltsev.parser.service.vacanciesSite.DjinniCo;
import com.maltsev.parser.service.vacanciesSite.JobsUa;
import com.maltsev.parser.service.vacanciesSite.WorkUa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Controller
public class RequirementsController {
    @Autowired
    FrameworksRepos frameworksRepos;
    @Autowired
    LanguageRepos languageRepos;
    @Autowired
    RequirementsRepos requirementsRepos;

    SimpleDateFormat formatter= new SimpleDateFormat("MMMM-yyyy");
    Date date = new Date(System.currentTimeMillis());

    @GetMapping("/requirements")
    public String requirementsPage (Model model){
        ArrayList<String> requirementsName = requirementsRepos.selectRequirementsArray();
        ArrayList<Integer> requirementsAmount = requirementsRepos.selectRequirementsAmountArray();

        model.addAttribute("requirementsName", requirementsName);
        model.addAttribute("requirementsAmount", requirementsAmount);
        return "requirements-popularity";
    }

    @GetMapping("/requirements/{date}")
    public String pickRequirementsStatsByDate(@RequestParam(name = "date") String date,
                                           Model model){
        ArrayList<String> requirements = requirementsRepos.selectRequirementsArrayWhereDateIs(date);
        ArrayList<Integer> requirementsAmount = requirementsRepos.selectRequirementsAmountArrayWhereDateIs(date);

        model.addAttribute("requirements", requirements);
        model.addAttribute("requirementsAmount", requirementsAmount);
        return "requirements-popularity";
    }

    @PostMapping("/requirements/refresh-tables")
    public String refreshTables() throws ExecutionException, InterruptedException {
        Set<String> allDescriptionsSet = new HashSet<>();
        allDescriptionsSet.addAll(new WorkUa().returnAllDescriptions());

        String [] frameworks = iFrameworks.frameworks;
        String [] languages = iLanguages.languages;
        String [] requirements = iRequirements.requirements;

        for(int i = 0; i < frameworks.length; i++){
            Frameworks frameworks1 = new Frameworks(frameworks[i], new WorkUa().frameworksCounter(frameworks[i], allDescriptionsSet), formatter.format(date));
            frameworksRepos.save(frameworks1);
        }

        for (int i = 0; i < languages.length; i++){
            Languages languages1 = new Languages(languages[i], new WorkUa().languagesCounter(languages[i], allDescriptionsSet), formatter.format(date));
            languageRepos.save(languages1);
        }

        for (int i = 0; i < requirements.length; i++){
            Requirements requirements1 = new Requirements(requirements[i], new WorkUa().frameworksCounter(requirements[i], allDescriptionsSet), formatter.format(date));
            requirementsRepos.save(requirements1);
        }

        return "redirect:/requirements";
    }

}
