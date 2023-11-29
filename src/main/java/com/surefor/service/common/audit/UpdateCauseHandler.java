package com.surefor.service.common.audit;

public class UpdateCauseHandler {
    private static final ThreadLocal<UpdateCause> UPDATE_CAUSE = new ThreadLocal<>();

    public static UpdateCause get() {
        return UPDATE_CAUSE.get();
    }

    public static void set(UpdateCause updateCause) {
        UPDATE_CAUSE.set(updateCause);
    }

    // Important that this is called or else it can lead to memory leaks
    public static void end() {
        UPDATE_CAUSE.remove();
    }
}
