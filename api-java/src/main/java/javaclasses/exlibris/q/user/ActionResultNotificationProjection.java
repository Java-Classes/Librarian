package javaclasses.exlibris.q.user;

import io.spine.core.EventContext;
import io.spine.core.Subscribe;
import io.spine.net.Url;
import io.spine.server.projection.Projection;
import io.spine.time.LocalDate;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.rejection.NonAvailableBook;
import javaclasses.exlibris.q.ActionResultNotification;
import javaclasses.exlibris.q.ActionResultNotificationVBuilder;
import javaclasses.exlibris.q.ActionStatus;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * A projection state of a result of an action after scanning OR code.
 *
 * @author Yurii Haidamaka
 */
public class ActionResultNotificationProjection extends Projection<UserId, ActionResultNotification, ActionResultNotificationVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public ActionResultNotificationProjection(UserId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event, EventContext context) {
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final UserId userId = event.getWhoBorrowed();
        final BookTitle title = bookDetails.getTitle();
        final AuthorName author = bookDetails.getAuthor();
        final Url bookCoverUrl = bookDetails.getBookCoverUrl();
        final ActionStatus status = ActionStatus.BORROW;
        final InventoryId inventoryId = event.getInventoryId();
        final LocalDate dueDate = toLocalDate(event.getWhenDue());

        getBuilder().setUserId(userId)
                    .setTitle(title)
                    .setAuthors(author)
                    .setCoverUrl(bookCoverUrl)
                    .setStatus(status)
                    .setInventoryId(inventoryId)
                    .setDueDate(dueDate);

    }

    @Subscribe
    public void on(BookReturned event, EventContext context) {
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();

        final UserId userId = event.getWhoReturned();
        final BookTitle title = bookDetails.getTitle();
        final AuthorName author = bookDetails.getAuthor();
        final Url bookCoverUrl = bookDetails.getBookCoverUrl();
        final ActionStatus status = ActionStatus.RETURN;
        final InventoryId inventoryId = event.getInventoryId();

        getBuilder().setUserId(userId)
                    .setTitle(title)
                    .setAuthors(author)
                    .setCoverUrl(bookCoverUrl)
                    .setStatus(status)
                    .setInventoryId(inventoryId);
    }

    @Subscribe
    public void on(NonAvailableBook rejection) {

    }
}
