package com.a;

import java.util.*;

class Model {

    List<Event> queue = new ArrayList<>();
    List<Event> listOfProcessedEvents = new ArrayList<>();
    List<Event> listOfComingEvents = new ArrayList<>();
    List<EventRecord> records = new ArrayList<>();

    int currentTime = 0;
    int timeOfBusyService = 0;

    Event currentEvent;

    public void startModelling() {
        generateExponentialDistributedArrivalTime(4);
        generateUniformDistributedServiceTime(1, 5);
//        generateExampleEvents();

        for (Event event : listOfComingEvents) {
            System.out.println(event.timeOfArrival);
        }
        new Scanner(System.in).nextInt();
        //Start of modelling
        System.out.println("Modelling time ");
        int time = new Scanner(System.in).nextInt();
        while (currentTime<= time) {


            System.out.println("New step, current time - " + currentTime);

            //Processing nearest event
            for (int i = 0; i < listOfComingEvents.size(); i++) {
                if (listOfComingEvents.get(i).timeOfArrival == currentTime) {
                    System.out.println("Event "
                            + listOfComingEvents.get(i).timeOfArrival + listOfComingEvents.get(i).timeOfProcessing
                            + " added to queue");
                    queue.add(listOfComingEvents.get(i));
                    if (currentEvent != null) {
                        addRecord(new EventRecord(queue.size(), currentTime, "arrival   ",
                                String.valueOf(currentTime),
                                String.valueOf(currentEvent.timeOfProcessing + 1)));
                    }
                }
            }
            if (!queue.isEmpty() & serverIsFree()) {
                if (currentTime >= queue.get(0).timeOfArrival) {
                    startEventProcessing(queue.get(0));
                    if (currentEvent != null) {
                        addRecord(new EventRecord(queue.size(), currentTime, "arrival   ",
                                String.valueOf(currentTime),
                                String.valueOf(currentEvent.timeOfProcessing + 1)));
                    }
                }
            } else if (!serverIsFree()) {
                processEvent();
                if (!queue.isEmpty() & serverIsFree() && currentTime >= queue.get(0).timeOfArrival) {
                    startEventProcessing(queue.get(0));
                }

            } else if (queue.isEmpty() & serverIsFree()) {
                System.out.println("Queue empty, server free");
            }
            System.out.println("Queue size " + queue.size());
            currentTime++;
            System.out.println();
            //End of cycle
        }
        printStats();
    }

    //Adding records to table
    void addRecord(EventRecord record) {
        records.add(record);
    }

    void startEventProcessing(Event event) {
        System.out.println("Start processing event");
        currentEvent = event;
        queue.remove(0);
        event.timeOfStartProcessing = currentTime;
        processEvent();
    }

    void processEvent() {
        if (currentEvent.timeOfProcessing == 0) {
            finishEventProcessing();
        } else {
            System.out.println("Processing event");
            currentEvent.timeOfProcessing = --currentEvent.timeOfProcessing;
            ++timeOfBusyService;
        }
    }

    void finishEventProcessing() {
        currentEvent.timeOfCompletion = currentTime;
        listOfProcessedEvents.add(currentEvent);
        if (currentEvent != null) {

            if (records.get(records.size() - 1).tm == currentEvent.timeOfCompletion) {
                records.get(records.size() - 1).queueLength = records.get(records.size() - 1).queueLength - 1;
                addRecord(new EventRecord(queue.size() - 1, currentTime, "completion",
                        "-",
                        String.valueOf(currentEvent.timeOfProcessing)));
            } else {
                addRecord(new EventRecord(queue.size(), currentTime, "completion",
                        "-",
                        String.valueOf(currentEvent.timeOfProcessing)));
            }


        }
        currentEvent = null;
        System.out.println("Finish processing event");
    }

    void printStats() {
        System.out.println("STATS");
        //UTILIZATION
        double util = (double) timeOfBusyService / (double) currentTime;
        System.out.println("Utilization (p) - " + util);

        //QUEUE LENGTH L
        double l = 0;
        for (int i = 1; i < records.size(); i++) {
            l = l + (((double) records.get(i).tm - (double) records.get(i - 1).tm) * (double) records.get(i).queueLength / (double) currentTime);
        }
        System.out.println("Mean queue length (l) - " + l);

        //NUMBER OF REQUESTS M
        System.out.println("Mean number of requests in system (m) - " + (l + util));

        //WAITING TIME W
        int sumOfResponseTime = 0;
        for (Event event : listOfProcessedEvents) {
            sumOfResponseTime = sumOfResponseTime + (event.timeOfStartProcessing - event.timeOfArrival);
        }
        System.out.println("Mean waiting time (ω) - " + (double) sumOfResponseTime / (double) listOfProcessedEvents.size());

        //TIME OF STAY U
        int sumOfSystemTime = 0;
        for (Event event : listOfProcessedEvents) {
            sumOfSystemTime = sumOfSystemTime + (event.timeOfCompletion - event.timeOfArrival);
        }
        System.out.println("Mean time of stay in system (u) - " + (double) sumOfSystemTime / (double) listOfProcessedEvents.size());

        //TABLE
        System.out.println();
        System.out.println("Table");
        for (EventRecord record : records) {
            System.out.println(record.tm + " " + record.type + " " + record.arrival + " " + record.completion + " " + record.queueLength);
        }
    }

    void generateExponentialDistributedArrivalTime(int lambda) {
        for (int i = 0; i < 10000; i++) {
            Event event = new Event();
            if (i > 0) {
                Event previousEvent = listOfComingEvents.get(i - 1);
                event.timeOfArrival = previousEvent.timeOfArrival
                        + previousEvent.timeOfProcessing
                        + (int) ((Math.log(1 - new Random().nextDouble())) / (-lambda) * 25);
                System.out.println("random time " + event.timeOfArrival);
            } else {
                event.timeOfArrival = (int) ((Math.log(1 - new Random().nextDouble())) / (-lambda) * 25);
                System.out.println("random first " + event.timeOfArrival);
            }

            listOfComingEvents.add(event);
        }
        for (Event event : listOfComingEvents) {
            System.out.println(event.timeOfArrival);
        }
    }

    void generateUniformDistributedServiceTime(int a, int b) {
        Random r = new Random();
        for (Event event : listOfComingEvents) {
            event.timeOfProcessing = r.nextInt(b) + a;
        }
    }

    void generateExampleEvents() {
        Event event = new Event();
        event.timeOfArrival = 3;
        event.timeOfProcessing = 3;
        listOfComingEvents.add(event);
        event = new Event();
        event.timeOfArrival = 4;
        event.timeOfProcessing = 3;
        listOfComingEvents.add(event);

        event = new Event();
        event.timeOfArrival = 6;
        event.timeOfProcessing = 1;
        listOfComingEvents.add(event);

        event = new Event();
        event.timeOfArrival = 7;
        event.timeOfProcessing = 1;
        listOfComingEvents.add(event);

        event = new Event();
        event.timeOfArrival = 10;
        event.timeOfProcessing = 1;
        listOfComingEvents.add(event);

        event = new Event();
        event.timeOfArrival = 10;
        event.timeOfProcessing = 2;
        listOfComingEvents.add(event);

        event = new Event();
        event.timeOfArrival = 10;
        event.timeOfProcessing = 1;
        listOfComingEvents.add(event);
    }

    private boolean serverIsFree() {
        return currentEvent == null;
    }
}