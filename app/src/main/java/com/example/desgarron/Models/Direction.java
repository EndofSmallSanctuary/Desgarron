package com.example.desgarron.Models;

public class Direction {
    public static int IN = 2;
    public static int N = 0;
    public static int OUT = 1;

    public static boolean check(int i) {
        return i == N || i == IN || i == OUT;
    }

    private Direction() {
    }
}
