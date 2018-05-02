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

import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.UpdateBook;

import static io.spine.time.Time.getCurrentTime;
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.CUSTOM_REASON;
import static javaclasses.exlibris.c.RemoveBook.BookRemovalReasonCase.OUTDATED;
import static javaclasses.exlibris.testdata.TestValues.BOOK_DETAILS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;

/**
 * A factory of the task commands for the test needs.
 *
 * @author Paul Ageyev
 */
public class BookCommandFactory {

    public static final RemoveBook.BookRemovalReasonCase removalOutdatedReason = OUTDATED;
    public static final RemoveBook.BookRemovalReasonCase removalCustomReason = CUSTOM_REASON;

    private BookCommandFactory() {
    }

    public static AddBook createBookInstance() {
        return createBookInstance(BOOK_ID, USER_ID, BOOK_DETAILS);
    }

    public static AddBook createBookInstance(BookId bookId, UserId userId,
                                             BookDetails bookDetails) {

        final AddBook result = AddBook.newBuilder()
                                      .setBookId(bookId)
                                      .setLibrarianId(userId)
                                      .setBookDetails(bookDetails)
                                      .build();
        return result;
    }

    public static UpdateBook updateBookInstance(BookId bookId, UserId userId,
                                                BookDetailsChange bookDetails) {

        final UpdateBook result = UpdateBook.newBuilder()
                                            .setBookId(bookId)
                                            .setLibrarianId(userId)
                                            .setBookDetails(bookDetails)
                                            .build();
        return result;

    }

    @SuppressWarnings("all") /*Cause of codacy needs a default switch statement
                             which will never be called*/
    public static RemoveBook removeBookInstance(BookId bookId, UserId librarianId,
                                                RemoveBook.BookRemovalReasonCase removalReasonCase) {

        final RemoveBook.Builder result = RemoveBook.newBuilder()
                                                    .setBookId(bookId)
                                                    .setLibrarianId(librarianId)
                                                    .setWhenRemoved(getCurrentTime());

        switch (removalReasonCase) {
            case OUTDATED: {
                result.setOutdated(true);
                break;
            }
            case CUSTOM_REASON: {
                result.setCustomReason(TestValues.CUSTOM_REASON);
                break;

            }
            case BOOKREMOVALREASON_NOT_SET: {
                throw newIllegalArgumentException("The book cannot be removed without reason.",
                                                  removalReasonCase);
            }
        }
        return result.build();
    }
}
