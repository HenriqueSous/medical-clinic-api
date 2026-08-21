package com.henrique.medical_clinic_api.exception;

public class SpecialtyNotFoundException extends RuntimeException {
  public SpecialtyNotFoundException(String message) {
    super(message);
  }
}
