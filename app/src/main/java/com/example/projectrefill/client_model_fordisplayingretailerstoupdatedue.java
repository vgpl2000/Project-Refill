package com.example.projectrefill;

public class client_model_fordisplayingretailerstoupdatedue {
    String name;
    String due_amt;

    public client_model_fordisplayingretailerstoupdatedue() {
    }

    public client_model_fordisplayingretailerstoupdatedue(String due_amt, String name) {
        this.due_amt = due_amt;
        this.name = name;
    }

    public String getDue_amt() {
        return due_amt;
    }

    public void setDue_amt(String due_amt) {
        this.due_amt = due_amt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
