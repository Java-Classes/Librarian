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
{
    window.ExlibrisAppTest = {} || ExlibrisAppTest;
    const threeWeeks = 21;
    const millisecondsInOneDay = 86400000;

    ExlibrisAppTest.BOOKS = [{
        id: "123456789",
        title: "Стив Джобс",
        image: "https://books.google.com/books/content/images/frontcover/xSWOMjMSzi8C?fife=w200-h300",
        author: "Уолтер Айзексон",
        category: "Biography",
        description: "В основу этой биографии легли беседы с самим Стивом Джобсом, а также с его родственниками, друзьями, врагами, соперниками и коллегами. Джобс никак не контролировал автора. Он откровенно отвечал на все вопросы и ждал такой же честности от остальных. Это рассказ о жизни, полной падений и взлетов, о сильном человеке и талантливом бизнесмене, который одним из первых понял: чтобы добиться успеха в XXI веке, нужно соединить креативность и технологии.",
        status: "AVAILABLE",
        statusMessage: "3 AVAILABLE",
        availableCount: 3
    }, {
        id: "123456790",
        title: "Эйнштейн. Его жизнь и его Вселенная",
        image: "https://books.google.com/books/content/images/frontcover/28IOCQAAQBAJ?fife=w200-h300",
        author: "Уолтер Айзексон",
        category: "Biography",
        description: "Уолтер Айзексон, автор знаменитой биографии Стивена Джобса, написал книгу об одном из самых известных ученых XX века, Альберте Эйнштейне. Он не только подробно и доступно изложил суть научных концепций и открытий автора теории относительности, но и увлекательно рассказал об Эйнштейне-человеке. В книге приводится множество документальных материалов – письма, воспоминания, дневниковые записи. Перед нами встает образ удивительно талантливого человека, мечтателя и бунтаря, гуманиста и мыслителя.",
        status: "RESERVED",
        date: new Date(new Date().getTime() + threeWeeks * millisecondsInOneDay)
    }];
}
