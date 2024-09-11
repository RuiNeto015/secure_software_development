package com.desofs.backend.utils;

import com.desofs.backend.domain.valueobjects.IntervalTime;

import java.util.List;

public class IntervalTimeUtils {

    public static boolean intervalsIntercept(IntervalTime a, IntervalTime b) {
        return !((a.getFrom().before(b.getFrom()) && a.getTo().before(b.getFrom())) || // precedes
                (a.getTo().equals(b.getFrom())) || // meets
                (b.getTo().equals(a.getFrom())) || // met by
                (b.getFrom().before(a.getFrom()) && b.getTo().before(a.getFrom()))); // preceded by
    }

    public static boolean listHasOverlap(List<IntervalTime> intervals) {
        for (int i = 0; i < intervals.size(); i++) {
            IntervalTime interval1 = intervals.get(i);
            for (int j = i + 1; j < intervals.size(); j++) {
                IntervalTime interval2 = intervals.get(j);
                if (intervalsIntercept(interval1, interval2)) {
                    return true;
                }
            }
        }
        return false;
    }

}
