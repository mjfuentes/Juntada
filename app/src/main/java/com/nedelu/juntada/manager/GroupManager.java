package com.nedelu.juntada.manager;

import com.nedelu.juntada.model.Group;

import java.util.List;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class GroupManager {

    private static GroupManager instance;
    private List<Group> groups;

    private GroupManager (){
    }

    public static GroupManager getInstance(){
        if (instance == null){
            instance = new GroupManager();
        }
        return  instance;
    }

    public void setGroups(List<Group> groups){
        this.groups = groups;
    }

    public List<Group> getGroups(){
        return this.groups;
    }

    public void addGroup(Group group){
        this.groups.add(group);
    }

}
