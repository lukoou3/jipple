package com.jipple.sql.catalyst.analysis;

import java.util.Map;

public interface TypeCheckResult {
    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    TypeCheckSuccess typeCheckSuccess = new TypeCheckSuccess();

    static TypeCheckSuccess typeCheckSuccess(){
        return typeCheckSuccess;
    }

    static TypeCheckFailure typeCheckFailure(String message){
        return new TypeCheckFailure(message);
    }

    static DataTypeMismatch dataTypeMismatch(String errorClass, Map<String, String> messageParameters){
        return new DataTypeMismatch(errorClass, messageParameters);
    }

    static DataTypeMismatch dataTypeMismatch(String errorClass){
        return new DataTypeMismatch(errorClass);
    }

   class TypeCheckSuccess implements TypeCheckResult{
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    class TypeCheckFailure implements TypeCheckResult{
        public final String message;

        TypeCheckFailure(String message) {
            this.message = message;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }

    class DataTypeMismatch implements TypeCheckResult {
        public final String errorClass;
        public final Map<String, String> messageParameters;

        public DataTypeMismatch(String errorClass, Map<String, String> messageParameters) {
            this.errorClass = errorClass;
            this.messageParameters = messageParameters;
        }

        public DataTypeMismatch(String errorClass) {
            this(errorClass, Map.of());
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}

