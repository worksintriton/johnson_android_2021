package com.triton.johnson.arraylist;

/**
 * Created by Iddinesh.
 */

public class PriorityList {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String status,  message,  id,  name;
    public PriorityList(String status, String message, String id, String name) {


            this.status = status;

            this.message = message;

            this.id = id;

            this.name = name;

    }
}
