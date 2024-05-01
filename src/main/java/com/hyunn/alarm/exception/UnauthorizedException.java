package com.hyunn.alarm.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String msg) {
    super(msg);
  }

  public ErrorStatus toErrorCode() {
    return ErrorStatus.UNAUTHORIZED_EXCEPTION;
  }

}
