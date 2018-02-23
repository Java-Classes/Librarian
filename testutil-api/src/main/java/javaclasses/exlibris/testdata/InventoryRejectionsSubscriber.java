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

package javaclasses.exlibris.testdata;

import io.spine.core.Subscribe;
import io.spine.server.rejection.RejectionSubscriber;
import javaclasses.exlibris.c.rejection.Rejections;

/**
 * The subscriber which holds the rejection.
 *
 * @author Alexander Karpets
 */
public class InventoryRejectionsSubscriber extends RejectionSubscriber {

    private static boolean wasCalled = false;

    public static boolean wasCalled() {
        return wasCalled;
    }

    @Subscribe
    public void on(Rejections.BookAlreadyBorrowed rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.BookAlreadyReserved rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.NonAvailableBook rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.CannotReturnNonBorrowedBook rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.CannotReturnMissingBook rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.CannotCancelMissingReservation rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.CannotWriteMissingBookOff rejection) {
        wasCalled = true;
    }

    @Subscribe
    public void on(Rejections.CannotExtendLoanPeriod rejection) {
        wasCalled = true;
    }

    public static void clear() {
        wasCalled = false;
    }
}
