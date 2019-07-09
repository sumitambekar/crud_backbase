package com.crud.base;

public class PageConstructor {

    /**
     * @return new instance of class <P> using default constructor
     */
    public static <P> P getInstance(Class<P> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to create new page instance of class: " + clazz);
        }
    }
}
