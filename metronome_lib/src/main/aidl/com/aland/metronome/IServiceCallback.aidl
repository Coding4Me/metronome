package com.aland.metronome;


interface IServiceCallback {
void onSuccess();
void onWarning(String message);
void onError(int code, String message);
}