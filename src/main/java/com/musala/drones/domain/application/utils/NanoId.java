package com.musala.drones.domain.application.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.Random;

/**
 * Utility class for generating globally unique serial numbers.
 * In a real system, of course, the numbers are not generated, but probably derived from the drone itself and validated through the manufacturer's API, but I need a way to generate numbers :)
 * <p>
 * Nano ID is a library for generating random IDs. Likewise UUID, there is a probability of duplicate IDs. However, this probability is extremely small.
 * <p>
 * More than 1 quadrillion years needed, in order to have a 1% probability of at least one collision, if we will generate 1000000000 id per second with using this alphabet with id size=36
 * <a href="https://zelark.github.io/nano-id-cc/">click here to know more</a>
 */
public class NanoId {
    private static final Random random = new Random();
    private static final char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_".toCharArray();
    private final static Integer size = 100;

    public static String next() {
        return NanoIdUtils.randomNanoId(random, alphabet, size);
    }
}
