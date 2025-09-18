package com.cmp.core.model;
public class Errors {
    public static class CmpException extends RuntimeException {
        public CmpException(String m){ super(m); }
        public CmpException(String m, Throwable t){ super(m,t); }
    }
    public static class ValidationException extends CmpException { public ValidationException(String m){ super(m); } }
    public static class NotFoundException extends CmpException { public NotFoundException(String m){ super(m); } }
    public static class UnauthorizedException extends CmpException { public UnauthorizedException(String m){ super(m); } }
}
