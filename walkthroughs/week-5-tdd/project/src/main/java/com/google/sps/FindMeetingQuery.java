// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (request.getAttendees().size() <= 0) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList();
    }
    // check every *duration* length block of the day
    int minutes = 0;
    int length = 0;
    int duration = (int) request.getDuration();
    int start = TimeRange.getTimeInMinutes(0, 0);
    boolean available;
    int time;
    TimeRange availableMeetingTime = null;
    ArrayList<TimeRange> availableMeetingTimes = new ArrayList<TimeRange>();
    TimeRange potentialMeetingTime;
    while (minutes < (24 * 60)) {
        available = true; 
        time = minutes + duration;
        potentialMeetingTime = TimeRange.fromStartDuration(minutes, duration);
        // if meeting overlaps with any event, check if any attendees are conflicted
        for (Event event: events) {
            if (potentialMeetingTime.overlaps(event.getWhen())) {
                for (String attendee: request.getAttendees()) {
                    if (event.getAttendees().contains(attendee)) {
                        available = false;
                        break;
                    }
                }
            }
        }
        if (available) {
            availableMeetingTimes.add(potentialMeetingTime);
            minutes = time;
        } else {
            minutes++;
        }
    }

    ArrayList<TimeRange> mergedMeetings = new ArrayList<TimeRange>();
    if (availableMeetingTimes.size() <= 0) {
        return mergedMeetings;
    } else if (availableMeetingTimes.size() == 1) {
        mergedMeetings.add(availableMeetingTimes.get(0));
        return mergedMeetings;
    }
    int size = 1;
    start = availableMeetingTimes.get(0).start();
    for (int i = 0; i < availableMeetingTimes.size() - 1; i++) {
        if (availableMeetingTimes.get(i).end() == availableMeetingTimes.get(i + 1).start()) {
            if (i == availableMeetingTimes.size() - 2) {
                mergedMeetings.add(TimeRange.fromStartEnd(start, availableMeetingTimes.get(i + 1).end(), false));
            }
        } else {
            mergedMeetings.add(TimeRange.fromStartEnd(start, availableMeetingTimes.get(i).end(), false));
            start = availableMeetingTimes.get(i + 1).start();
        }
    }
    return mergedMeetings;
  }
}
