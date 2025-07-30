package com.example.my_o2o_app.model;

public class ExpertEstimate {
    private int expertEstimateId;
    private int expertId;
    private String companyName;
    private String profileImage;
    private int price;
    private String message;
    private float rating;
    private int reviewCount;

    // Getter
    public int getExpertEstimateId() { return expertEstimateId; }
    public int getExpertId() { return expertId; }
    public String getCompanyName() { return companyName; }
    public String getProfileImage() { return profileImage; }
    public int getPrice() { return price; }
    public String getMessage() { return message; }
    public float getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
}
