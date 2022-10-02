package com.musala.drones.domain.application.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.Random;

/**
 * Utility class for generating globally unique serial numbers.
 * See solution notes
 */
public class NanoId {
    private static final Random random = new Random();
    private static final char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_".toCharArray();
    private final static Integer size = 100;

    public static String next() {
        return NanoIdUtils.randomNanoId(random, alphabet, size);
    }
}
