package com.jipple.error;

import com.jipple.QueryContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface mixed into Throwables thrown from Jipple.
 *
 * - For backwards compatibility, existing Throwable types can be thrown with an arbitrary error
 *   message with a null error class. See [[JippleException]].
 * - To promote standardization, Throwables should be thrown with an error class and message
 *   parameters to construct an error message with JippleThrowableHelper.getMessage(). New Throwable
 *   types should not accept arbitrary error messages. See [[JippleArithmeticException]].
 *
 * @since 3.2.0
 */
public interface JippleThrowable {
  // Succinct, human-readable, unique, and consistent representation of the error category
  // If null, error class is not set
  String getErrorClass();

  // Portable error identifier across SQL engines
  // If null, error class or SQLSTATE is not set
  default String getSqlState() {
    return JippleThrowableHelper.getSqlState(this.getErrorClass());
  }

  // True if this error is an internal error.
  default boolean isInternalError() {
    return JippleThrowableHelper.isInternalError(this.getErrorClass());
  }

  default Map<String, String> getMessageParameters() {
    return new HashMap<>();
  }

  default QueryContext[] getQueryContext() { return new QueryContext[0]; }
}
