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

package javaclasses.exlibris.c.inventory;

import javaclasses.exlibris.c.book.BookAggregateRejections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;

public class RejectionConstructorsTest {

    @Test
    @DisplayName("AddBookRejection has a private constructor")
    void bookAggregateRejectionsHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.AddBookRejection.class);
    }

    @Test
    @DisplayName("RemoveBookRejection has a private constructor")
    void RemoveBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.RemoveBookRejection.class);
    }

    @Test
    @DisplayName("UpdateBookRejection has a private constructor")
    void UpdateBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.UpdateBookRejection.class);
    }

    @Test
    @DisplayName("ReturnBookRejection has a private constructor")
    void ReturnBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.ReturnBookRejection.class);
    }

    @Test
    @DisplayName("CancelReservationRejection has a private constructor")
    void CancelReservationRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(
                InventoryAggregateRejections.CancelReservationRejection.class);
    }

    @Test
    @DisplayName("ReserveBookRejection has a private constructor")
    void ReserveBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.ReserveBookRejection.class);
    }

    @Test
    @DisplayName("WriteBookOffRejection has a private constructor")
    void WriteBookOffRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.WriteBookOffRejection.class);
    }

    @Test
    @DisplayName("ExtendLoanPeriodRejection has a private constructor")
    void ExtendLoanPeriodRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(
                InventoryAggregateRejections.ExtendLoanPeriodRejection.class);
    }

    @Test
    @DisplayName("InventoryAggregateRejections has a private constructor")
    void InventoryAggregateRejectionsHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.class);
    }

    @Test
    @DisplayName("BookAggregateRejections has a private constructor")
    void  BookAggregateRejectionsHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(BookAggregateRejections.class);
    }

    @Test
    @DisplayName("BorrowBookRejection has a private constructor")
    void BorrowBookRejectionHasPrivateConstructor() {
        assertHasPrivateParameterlessCtor(InventoryAggregateRejections.BorrowBookRejection.class);
    }
}
