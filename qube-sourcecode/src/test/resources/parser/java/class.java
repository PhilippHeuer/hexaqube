package com.qube;

import org.jetbrains.annotations.ApiStatus;

public class HelloWorld {

    @ApiStatus.AvailableSince("1.1.0")
    public void helloWorld() {
        System.out.println("Hello World!");
    }

    /**
     * important method
     *
     * @since 0.0.1
     * @return very important string
     */
    public String aMethod() {
        return "hi mom";
    }

    @Deprecated
    @ApiStatus.Experimental
    @ApiStatus.Internal
    public void goodbye() {
        System.out.println("Goodbye!");
    }
}
