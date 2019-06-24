package com.upc.gessi.spring.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BugzillaBugsSchema implements Serializable {

    List<BugzillaBug> bugs= new ArrayList<BugzillaBug>();

    public Set<String> getStakeholders() {
        Set<String> name = new HashSet<String>();
        Set<String> id = new HashSet<String>();
        for (BugzillaBug bug : bugs) {
            if (bug.getAssigned_to()!=null) {
                name.add(bug.getAssigned_to());
            }
        }
        return name;
    }

    public List<Responsible> getResponsibles(String user) {
        List<Responsible> resp = new ArrayList<Responsible>();
        for (BugzillaBug bug : bugs) {
            if (bug.getAssigned_to().equals(user)) {
                Responsible re = new Responsible();
                re.setPerson(user);
                re.setRequirement(bug.getId());
                resp.add(new Responsible());
            }
        }
        return resp;
    }

    public List<BugzillaBug> getBugs() {
        return bugs;
    }

    public void setBugs(List<BugzillaBug> bugs) {
        this.bugs = bugs;
    }

    public void addBugs(BugzillaBugsSchema toFuse) {
        if (toFuse.bugs!=null) {
            this.bugs.addAll(toFuse.bugs);
        }
    }

}
