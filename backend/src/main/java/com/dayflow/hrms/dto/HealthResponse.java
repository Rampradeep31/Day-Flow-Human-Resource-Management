package com.dayflow.hrms.dto;

public class HealthResponse {
    private String status;
    private String service;

    public HealthResponse() {
    }

    public HealthResponse(String status, String service) {
        this.status = status;
        this.service = service;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public static class Builder {
        private String status;
        private String service;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public HealthResponse build() {
            return new HealthResponse(status, service);
        }
    }
}
