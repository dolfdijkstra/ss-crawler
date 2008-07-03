package com.fatwire.dta.sscrawler.handlers;

public interface Visitor<T> {

    void visit(T o);
    
}
