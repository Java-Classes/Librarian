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

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import javaclasses.exlibris.c.aggregate.rejection.BookAggregateRejections;
import javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;

public class RejectionConstructorsTest {

    @Test
    @DisplayName("AddBookRejection has the private constructor")
    void bookAggregateRejectionsHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.AddBookRejection.class);
    }

    @Test
    @DisplayName("RemoveBookRejection has the private constructor")
    void RemoveBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.RemoveBookRejection.class);
    }

    @Test
    @DisplayName("UpdateBookRejection has the private constructor")
    void UpdateBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.UpdateBookRejection.class);
    }

    @Test
    @DisplayName("ReturnBookRejection has the private constructor")
    void ReturnBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.ReturnBookRejection.class);
    }

    @Test
    @DisplayName("CancelReservationRejection has the private constructor")
    void CancelReservationRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(
                InventoryAggregateRejections.CancelReservationRejection.class);
    }

    @Test
    @DisplayName("ReserveBookRejection has the private constructor")
    void ReserveBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.ReserveBookRejection.class);
    }

    @Test

    @DisplayName("WriteBookOffRejection has the private constructor")
    void WriteBookOffRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.WriteBookOffRejection.class);
    }

}
