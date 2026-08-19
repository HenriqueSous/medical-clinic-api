package com.henrique.medical_clinic_api.exception.model;

public class BusinessExcption extends RuntimeException {
  public BusinessExcption(String message) {
    super(message);
  }
}
