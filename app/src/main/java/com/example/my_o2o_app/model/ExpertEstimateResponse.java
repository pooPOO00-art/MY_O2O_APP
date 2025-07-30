package com.example.my_o2o_app.model;

import java.util.List;

/**
 * /estimate/detail API 응답 매핑 클래스
 * 서버 JSON 구조:
 * {
 *   "success": true,
 *   "estimates": [ { ... ExpertEstimate ... } ]
 * }
 */
public class ExpertEstimateResponse {
    private boolean success;
    private List<ExpertEstimate> estimates;

    public boolean isSuccess() { return success; }
    public List<ExpertEstimate> getEstimates() { return estimates; }
}
