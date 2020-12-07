package com.a;

class EventRecord {

    int queueLength;
    int tm;
    String type = "complition";
    String arrival;
    String completion;

    public EventRecord(int queueLength, int tm, String type, String arrival, String completion) {
        this.queueLength = queueLength;
        this.tm = tm;
        this.type = type;
        this.arrival = arrival;
        this.completion = completion;
    }

    public EventRecord() {
    }
}