package com.shindhe.controller;

import com.shindhe.request.JobParamsRequest;
import com.shindhe.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
curl --location --request GET 'http://localhost:8080/api/job/start/First Job'
curl --location --request GET 'http://localhost:8080/api/job/start/Second Job'
 */

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    JobService jobService;

    @Autowired
    JobOperator jobOperator;

    @GetMapping("/start/{jobName}")
    public String startJob(@PathVariable String jobName, @RequestBody List<JobParamsRequest> jobParamsRequestList) throws Exception {
        jobService.startJob(jobName, jobParamsRequestList);
        return "Job Started...";
    }

    @GetMapping("/stop/{jobExecutionId}")
    public String stopJob(@PathVariable long jobExecutionId) {
        try {
            jobOperator.stop(jobExecutionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Job Stopped...";
    }

}


/*
curl --location --request GET 'http://localhost:8080/api/job/start/Second Job' \
--header 'Content-Type: application/json' \
--data-raw '[
    {
        "paramKey":"abc",
        "paramValue": "abc123"
    },
     {
        "paramKey":"xyz",
        "paramValue": "xyz123"
    }
]'

 */
