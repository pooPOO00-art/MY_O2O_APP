package com.example.my_o2o_app.model;

import java.util.List;

public class ExpertResponse {
    private boolean success;
    private List<Expert> experts;

    public boolean isSuccess() {
        return success;
    }

    public List<Expert> getExperts() {
        return experts;
    }
}
