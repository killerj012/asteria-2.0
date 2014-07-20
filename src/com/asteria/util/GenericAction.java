package com.asteria.util;

/**
 * A dynamic class that can run an action of any context.
 * 
 * @author lare96
 * @param <T>
 *            the context class of the action.
 */
public interface GenericAction<T> {

    /**
     * The action that will be carried out.
     * 
     * @param context
     *            the context the action will be carried out in.
     */
    public void run(T context);
}
