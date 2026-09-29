package com.quanlykhachsan.services;

public record Result(boolean success, String message) {
    public static Result ok(String msg) { return new Result(true, msg); }
    public static Result fail(String msg) { return new Result(false, msg); }
}
