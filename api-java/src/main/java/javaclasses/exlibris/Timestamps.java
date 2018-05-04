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
import java.util.TimeZone;

import static io.spine.time.Timestamps2.toDate;

/**
 * Utility class for working with {@link Timestamp}.
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
     * and {@link TimeZone}.
     */
    public static LocalDate toLocalDate(Timestamp timestamp, TimeZone timeZone) {
        final Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(toDate(timestamp));
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
