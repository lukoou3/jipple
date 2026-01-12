package com.jipple.sql.catalyst.analysis;

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
}

