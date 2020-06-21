package com.powerdropship.powerdropship.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * Helper class to wrap a list of jobs. This is used for saving the
 * list of jobs to XML.
 */

@XmlRootElement(name = "jobs")
public class JobListWrapper {

    private List<Job> jobs;

    @XmlElement(name = "job")
    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
