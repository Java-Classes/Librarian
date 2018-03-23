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

const allTestBooks = [
    {
        title: "Прежде Чем Он Согрешит ",
        image: "https://books.google.com/books/content/images/frontcover/uVVMDwAAQBAJ?fife=w200-h300",
        author: "Блейк Пирс",
        status: "overdue",
        statusMessage: "31 DAYS OVERDUE"
    }, {
        title: "The Martian",
        image: "https://books.google.com/books/content/images/frontcover/gdxlAQAAQBAJ?fife=w200-h300",
        author: "Andy Weir",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Effective Java: Edition 2",
        image: "https://books.google.com/books/content/images/frontcover/ka2VUBqHiWkC?fife=w200-h300",
        author: "Joshua Bloch",
        status: "borrowed",
        statusMessage: "DUE ON 28.01.18"

    }, {
        title: "Clean Code: A Handbook of Agile Software Craftsmanship",
        image: "https://books.google.com/books/content/images/frontcover/_i6bDeoCQzsC?fife=w200-h300",
        author: "Robert C. Martin",
        status: "expected",
        statusMessage: "EXPECTED ON 28.01.18"
    }, {
        title: "Java 8 Preview Sampler",
        image: "https://books.google.com/books/content/images/frontcover/5FVOAwAAQBAJ?fife=w200-h300",
        author: "Herbert Schildt",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Java Generics and Collections: Speed Up the Java Development Process",
        image: "https://books.google.com/books/content/images/frontcover/zaoK0Z2STlkC?fife=w200-h300",
        author: "Maurice Naftalin",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "The C++ Programming Language: Edition 4",
        image: "https://books.google.com/books/content/images/frontcover/PSUNAAAAQBAJ?fife=w200-h300",
        author: "Bjarne Stroustrup",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Mastering Lambdas: Java Programming in a Multicore World",
        image: "https://books.google.com/books/content/images/frontcover/Zw5oBAAAQBAJ?fife=w200-h300",
        author: "Maurice Naftalin",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Elon Musk: How the Billionaire CEO of SpaceX and Tesla is Shaping our Future",
        image: "https://books.google.com/books/content/images/frontcover/_LFSBgAAQBAJ?fife=w200-h300",
        author: "Ashlee Vance",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Getting Started with Polymer",
        image: "https://books.google.com/books/content/images/frontcover/fAFwDQAAQBAJ?fife=w200-h300",
        author: "Arshak Khachatrian",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Don't Make Me Think, Revisited: A Common Sense Approach to Web Usability, Edition 3",
        image: "https://books.google.com/books/content/images/frontcover/QlduAgAAQBAJ?fife=w200-h300",
        author: "Steve Krug",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Node.js в действии. 2-е издание",
        image: "https://books.google.com/books/content/images/frontcover/s9VMDwAAQBAJ?fife=w200-h300",
        author: "Кантелон МайкХартер МаркГоловайчук TJРайлих Натан",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "React.js. Быстрый старт",
        image: "https://books.google.com/books/content/images/frontcover/g79TDgAAQBAJ?fife=w200-h300",
        author: "Стоян Стефанов",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Game Programming Using Qt: Beginner's Guide",
        image: "https://books.google.com/books/content/images/frontcover/6F0dDAAAQBAJ?fife=w200-h300",
        author: "Witold WysotaLorenz Haas",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Pro Spring 5: An In-Depth Guide to the Spring Framework and Its Tools, Edition 5",
        image: "https://books.google.com/books/content/images/frontcover/N-U5DwAAQBAJ?fife=w200-h300",
        author: "Iuliana CosminaRob HarropChris SchaeferClarence Ho",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "JavaScript: The Good Parts: The Good Parts",
        image: "https://books.google.com/books/content/images/frontcover/PXa2bby0oQ0C?fife=w200-h300",
        author: "Douglas Crockford",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Java in a Nutshell: A Desktop Quick Reference, Edition 6",
        image: "https://books.google.com/books/content/images/frontcover/trDXBAAAQBAJ?fife=w200-h300",
        author: "Benjamin J EvansDavid Flanagan",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "You Don't Know JS: ES6 & Beyond",
        image: "https://books.google.com/books/content/images/frontcover/iOc6CwAAQBAJ?fife=w200-h300",
        author: "Kyle Simpson",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Прежде Чем Он Согрешит ",
        image: "https://books.google.com/books/content/images/frontcover/uVVMDwAAQBAJ?fife=w200-h300",
        author: "Блейк Пирс",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "The Martian",
        image: "https://books.google.com/books/content/images/frontcover/gdxlAQAAQBAJ?fife=w200-h300",
        author: "Andy Weir",
        status: "available",
        statusMessage: "3 AVAILABLE"

    }, {
        title: "Effective Java: Edition 2",
        image: "https://books.google.com/books/content/images/frontcover/ka2VUBqHiWkC?fife=w200-h300",
        author: "Joshua Bloch",
        status: "available",
        statusMessage: "3 AVAILABLE"

    }, {
        title: "Clean Code: A Handbook of Agile Software Craftsmanship",
        image: "https://books.google.com/books/content/images/frontcover/_i6bDeoCQzsC?fife=w200-h300",
        author: "Robert C. Martin",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Java 8 Preview Sampler",
        image: "https://books.google.com/books/content/images/frontcover/5FVOAwAAQBAJ?fife=w200-h300",
        author: "Herbert Schildt",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Java Generics and Collections: Speed Up the Java Development Process",
        image: "https://books.google.com/books/content/images/frontcover/zaoK0Z2STlkC?fife=w200-h300",
        author: "Maurice Naftalin",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "The C++ Programming Language: Edition 4",
        image: "https://books.google.com/books/content/images/frontcover/PSUNAAAAQBAJ?fife=w200-h300",
        author: "Bjarne Stroustrup",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Mastering Lambdas: Java Programming in a Multicore World",
        image: "https://books.google.com/books/content/images/frontcover/Zw5oBAAAQBAJ?fife=w200-h300",
        author: "Maurice Naftalin",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Elon Musk: How the Billionaire CEO of SpaceX and Tesla is Shaping our Future",
        image: "https://books.google.com/books/content/images/frontcover/_LFSBgAAQBAJ?fife=w200-h300",
        author: "Ashlee Vance",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Getting Started with Polymer",
        image: "https://books.google.com/books/content/images/frontcover/fAFwDQAAQBAJ?fife=w200-h300",
        author: "Arshak Khachatrian",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Don't Make Me Think, Revisited: A Common Sense Approach to Web Usability, Edition 3",
        image: "https://books.google.com/books/content/images/frontcover/QlduAgAAQBAJ?fife=w200-h300",
        author: "Steve Krug",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Node.js в действии. 2-е издание",
        image: "https://books.google.com/books/content/images/frontcover/s9VMDwAAQBAJ?fife=w200-h300",
        author: "Кантелон МайкХартер МаркГоловайчук TJРайлих Натан",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "React.js. Быстрый старт",
        image: "https://books.google.com/books/content/images/frontcover/g79TDgAAQBAJ?fife=w200-h300",
        author: "Стоян Стефанов",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Game Programming Using Qt: Beginner's Guide",
        image: "https://books.google.com/books/content/images/frontcover/6F0dDAAAQBAJ?fife=w200-h300",
        author: "Witold WysotaLorenz Haas",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Pro Spring 5: An In-Depth Guide to the Spring Framework and Its Tools, Edition 5",
        image: "https://books.google.com/books/content/images/frontcover/N-U5DwAAQBAJ?fife=w200-h300",
        author: "Iuliana CosminaRob HarropChris SchaeferClarence Ho",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "JavaScript: The Good Parts: The Good Parts",
        image: "https://books.google.com/books/content/images/frontcover/PXa2bby0oQ0C?fife=w200-h300",
        author: "Douglas Crockford",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "Java in a Nutshell: A Desktop Quick Reference, Edition 6",
        image: "https://books.google.com/books/content/images/frontcover/trDXBAAAQBAJ?fife=w200-h300",
        author: "Benjamin J EvansDavid Flanagan",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }, {
        title: "You Don't Know JS: ES6 & Beyond",
        image: "https://books.google.com/books/content/images/frontcover/iOc6CwAAQBAJ?fife=w200-h300",
        author: "Kyle Simpson",
        status: "available",
        statusMessage: "3 AVAILABLE"
    }
];
