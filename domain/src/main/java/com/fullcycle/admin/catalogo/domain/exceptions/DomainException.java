package com.fullcycle.admin.catalogo.domain.exceptions;

import com.fullcycle.admin.catalogo.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStacktraceException {

  protected final List<Error> errors;

  protected DomainException(final String aMessage, final List<Error> theErrors) {
    super(aMessage);
    this.errors = theErrors;
  }

  public static DomainException with(final Error anError) {
    return new DomainException(anError.message(), List.of(anError));
  }

  public static DomainException with(final List<Error> theErrors) {
    return new DomainException("", theErrors);
  }

  public List<Error> getErrors() {
    return errors;
  }
}
