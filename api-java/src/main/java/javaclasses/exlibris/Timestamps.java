/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javaclasses.exlibris;

import com.google.protobuf.Timestamp;
import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;

import java.util.Calendar;

import static io.spine.time.Timestamps2.toDate;

/**
 * Utilities for working with {@link Timestamp}.
 *
 * <p> This class provides a method that creates a {@link LocalDate} object
 * from the {@link Timestamp} object using the current timezone.
 *
 * @author Yurii Haidamaka
 */
public class Timestamps {

    private Timestamps() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Obtains a {@link LocalDate} from the {@link Timestamp} object
     * using the current timezone.
     */
    public static LocalDate toLocalDate(Timestamp timestamp) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(timestamp));
        final LocalDate localDate = toLocalDate(calendar);
        return localDate;
    }

    /**
     * Obtains a {@link LocalDate} from the {@link Timestamp} object
     * and number of days after this time, using the current timezone.
     *
     * @param timestamp a point in time
     * @param period    number of days after timestamp
     */
    public static LocalDate toLocalDateAfterPeriod(Timestamp timestamp, int period) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(timestamp));
        calendar.add(Calendar.DATE, period);
        final LocalDate localDate = toLocalDate(calendar);
        return localDate;
    }

    private static LocalDate toLocalDate(Calendar calendar) {
        final LocalDate date = LocalDate.newBuilder()
                                        .setDay(calendar.get(Calendar.DAY_OF_MONTH))
                                        .setMonth(
                                                MonthOfYear.valueOf(
                                                        calendar.get(Calendar.MONTH) + 1))
                                        .setYear(calendar.get(Calendar.YEAR))
                                        .build();
        return date;
    }

}
