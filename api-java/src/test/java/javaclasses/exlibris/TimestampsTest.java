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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.exlibris.Timestamps.toLocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TimestampsTest should")
class TimestampsTest {

    @Test
    @DisplayName("have the private constructor")
    void havePrivateConstructor() {
        assertHasPrivateParameterlessCtor(Timestamps.class);
    }


    @Test
    @DisplayName("create LocalDate on the first January 1970 with the current TimeZone")
    void createLocalDateWithCurrentTimeZone() {
        final Timestamp timestamp = Timestamp.getDefaultInstance();
        final LocalDate localDate = toLocalDate(timestamp);
        assertEquals(localDate.getDay(), 1);
        assertEquals(localDate.getMonth()
                              .getNumber(), 1);
        assertEquals(localDate.getYear(), 1970);
    }

    @Test
    @DisplayName("create LocalDate from the current timestamp with the current TimeZone")
    void createLocalDateFromCurrentTimestampWithCurrentTimeZone() {
        final Instant time = Instant.now();
        final Timestamp timestamp = Timestamp.newBuilder()
                                             .setSeconds(time.getEpochSecond())
                                             .setNanos(time.getNano())
                                             .build();
        final LocalDate localDate = toLocalDate(timestamp);
        final LocalDateTime now = LocalDateTime.now();
        assertEquals(localDate.getDay(), now.getDayOfMonth());
        assertEquals(localDate.getMonth()
                              .getNumber(), now.getMonthValue());
        assertEquals(localDate.getYear(), now.getYear());
    }

    @Test
    @DisplayName("create LocalDate on the 31 December 1969 with the EST TimeZone")
    void createLocalDateWithESTTimeZone() {
        final Timestamp timestamp = Timestamp.getDefaultInstance();
        final LocalDate localDate = toLocalDate(timestamp, TimeZone.getTimeZone("EST"));
        assertEquals(localDate.getDay(), 31);
        assertEquals(localDate.getMonth()
                              .getNumber(), 12);
        assertEquals(localDate.getYear(), 1969);
    }
}
