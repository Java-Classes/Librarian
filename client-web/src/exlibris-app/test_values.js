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

const TWO_WEEKS = 14;
const THREE_WEEKS = 21;
const THREE_DAYS = 3;
const TWO_DAYS = 2;
const MILLISECONDS_IN_ONE_DAY = 86400000;

/* exported libraryTestBooks */

const libraryTestBooks = [
    {
        id: "8535902775",
        title: "Прежде Чем Он Согрешит ",
        image: "https://books.google.com/books/content/images/frontcover/uVVMDwAAQBAJ?fife=w200-h300",
        author: "Блейк Пирс",
        isbn: "9781640293663",
        category: "Fiction ",
        description: "Блейк Пирс, автор бестселлера «КОГДА ОНА УШЛА» (бестселлера #1 с более чем 900 отзывов с высшей оценкой), представляет книгу #7 в увлекательной серии детективных романов о Макензи Уайт.\n" +
        "В «ПРЕЖДЕ ЧЕМ ОН СОГРЕШИТ» (Загадки Макензи Уайт—Книга 7) по всему Вашингтону полиция находит тела священников, распятые перед входом в храмы. Что это, акт возмездия? Искать ли преступника среди служителей церкви? Или это серийный убийца, открывший охоту на священников и движимый более зловещими мотивами?\n" +
        "\n" +
        "ФБР передаёт это дело специальному агенту Макензи Уайт, потому что оно перекликается с религиозными убийствами, которые она расследовала, когда охотилась на преступника по прозвищу «Страшила» в своём первом деле. Погрузившись в мир, в котором живут священники, Макензи пытается лучше понять смысл ритуалов, древних религиозных текстов и даже разум самого убийцы. При этом она также занята поиском убийцы отца, твёрдо решив, что на этот раз не даст ему уйти. Убийца священников намного более жесток, чем другие преступники, и в смертельной игре в кошки-мышки он подводит Макензи к черте, за которой начинается безумие.\n" +
        "\n" +
        "Мрачный психологический триллер с увлекательным сюжетом «ПРЕЖДЕ ЧЕМ ОН СОГРЕШИТ» – это книга #7 в захватывающей серии детективных романов, рассказывающих о любимой героине. От книги просто невозможно оторваться.\n" +
        "\n" +
        "Также не пропустите роман Блейка Пирса «КОГДА ОНА УШЛА» (Загадки Райли Пейдж—Книга #1), бестселлер #1 с более чем 900 отзывов с высшей оценкой. Роман доступен для бесплатного скачивания!",
        availableCount: 3,
        status: "AVAILABLE"
    }, {
        id: "8535902771",
        title: "The Martian",
        image: "https://books.google.com/books/content/images/frontcover/gdxlAQAAQBAJ?fife=w200-h300",
        author: "Andy Weir",
        isbn: "B017PNMYSK",
        category: "Fiction",
        description: "The Sunday Times Bestseller behind the major new film from Ridley Scott starring Matt Damon and Jessica Chastain.\n" +
        "I’m stranded on Mars.\n" +
        "\n" +
        "I have no way to communicate with Earth.\n" +
        "\n" +
        "I’m in a Habitat designed to last 31 days.\n" +
        "\n" +
        "If the Oxygenator breaks down, I’ll suffocate. If the Water Reclaimer breaks down, I’ll die of thirst. If the Hab breaches, I’ll just kind of explode. If none of those things happen, I’ll eventually run out of food and starve to death.\n" +
        "\n" +
        "So yeah. I’m screwed.\n" +
        "\n" +
        "Andy Weir's second novel Artemis is now available",
        availableCount: 0,
        status: "EXPECTED"
    }, {

        id: "0132350882",
        title: "Clean Code: A Handbook of Agile Software Craftsmanship",
        image: "https://books.google.com/books/content/images/frontcover/_i6bDeoCQzsC?fife=w200-h300",
        author: "Robert C. Martin",
        isbn: "0132350882",
        category: "Programming",
        description: "Even bad code can function. But if code isn’t clean, it can bring a development organization to its knees. Every year, countless hours and significant resources are lost because of poorly written code. But it doesn’t have to be that way.\n" +
        "\n" +
        "Noted software expert Robert C. Martin presents a revolutionary paradigm with Clean Code: A Handbook of Agile Software Craftsmanship . Martin has teamed up with his colleagues from Object Mentor to distill their best agile practice of cleaning code “on the fly” into a book that will instill within you the values of a software craftsman and make you a better programmer–but only if you work at it.\n" +
        "\n" +
        "What kind of work will you be doing? You’ll be reading code–lots of code. And you will be challenged to think about what’s right about that code, and what’s wrong with it. More importantly, you will be challenged to reassess your professional values and your commitment to your craft.\n" +
        "\n" +
        "Clean Code is divided into three parts. The first describes the principles, patterns, and practices of writing clean code. The second part consists of several case studies of increasing complexity. Each case study is an exercise in cleaning up code–of transforming a code base that has some problems into one that is sound and efficient. The third part is the payoff: a single chapter containing a list of heuristics and “smells” gathered while creating the case studies. The result is a knowledge base that describes the way we think when we write, read, and clean code.\n" +
        "\n" +
        "Readers will come away from this book understanding\n" +
        "How to tell the difference between good and bad code\n" +
        "How to write good code and how to transform bad code into good code\n" +
        "How to create good names, good functions, good objects, and good classes\n" +
        "How to format code for maximum readability\n" +
        "How to implement complete error handling without obscuring code logic\n" +
        "How to unit test and practice test-driven development\n" +
        "This book is a must for any developer, software engineer, project manager, team lead, or systems analyst with an interest in producing better code.",
        availableCount: 4,
        status: "AVAILABLE"
    }, {
        id: "0596527756",
        title: "Java Generics and Collections: Speed Up the Java Development Process",
        image: "https://books.google.com/books/content/images/frontcover/zaoK0Z2STlkC?fife=w200-h300",
        author: "Maurice Naftalin",
        isbn: "0596527756",
        category: "Java programming",
        description: "This comprehensive guide shows you how to master the most importantchanges to Java since it was first released. Generics and the greatlyexpanded collection libraries have tremendously increased the power ofJava 5 and Java 6. But they have also confused many developers whohaven't known how to take advantage of these new features.\n" +
        "Java Generics and Collections covers everything from the mostbasic uses of generics to the strangest corner cases. It teaches youeverything you need to know about the collections libraries, so you'llalways know which collection is appropriate for any given task, andhow to use it.\n" +
        "\n" +
        "Topics covered include:\n" +
        "\n" +
        "Fundamentals of generics: type parameters and generic methods\n" +
        "Other new features: boxing and unboxing, foreach loops, varargs\n" +
        "Subtyping and wildcards\n" +
        "Evolution not revolution: generic libraries with legacy clients andgeneric clients with legacy libraries\n" +
        "Generics and reflection\n" +
        "Design patterns for generics\n" +
        "Sets, Queues, Lists, Maps, and their implementations\n" +
        "Concurrent programming and thread safety with collections\n" +
        "Performance implications of different collections\n" +
        "Generics and the new collection libraries they inspired take Java to anew level. If you want to take your software development practice toa new level, this book is essential reading.",
        availableCount: 0,
        status: "EXPECTED"
    }, {

        id: "0321563840",
        title: "The C++ Programming Language: Edition 4",
        image: "https://books.google.com/books/content/images/frontcover/PSUNAAAAQBAJ?fife=w200-h300",
        author: "Bjarne Stroustrup",
        isbn: "0321563840",
        category: "C++",
        description: "The new C++11 standard allows programmers to express ideas more clearly, simply, and directly, and to write faster, more efficient code. Bjarne Stroustrup, the designer and original implementer of C++, has reorganized, extended, and completely rewritten his definitive reference and tutorial for programmers who want to use C++ most effectively.\n" +
        "The C++ Programming Language, Fourth Edition, delivers meticulous, richly explained, and integrated coverage of the entire language—its facilities, abstraction mechanisms, standard libraries, and key design techniques. Throughout, Stroustrup presents concise, “pure C++11” examples, which have been carefully crafted to clarify both usage and program design. To promote deeper understanding, the author provides extensive cross-references, both within the book and to the ISO standard.\n" +
        "\n" +
        "New C++11 coverage includes\n" +
        "\n" +
        "Support for concurrency\n" +
        "Regular expressions, resource management pointers, random numbers, and improved containers\n" +
        "General and uniform initialization, simplified for-statements, move semantics, and Unicode support\n" +
        "Lambdas, general constant expressions, control over class defaults, variadic templates, template aliases, and user-defined literals\n" +
        "Compatibility issues\n" +
        "Topics addressed in this comprehensive book include\n" +
        "\n" +
        "Basic facilities: type, object, scope, storage, computation fundamentals, and more\n" +
        "Modularity, as supported by namespaces, source files, and exception handling\n" +
        "C++ abstraction, including classes, class hierarchies, and templates in support of a synthesis of traditional programming, object-oriented programming, and generic programming\n" +
        "Standard Library: containers, algorithms, iterators, utilities, strings, stream I/O, locales, numerics, and more\n" +
        "The C++ basic memory model, in depth\n" +
        "This fourth edition makes C++11 thoroughly accessible to programmers moving from C++98 or other languages, while introducing insights and techniques that even cutting-edge C++11 programmers will find indispensable.\n" +
        "\n" +
        "This book features an enhanced, layflat binding, which allows the book to stay open more easily when placed on a flat surface. This special binding method—noticeable by a small space inside the spine—also increases durability.",
        availableCount: 6,
        status: "AVAILABLE"
    }, {
        id: "0753555638",
        title: "Elon Musk: How the Billionaire CEO of SpaceX and Tesla is Shaping our Future",
        image: "https://books.google.com/books/content/images/frontcover/_LFSBgAAQBAJ?fife=w200-h300",
        author: "Ashlee Vance",
        isbn: "0753555638",
        category: "Biography",
        description: "Explore the whole new world of web development and create responsive web apps using PolymerAbout This Book\n" +
        "Get to grips with the principles of Material Design and Google Web components\n" +
        "Make full use of the Polymer Designer Tool, Polymer Starter Kit, and Dart to create responsive web apps\n" +
        "An in-depth guide with real-life examples so you can learn everything you need to know about Polymer\n" +
        "Who This Book Is For\n" +
        "If you are a beginner-level web developer who would like to learn the concepts of web development using the Polymer library, then this is the book for you. Knowledge of JavaScript and HTML is expected.\n" +
        "\n" +
        "What You Will Learn\n" +
        "Understand the basics of web components such as Shadow DOM, HTML imports, Templates, and custom elements\n" +
        "Familiarize yourself with the principles of Material Design\n" +
        "Install Polymer on your system and create your project structure\n" +
        "Use the different Polymer 1.0 elements in your code\n" +
        "Work with Polymer.dart and create your own app\n" +
        "Get to know the best practices in Polymer programming from the top guys in the Polymer team\n" +
        "In Detail\n" +
        "Polymer is a library that helps you develop fast, responsive applications for the desktop and mobile web. It uses the Web Components specifications for the components and Material Design concepts to create a beautiful user interface.\n" +
        "\n" +
        "This focused, fast-paced guide deals with Polymer web components. We will cover layouts, attributes, elements, and handling touch and gesture events. You will also see how to build custom web components and applications using Polymer. Don't want to code? You can make the most of the Polymer Designer Tool app and create your own app without coding at all. Finally, you will see how you can improve your Polymer application by reading the best practices from Google Polymer team.\n" +
        "\n" +
        "By the end of this book, you will be equipped with all the necessary skills to use Polymer to create custom web components.\n" +
        "\n" +
        "Style and approach\n" +
        "This is your guide to designing custom web components, and the concepts are explained in a conversational and easy-to-follow style. Each topic is explained through examples, with detailed explanations wherever required.",
        availableCount: 8,
        status: "AVAILABLE"
    }, {

        id: "0321965515",
        title: "Don't Make Me Think, Revisited: A Common Sense Approach to Web Usability, Edition 3",
        image: "https://books.google.com/books/content/images/frontcover/QlduAgAAQBAJ?fife=w200-h300",
        author: "Steve Krug",
        isbn: "0321965515",
        category: "Web Design",
        description: "Since Don’t Make Me Think was first published in 2000, hundreds of thousands of Web designers and developers have relied on usability guru Steve Krug’s guide to help them understand the principles of intuitive navigation and information design. Witty, commonsensical, and eminently practical, it’s one of the best-loved and most recommended books on the subject.\n" +
        "\n" +
        "Now Steve returns with fresh perspective to reexamine the principles that made Don’t Make Me Think a classic–with updated examples and a new chapter on mobile usability. And it’s still short, profusely illustrated…and best of all–fun to read.\n" +
        "\n" +
        "If you’ve read it before, you’ll rediscover what made Don’t Make Me Think so essential to Web designers and developers around the world. If you’ve never read it, you’ll see why so many people have said it should be required reading for anyone working on Web sites.\n" +
        "\n" +
        "\n" +
        "“After reading it over a couple of hours and putting its ideas to work for the past five years, I can say it has done more to improve my abilities as a Web designer than any other book.”\n" +
        "–Jeffrey Zeldman, author of Designing with Web Standards",
        availableCount: 10,
        status: "AVAILABLE"
    }, {
        id: "1617292575",
        title: "Node.js в действии. 2-е издание",
        image: "https://books.google.com/books/content/images/frontcover/s9VMDwAAQBAJ?fife=w200-h300",
        author: "Кантелон МайкХартер МаркГоловайчук TJРайлих Натан",
        isbn: "1617292575",
        description: "Реактивное программирование - совершенно новая и многообещающая парадигма, позволяющая эффективно решать задачи, связанные с созданием распределенных систем и программированием для JVM. Эта книга расскажет, как организовать поток задач, наладить обмен сообщениями между элементами программы, обеспечить параллельную и конкурентную обработку и создавать надежные, отказоустойчивые и гибкие приложения. Перед вами - основополагающая работа по шаблонам проектирования (design patterns) этой парадигмы. Книга проиллюстрирована многочисленными примерами и ориентирована на опытных Java- и Scala-разработчиков",
        availableCount: 11,
        status: "AVAILABLE"
    }, {

        id: "9781491931776",
        title: "React.js. Быстрый старт",
        image: "https://books.google.com/books/content/images/frontcover/g79TDgAAQBAJ?fife=w200-h300",
        author: "Стоян Стефанов",
        isbn: "9781491931776",
        description: "Незаменимая вводная книга по технологии React для взыскательных JavaScript-разработчиков. Все самое интересное о сверхпопулярном инструменте от компании Facebook. В книге рассмотрены основные концепции высокопроизводительного программирования при помощи React, реальные примеры кода и доступные блок-схемы. Прочитав ее, вы научитесь: • Создавать и использовать собственные компоненты React наряду с универсальными компонентами DOM • Писать компоненты для таблиц данных, позволяющие редактировать, сортировать таблицу, выполнять в ней поиск и экспортировать ее содержимое • Использовать дополнительный синтаксис JSX в качестве альтернативы для вызовов функций • Запускать низкоуровневый гибкий процесс сборки, который освободит вам время и поможет сосредоточиться на работе с React • Работать с инструментами ESLint, Flow и Jest, позволяющими проверять и тестировать код по мере разработки приложения • Обеспечивать коммуникацию между компонентами при помощи Flux В итоге у вас получится полноценное веб-приложение, позволяющее сохранять данные на стороне клиента.",
        availableCount: 0,
        status: "EXPECTED"
    }, {

        id: "1782168877",
        title: "Game Programming Using Qt: Beginner's Guide",
        image: "https://books.google.com/books/content/images/frontcover/6F0dDAAAQBAJ?fife=w200-h300",
        author: "Witold WysotaLorenz Haas",
        isbn: "1782168877",
        category: "Programming",
        description: "A complete guide to designing and building fun games with Qt and Qt Quick 2 using associated toolsetsAbout This Book\n" +
        "Learn to create simple 2D to complex 3D graphics and games using all possible tools and widgets available for game development in Qt\n" +
        "Understand technologies such as QML, Qt Quick, OpenGL, and Qt Creator, and learn the best practices to use them to design games\n" +
        "Learn Qt with the help of many sample games introduced step-by-step in each chapter\n" +
        "Who This Book Is For\n" +
        "If you want to create great graphical user interfaces and astonishing games with Qt, this book is ideal for you. Any previous knowledge of Qt is not required, however knowledge of C++ is mandatory.\n" +
        "\n" +
        "What You Will Learn\n" +
        "Install Qt on your system\n" +
        "Understand the basic concepts of every Qt game and application\n" +
        "Develop 2D object-oriented graphics using Qt Graphics View\n" +
        "Build multiplayer games or add a chat function to your games with Qt's Network module\n" +
        "Script your game with Qt Script\n" +
        "Program resolution-independent and fluid UI using QML and Qt Quick\n" +
        "Control your game flow as per the sensors of a mobile device\n" +
        "See how to test and debug your game easily with Qt Creator and Qt Test\n" +
        "In Detail\n" +
        "Qt is the leading cross-platform toolkit for all significant desktop, mobile, and embedded platforms and is becoming more popular by the day, especially on mobile and embedded devices. Despite its simplicity, it's a powerful tool that perfectly fits game developers' needs. Using Qt and Qt Quick, it is easy to build fun games or shiny user interfaces. You only need to create your game once and deploy it on all major platforms like iOS, Android, and WinRT without changing a single source file.\n" +
        "\n" +
        "The book begins with a brief introduction to creating an application and preparing a working environment for both desktop and mobile platforms. It then dives deeper into the basics of creating graphical interfaces and Qt core concepts of data processing and display before you try creating a game. As you progress through the chapters, you'll learn to enrich your games by implementing network connectivity and employing scripting. We then delve into Qt Quick, OpenGL, and various other tools to add game logic, design animation, add game physics, and build astonishing UI for the games. Towards the final chapters, you'll learn to exploit mobile device features such as accelerators and sensors to build engaging user experiences. If you are planning to learn about Qt and its associated toolsets to build apps and games, this book is a must have.\n" +
        "\n" +
        "Style and approach\n" +
        "This is an easy-to-follow, example-based, comprehensive introduction to all the major features in Qt. The content of each chapter is explained and organized around one or multiple simple game examples to learn Qt in a fun way.\n" +
        "\n",
        availableCount: 13,
        status: "AVAILABLE"
    }, {

        id: "1484228073",
        title: "Pro Spring 5: An In-Depth Guide to the Spring Framework and Its Tools, Edition 5",
        image: "https://books.google.com/books/content/images/frontcover/N-U5DwAAQBAJ?fife=w200-h300",
        author: "Iuliana CosminaRob HarropChris SchaeferClarence Ho",
        isbn: "1484228073",
        category: "Java Programming",
        description: "",
        availableCount: 286,
        status: "AVAILABLE"
    }, {
        id: "0596517742",
        title: "JavaScript: The Good Parts: The Good Parts",
        image: "https://books.google.com/books/content/images/frontcover/PXa2bby0oQ0C?fife=w200-h300",
        author: "Douglas Crockford",
        isbn: "0596517742",
        category: "JavaScript",
        description: "Most programming languages contain good and bad parts, but JavaScript has more than its share of the bad, having been developed and released in a hurry before it could be refined. This authoritative book scrapes away these bad features to reveal a subset of JavaScript that's more reliable, readable, and maintainable than the language as a whole—a subset you can use to create truly extensible and efficient code.\n" +
        "Considered the JavaScript expert by many people in the development community, author Douglas Crockford identifies the abundance of good ideas that make JavaScript an outstanding object-oriented programming language-ideas such as functions, loose typing, dynamic objects, and an expressive object literal notation. Unfortunately, these good ideas are mixed in with bad and downright awful ideas, like a programming model based on global variables.\n" +
        "\n" +
        "When Java applets failed, JavaScript became the language of the Web by default, making its popularity almost completely independent of its qualities as a programming language. In JavaScript: The Good Parts, Crockford finally digs through the steaming pile of good intentions and blunders to give you a detailed look at all the genuinely elegant parts of JavaScript, including:\n" +
        "\n" +
        "Syntax\n" +
        "Objects\n" +
        "Functions\n" +
        "Inheritance\n" +
        "Arrays\n" +
        "Regular expressions\n" +
        "Methods\n" +
        "Style\n" +
        "Beautiful features\n" +
        "The real beauty? As you move ahead with the subset of JavaScript that this book presents, you'll also sidestep the need to unlearn all the bad parts. Of course, if you want to find out more about the bad parts and how to use them badly, simply consult any other JavaScript book.\n" +
        "\n" +
        "With JavaScript: The Good Parts, you'll discover a beautiful, elegant, lightweight and highly expressive language that lets you create effective code, whether you're managing object libraries or just trying to get Ajax to run fast. If you develop sites or applications for the Web, this book is an absolute must.",
        availableCount: 15,
        status: "AVAILABLE"
    }, {
        id: "9351108511",
        title: "Java in a Nutshell: A Desktop Quick Reference, Edition 6",
        image: "https://books.google.com/books/content/images/frontcover/trDXBAAAQBAJ?fife=w200-h300",
        author: "Benjamin J EvansDavid Flanagan",
        isbn: "9351108511",
        category: "Java Programming",
        description: "The latest edition of Java in a Nutshell is designed to help experienced Java programmers get the most out of Java 7 and 8, but it’s also a learning path for new developers. Chock full of examples that demonstrate how to take complete advantage of modern Java APIs and development best practices, the first section of this thoroughly updated book provides a fast-paced, no-fluff introduction to the Java programming language and the core runtime aspects of the Java platform.\n" +
        "The second section is a reference to core concepts and APIs that shows you how to perform real programming work in the Java environment.\n" +
        "\n" +
        "Get up to speed on language details, including Java 8 changes\n" +
        "Learn object-oriented programming, using basic Java syntax\n" +
        "Explore generics, enumerations, annotations, and lambda expressions\n" +
        "Understand basic techniques used in object-oriented design\n" +
        "Examine concurrency and memory, and how they’re intertwined\n" +
        "Work with Java collections and handle common data formats\n" +
        "Delve into Java’s latest I/O APIs, including asynchronous channels\n" +
        "Use Nashorn to execute JavaScript on the Java Virtual Machine\n" +
        "Become familiar with development tools in OpenJDK",
        availableCount: 5,
        status: "AVAILABLE"
    }, {
        id: "1491904240",
        title: "You Don't Know JS: ES6 & Beyond",
        image: "https://books.google.com/books/content/images/frontcover/iOc6CwAAQBAJ?fife=w200-h300",
        author: "Kyle Simpson",
        isbn: '1491904240',
        category: "JavaScript",
        description: "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the \"You Don’t Know JS\" series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.\n" +
        "Like other books in this series, You Don’t Know JS: ES6 & Beyond dives into trickier parts of the language that many JavaScript programmers either avoid or know nothing about. Armed with this knowledge, you can achieve true JavaScript mastery.\n" +
        "\n" +
        "With this book, you will:\n" +
        "\n" +
        "Learn new ES6 syntax that eases the pain points of common programming idioms\n" +
        "Organize code with iterators, generators, modules, and classes\n" +
        "Express async flow control with Promises combined with generators\n" +
        "Use collections to work more efficiently with data in structured ways\n" +
        "Leverage new API helpers, including Array, Object, Math, Number, and String\n" +
        "Extend your program’s capabilities through meta programming\n" +
        "Preview features likely coming to JS beyond ES6",
        availableCount: 1,
        status: "AVAILABLE"
    }, {
        id: "9781640293663",
        title: "Прежде Чем Он Согрешит ",
        image: "https://books.google.com/books/content/images/frontcover/uVVMDwAAQBAJ?fife=w200-h300",
        author: "Блейк Пирс",
        isbn: "9781640293663",
        category: "Fiction ",
        description: "Блейк Пирс, автор бестселлера «КОГДА ОНА УШЛА» (бестселлера #1 с более чем 900 отзывов с высшей оценкой), представляет книгу #7 в увлекательной серии детективных романов о Макензи Уайт.\n" +
        "В «ПРЕЖДЕ ЧЕМ ОН СОГРЕШИТ» (Загадки Макензи Уайт—Книга 7) по всему Вашингтону полиция находит тела священников, распятые перед входом в храмы. Что это, акт возмездия? Искать ли преступника среди служителей церкви? Или это серийный убийца, открывший охоту на священников и движимый более зловещими мотивами?\n" +
        "\n" +
        "ФБР передаёт это дело специальному агенту Макензи Уайт, потому что оно перекликается с религиозными убийствами, которые она расследовала, когда охотилась на преступника по прозвищу «Страшила» в своём первом деле. Погрузившись в мир, в котором живут священники, Макензи пытается лучше понять смысл ритуалов, древних религиозных текстов и даже разум самого убийцы. При этом она также занята поиском убийцы отца, твёрдо решив, что на этот раз не даст ему уйти. Убийца священников намного более жесток, чем другие преступники, и в смертельной игре в кошки-мышки он подводит Макензи к черте, за которой начинается безумие.\n" +
        "\n" +
        "Мрачный психологический триллер с увлекательным сюжетом «ПРЕЖДЕ ЧЕМ ОН СОГРЕШИТ» – это книга #7 в захватывающей серии детективных романов, рассказывающих о любимой героине. От книги просто невозможно оторваться.\n" +
        "\n" +
        "Также не пропустите роман Блейка Пирса «КОГДА ОНА УШЛА» (Загадки Райли Пейдж—Книга #1), бестселлер #1 с более чем 900 отзывов с высшей оценкой. Роман доступен для бесплатного скачивания!",
        availableCount: 3,
        status: "AVAILABLE"
    }, {
        id: "8535902716",
        title: "The Martian",
        image: "https://books.google.com/books/content/images/frontcover/gdxlAQAAQBAJ?fife=w200-h300",
        author: "Andy Weir",
        isbn: "B017PNMYSK",
        category: "Fiction ",
        description: "The Sunday Times Bestseller behind the major new film from Ridley Scott starring Matt Damon and Jessica Chastain.\n" +
        "I’m stranded on Mars.\n" +
        "\n" +
        "I have no way to communicate with Earth.\n" +
        "\n" +
        "I’m in a Habitat designed to last 31 days.\n" +
        "\n" +
        "If the Oxygenator breaks down, I’ll suffocate. If the Water Reclaimer breaks down, I’ll die of thirst. If the Hab breaches, I’ll just kind of explode. If none of those things happen, I’ll eventually run out of food and starve to death.\n" +
        "\n" +
        "So yeah. I’m screwed.\n" +
        "\n" +
        "Andy Weir's second novel Artemis is now available",
        availableCount: 2,
        status: "AVAILABLE"
    }, {
        id: "8535902718",
        title: "Clean Code: A Handbook of Agile Software Craftsmanship",
        image: "https://books.google.com/books/content/images/frontcover/_i6bDeoCQzsC?fife=w200-h300",
        author: "Robert C. Martin",
        isbn: "0132350882",
        category: "Programming",
        description: "Even bad code can function. But if code isn’t clean, it can bring a development organization to its knees. Every year, countless hours and significant resources are lost because of poorly written code. But it doesn’t have to be that way.\n" +
        "\n" +
        "Noted software expert Robert C. Martin presents a revolutionary paradigm with Clean Code: A Handbook of Agile Software Craftsmanship . Martin has teamed up with his colleagues from Object Mentor to distill their best agile practice of cleaning code “on the fly” into a book that will instill within you the values of a software craftsman and make you a better programmer–but only if you work at it.\n" +
        "\n" +
        "What kind of work will you be doing? You’ll be reading code–lots of code. And you will be challenged to think about what’s right about that code, and what’s wrong with it. More importantly, you will be challenged to reassess your professional values and your commitment to your craft.\n" +
        "\n" +
        "Clean Code is divided into three parts. The first describes the principles, patterns, and practices of writing clean code. The second part consists of several case studies of increasing complexity. Each case study is an exercise in cleaning up code–of transforming a code base that has some problems into one that is sound and efficient. The third part is the payoff: a single chapter containing a list of heuristics and “smells” gathered while creating the case studies. The result is a knowledge base that describes the way we think when we write, read, and clean code.\n" +
        "\n" +
        "Readers will come away from this book understanding\n" +
        "How to tell the difference between good and bad code\n" +
        "How to write good code and how to transform bad code into good code\n" +
        "How to create good names, good functions, good objects, and good classes\n" +
        "How to format code for maximum readability\n" +
        "How to implement complete error handling without obscuring code logic\n" +
        "How to unit test and practice test-driven development\n" +
        "This book is a must for any developer, software engineer, project manager, team lead, or systems analyst with an interest in producing better code.",
        availableCount: 1,
        status: "AVAILABLE"
    }, {
        id: "8535902720",
        title: "Java Generics and Collections: Speed Up the Java Development Process",
        image: "https://books.google.com/books/content/images/frontcover/zaoK0Z2STlkC?fife=w200-h300",
        author: "Maurice Naftalin",
        category: "Java Programming",
        description: "This comprehensive guide shows you how to master the most importantchanges to Java since it was first released. Generics and the greatlyexpanded collection libraries have tremendously increased the power ofJava 5 and Java 6. But they have also confused many developers whohaven't known how to take advantage of these new features.\n" +
        "Java Generics and Collections covers everything from the mostbasic uses of generics to the strangest corner cases. It teaches youeverything you need to know about the collections libraries, so you'llalways know which collection is appropriate for any given task, andhow to use it.\n" +
        "\n" +
        "Topics covered include:\n" +
        "\n" +
        "Fundamentals of generics: type parameters and generic methods\n" +
        "Other new features: boxing and unboxing, foreach loops, varargs\n" +
        "Subtyping and wildcards\n" +
        "Evolution not revolution: generic libraries with legacy clients andgeneric clients with legacy libraries\n" +
        "Generics and reflection\n" +
        "Design patterns for generics\n" +
        "Sets, Queues, Lists, Maps, and their implementations\n" +
        "Concurrent programming and thread safety with collections\n" +
        "Performance implications of different collections\n" +
        "Generics and the new collection libraries they inspired take Java to anew level. If you want to take your software development practice toa new level, this book is essential reading.",
        availableCount: 226,
        status: "AVAILABLE"
    }, {
        id: "8535902721",
        title: "The C++ Programming Language: Edition 4",
        image: "https://books.google.com/books/content/images/frontcover/PSUNAAAAQBAJ?fife=w200-h300",
        author: "Bjarne Stroustrup",
        category: "C++",
        description: "The new C++11 standard allows programmers to express ideas more clearly, simply, and directly, and to write faster, more efficient code. Bjarne Stroustrup, the designer and original implementer of C++, has reorganized, extended, and completely rewritten his definitive reference and tutorial for programmers who want to use C++ most effectively.\n" +
        "The C++ Programming Language, Fourth Edition, delivers meticulous, richly explained, and integrated coverage of the entire language—its facilities, abstraction mechanisms, standard libraries, and key design techniques. Throughout, Stroustrup presents concise, “pure C++11” examples, which have been carefully crafted to clarify both usage and program design. To promote deeper understanding, the author provides extensive cross-references, both within the book and to the ISO standard.\n" +
        "\n" +
        "New C++11 coverage includes\n" +
        "\n" +
        "Support for concurrency\n" +
        "Regular expressions, resource management pointers, random numbers, and improved containers\n" +
        "General and uniform initialization, simplified for-statements, move semantics, and Unicode support\n" +
        "Lambdas, general constant expressions, control over class defaults, variadic templates, template aliases, and user-defined literals\n" +
        "Compatibility issues\n" +
        "Topics addressed in this comprehensive book include\n" +
        "\n" +
        "Basic facilities: type, object, scope, storage, computation fundamentals, and more\n" +
        "Modularity, as supported by namespaces, source files, and exception handling\n" +
        "C++ abstraction, including classes, class hierarchies, and templates in support of a synthesis of traditional programming, object-oriented programming, and generic programming\n" +
        "Standard Library: containers, algorithms, iterators, utilities, strings, stream I/O, locales, numerics, and more\n" +
        "The C++ basic memory model, in depth\n" +
        "This fourth edition makes C++11 thoroughly accessible to programmers moving from C++98 or other languages, while introducing insights and techniques that even cutting-edge C++11 programmers will find indispensable.\n" +
        "\n" +
        "This book features an enhanced, layflat binding, which allows the book to stay open more easily when placed on a flat surface. This special binding method—noticeable by a small space inside the spine—also increases durability.",
        availableCount: 20,
        status: "AVAILABLE"
    }, {
        id: "8535902723",
        title: "Elon Musk: How the Billionaire CEO of SpaceX and Tesla is Shaping our Future",
        image: "https://books.google.com/books/content/images/frontcover/_LFSBgAAQBAJ?fife=w200-h300",
        author: "Ashlee Vance",
        category: "Biography",
        description: "South African born Elon Musk is the renowned entrepreneur and innovator behind PayPal, SpaceX, Tesla, and SolarCity. Musk wants to save our planet; he wants to send citizens into space, to form a colony on Mars; he wants to make money while doing these things; and he wants us all to know about it. He is the real-life inspiration for the Iron Man series of films starring Robert Downey Junior.\n" +
        "The personal tale of Musk’s life comes with all the trappings one associates with a great, drama-filled story. He was a freakishly bright kid who was bullied brutally at school, and abused by his father. In the midst of these rough conditions, and the violence of apartheid South Africa, Musk still thrived academically and attended the University of Pennsylvania, where he paid his own way through school by turning his house into a club and throwing massive parties.\n" +
        "\n" +
        "He started a pair of huge dot-com successes, including PayPal, which eBay acquired for $1.5 billion in 2002. Musk was forced out as CEO and so began his lost years in which he decided to go it alone and baffled friends by investing his fortune in rockets and electric cars. Meanwhile Musk’s marriage disintegrated as his technological obsessions took over his life ...\n" +
        "\n" +
        "Elon Musk is the Steve Jobs of the present and the future, and for the past twelve months, he has been shadowed by tech reporter, Ashlee Vance. Elon Musk: How the Billionaire CEO of Spacex and Tesla is Shaping our Future is an important, exciting and intelligent account of the real-life Iron Man.",
        availableCount: 26,
        status: "AVAILABLE"
    }, {
        id: "8535902725",
        title: "Don't Make Me Think, Revisited: A Common Sense Approach to Web Usability, Edition 3",
        image: "https://books.google.com/books/content/images/frontcover/QlduAgAAQBAJ?fife=w200-h300",
        author: "Steve Krug",
        category: "Web Design",
        description: "Since Don’t Make Me Think was first published in 2000, hundreds of thousands of Web designers and developers have relied on usability guru Steve Krug’s guide to help them understand the principles of intuitive navigation and information design. Witty, commonsensical, and eminently practical, it’s one of the best-loved and most recommended books on the subject.\n" +
        "\n" +
        "Now Steve returns with fresh perspective to reexamine the principles that made Don’t Make Me Think a classic–with updated examples and a new chapter on mobile usability. And it’s still short, profusely illustrated…and best of all–fun to read.\n" +
        "\n" +
        "If you’ve read it before, you’ll rediscover what made Don’t Make Me Think so essential to Web designers and developers around the world. If you’ve never read it, you’ll see why so many people have said it should be required reading for anyone working on Web sites.\n" +
        "\n" +
        "\n" +
        "“After reading it over a couple of hours and putting its ideas to work for the past five years, I can say it has done more to improve my abilities as a Web designer than any other book.”\n" +
        "–Jeffrey Zeldman, author of Designing with Web Standards",
        availableCount: 6,
        status: "AVAILABLE"
    }, {
        id: "8535902726",
        title: "Node.js в действии. 2-е издание",
        image: "https://books.google.com/books/content/images/frontcover/s9VMDwAAQBAJ?fife=w200-h300",
        author: "Кантелон МайкХартер МаркГоловайчук TJРайлих Натан",
        category: "JavaScript",
        description: "Реактивное программирование - совершенно новая и многообещающая парадигма, позволяющая эффективно решать задачи, связанные с созданием распределенных систем и программированием для JVM. Эта книга расскажет, как организовать поток задач, наладить обмен сообщениями между элементами программы, обеспечить параллельную и конкурентную обработку и создавать надежные, отказоустойчивые и гибкие приложения. Перед вами - основополагающая работа по шаблонам проектирования (design patterns) этой парадигмы. Книга проиллюстрирована многочисленными примерами и ориентирована на опытных Java- и Scala-разработчиков",
        availableCount: 14,
        status: "AVAILABLE"
    }, {
        id: "8535902727",
        title: "React.js. Быстрый старт",
        image: "https://books.google.com/books/content/images/frontcover/g79TDgAAQBAJ?fife=w200-h300",
        author: "Стоян Стефанов",
        category: "JavaScript",
        description: "Незаменимая вводная книга по технологии React для взыскательных JavaScript-разработчиков. Все самое интересное о сверхпопулярном инструменте от компании Facebook. В книге рассмотрены основные концепции высокопроизводительного программирования при помощи React, реальные примеры кода и доступные блок-схемы. Прочитав ее, вы научитесь: • Создавать и использовать собственные компоненты React наряду с универсальными компонентами DOM • Писать компоненты для таблиц данных, позволяющие редактировать, сортировать таблицу, выполнять в ней поиск и экспортировать ее содержимое • Использовать дополнительный синтаксис JSX в качестве альтернативы для вызовов функций • Запускать низкоуровневый гибкий процесс сборки, который освободит вам время и поможет сосредоточиться на работе с React • Работать с инструментами ESLint, Flow и Jest, позволяющими проверять и тестировать код по мере разработки приложения • Обеспечивать коммуникацию между компонентами при помощи Flux В итоге у вас получится полноценное веб-приложение, позволяющее сохранять данные на стороне клиента.",
        availableCount: 4,
        status: "AVAILABLE"
    }, {
        id: "8535902728",
        title: "Game Programming Using Qt: Beginner's Guide",
        image: "https://books.google.com/books/content/images/frontcover/6F0dDAAAQBAJ?fife=w200-h300",
        author: "Witold WysotaLorenz Haas",
        category: "Programming",
        description: "A complete guide to designing and building fun games with Qt and Qt Quick 2 using associated toolsetsAbout This Book\n" +
        "Learn to create simple 2D to complex 3D graphics and games using all possible tools and widgets available for game development in Qt\n" +
        "Understand technologies such as QML, Qt Quick, OpenGL, and Qt Creator, and learn the best practices to use them to design games\n" +
        "Learn Qt with the help of many sample games introduced step-by-step in each chapter\n" +
        "Who This Book Is For\n" +
        "If you want to create great graphical user interfaces and astonishing games with Qt, this book is ideal for you. Any previous knowledge of Qt is not required, however knowledge of C++ is mandatory.\n" +
        "\n" +
        "What You Will Learn\n" +
        "Install Qt on your system\n" +
        "Understand the basic concepts of every Qt game and application\n" +
        "Develop 2D object-oriented graphics using Qt Graphics View\n" +
        "Build multiplayer games or add a chat function to your games with Qt's Network module\n" +
        "Script your game with Qt Script\n" +
        "Program resolution-independent and fluid UI using QML and Qt Quick\n" +
        "Control your game flow as per the sensors of a mobile device\n" +
        "See how to test and debug your game easily with Qt Creator and Qt Test\n" +
        "In Detail\n" +
        "Qt is the leading cross-platform toolkit for all significant desktop, mobile, and embedded platforms and is becoming more popular by the day, especially on mobile and embedded devices. Despite its simplicity, it's a powerful tool that perfectly fits game developers' needs. Using Qt and Qt Quick, it is easy to build fun games or shiny user interfaces. You only need to create your game once and deploy it on all major platforms like iOS, Android, and WinRT without changing a single source file.\n" +
        "\n" +
        "The book begins with a brief introduction to creating an application and preparing a working environment for both desktop and mobile platforms. It then dives deeper into the basics of creating graphical interfaces and Qt core concepts of data processing and display before you try creating a game. As you progress through the chapters, you'll learn to enrich your games by implementing network connectivity and employing scripting. We then delve into Qt Quick, OpenGL, and various other tools to add game logic, design animation, add game physics, and build astonishing UI for the games. Towards the final chapters, you'll learn to exploit mobile device features such as accelerators and sensors to build engaging user experiences. If you are planning to learn about Qt and its associated toolsets to build apps and games, this book is a must have.\n" +
        "\n" +
        "Style and approach\n" +
        "This is an easy-to-follow, example-based, comprehensive introduction to all the major features in Qt. The content of each chapter is explained and organized around one or multiple simple game examples to learn Qt in a fun way.\n" +
        "\n",
        availableCount: 13,
        status: "AVAILABLE"
    }, {
        id: "8535902729",
        title: "Pro Spring 5: An In-Depth Guide to the Spring Framework and Its Tools, Edition 5",
        image: "https://books.google.com/books/content/images/frontcover/N-U5DwAAQBAJ?fife=w200-h300",
        author: "Iuliana CosminaRob HarropChris SchaeferClarence Ho",
        category: "Java Programming",
        description: "Master Spring basics and core topics, and share the authors’ insights and real–world experiences with remoting, Hibernate, and EJB. Beyond the basics, you'll learn how to leverage the Spring Framework to build the various tiers and parts of an enterprise Java application: transactions, web and presentation tiers, deployment, and much more. A full sample application allows you to apply many of the technologies and techniques covered in Pro Spring 5 and see how they work together.\n" +
        "This book updates the perennial bestseller with the latest that the new Spring Framework 5 has to offer. Now in its fifth edition, this popular title is by far the most comprehensive and definitive treatment of Spring available. It covers the new functional web framework and interoperability with Java 9.\n" +
        "\n" +
        "After reading this definitive book, you'll be armed with the power of Spring to build complex Spring applications, top to bottom.\n" +
        "\n" +
        "The agile, lightweight, open-source Spring Framework\n" +
        "\n" +
        "continues to be the de facto leading enterprise Java application development framework for today's Java programmers and developers. It works with other leading open-source, agile, and lightweight Java technologies such as Hibernate, Groovy, MyBatis, and more. Spring now works with Java EE and JPA 2 as well.\n" +
        "What You'll Learn\n" +
        "\n" +
        "Discover what’s new in Spring Framework 5\n" +
        "Use the Spring Framework with Java 9\n" +
        "Master data access and transactions \n" +
        "Work with the new functional web framework \n" +
        "Create microservices and other web services \n" +
        "Who This Book Is For\n" +
        "\n" +
        "Experienced Java and enterprise Java developers and programmers. Some experience with Spring highly recommended.",
        availableCount: 286,
        status: "AVAILABLE"
    }, {
        id: "853590277530",
        title: "JavaScript: The Good Parts: The Good Parts",
        image: "https://books.google.com/books/content/images/frontcover/PXa2bby0oQ0C?fife=w200-h300",
        author: "Douglas Crockford",
        category: "JavaScript",
        description: "Most programming languages contain good and bad parts, but JavaScript has more than its share of the bad, having been developed and released in a hurry before it could be refined. This authoritative book scrapes away these bad features to reveal a subset of JavaScript that's more reliable, readable, and maintainable than the language as a whole—a subset you can use to create truly extensible and efficient code.\n" +
        "Considered the JavaScript expert by many people in the development community, author Douglas Crockford identifies the abundance of good ideas that make JavaScript an outstanding object-oriented programming language-ideas such as functions, loose typing, dynamic objects, and an expressive object literal notation. Unfortunately, these good ideas are mixed in with bad and downright awful ideas, like a programming model based on global variables.\n" +
        "\n" +
        "When Java applets failed, JavaScript became the language of the Web by default, making its popularity almost completely independent of its qualities as a programming language. In JavaScript: The Good Parts, Crockford finally digs through the steaming pile of good intentions and blunders to give you a detailed look at all the genuinely elegant parts of JavaScript, including:\n" +
        "\n" +
        "Syntax\n" +
        "Objects\n" +
        "Functions\n" +
        "Inheritance\n" +
        "Arrays\n" +
        "Regular expressions\n" +
        "Methods\n" +
        "Style\n" +
        "Beautiful features\n" +
        "The real beauty? As you move ahead with the subset of JavaScript that this book presents, you'll also sidestep the need to unlearn all the bad parts. Of course, if you want to find out more about the bad parts and how to use them badly, simply consult any other JavaScript book.\n" +
        "\n" +
        "With JavaScript: The Good Parts, you'll discover a beautiful, elegant, lightweight and highly expressive language that lets you create effective code, whether you're managing object libraries or just trying to get Ajax to run fast. If you develop sites or applications for the Web, this book is an absolute must.",
        availableCount: 15,
        status: "AVAILABLE"
    }, {
        id: "8535902731",
        title: "Java in a Nutshell: A Desktop Quick Reference, Edition 6",
        image: "https://books.google.com/books/content/images/frontcover/trDXBAAAQBAJ?fife=w200-h300",
        author: "Benjamin J EvansDavid Flanagan",
        category: "Java Programming",
        description: "The latest edition of Java in a Nutshell is designed to help experienced Java programmers get the most out of Java 7 and 8, but it’s also a learning path for new developers. Chock full of examples that demonstrate how to take complete advantage of modern Java APIs and development best practices, the first section of this thoroughly updated book provides a fast-paced, no-fluff introduction to the Java programming language and the core runtime aspects of the Java platform.\n" +
        "The second section is a reference to core concepts and APIs that shows you how to perform real programming work in the Java environment.\n" +
        "\n" +
        "Get up to speed on language details, including Java 8 changes\n" +
        "Learn object-oriented programming, using basic Java syntax\n" +
        "Explore generics, enumerations, annotations, and lambda expressions\n" +
        "Understand basic techniques used in object-oriented design\n" +
        "Examine concurrency and memory, and how they’re intertwined\n" +
        "Work with Java collections and handle common data formats\n" +
        "Delve into Java’s latest I/O APIs, including asynchronous channels\n" +
        "Use Nashorn to execute JavaScript on the Java Virtual Machine\n" +
        "Become familiar with development tools in OpenJDK",
        availableCount: 5,
        status: "AVAILABLE"
    }, {
        id: "8535902732",
        title: "You Don't Know JS: ES6 & Beyond",
        image: "https://books.google.com/books/content/images/frontcover/iOc6CwAAQBAJ?fife=w200-h300",
        author: "Kyle Simpson",
        category: "JavaScript",
        description: "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the \"You Don’t Know JS\" series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.\n" +
        "Like other books in this series, You Don’t Know JS: ES6 & Beyond dives into trickier parts of the language that many JavaScript programmers either avoid or know nothing about. Armed with this knowledge, you can achieve true JavaScript mastery.\n" +
        "\n" +
        "With this book, you will:\n" +
        "\n" +
        "Learn new ES6 syntax that eases the pain points of common programming idioms\n" +
        "Organize code with iterators, generators, modules, and classes\n" +
        "Express async flow control with Promises combined with generators\n" +
        "Use collections to work more efficiently with data in structured ways\n" +
        "Leverage new API helpers, including Array, Object, Math, Number, and String\n" +
        "Extend your program’s capabilities through meta programming\n" +
        "Preview features likely coming to JS beyond ES6",
        availableCount: 1,
        status: "AVAILABLE"
    }
];
/* exported expectedSoonLibraryBooks */

const expectedSoonLibraryBooks = [{
    id: "8535902733",
    title: "Elements of Programming Interviews: The Insiders' Guide",
    image: "https://books.google.com/books/content/images/frontcover/y6FLBQAAQBAJ?fife=w200-h300",
    author: "Adnan AzizTsung-Hsien LeeAmit Prakash",
    category: "Business ",
    description: "The core of EPI is a collection of over 300 problems with detailed solutions, including 100 figures, 250 tested programs, and 150 variants. The problems are representative of questions asked at the leading software companies.\n" +
    "\n" +
    "The book begins with a summary of the nontechnical aspects of interviewing, such as common mistakes, strategies for a great interview, perspectives from the other side of the table, tips on negotiating the best offer, and a guide to the best ways to use EPI.\n" +
    "\n" +
    "The technical core of EPI is a sequence of chapters on basic and advanced data structures, searching, sorting, broad algorithmic principles, concurrency, and system design. Each chapter consists of a brief review, followed by a broad and thought-provoking series of problems. We include a summary of data structure, algorithm, and problem solving patterns.",
    status: "EXPECTED_SOON"
}];
/* exported userTestBooks */

const userTestBooks = {
        reserved: [{
            id: "8535902717",
            title: "Effective Java: Edition 2",
            image: "https://books.google.com/books/content/images/frontcover/ka2VUBqHiWkC?fife=w200-h300",
            author: "Joshua Bloch",
            category: "Java Programming",
            description: "Are you looking for a deeper understanding of the Java™ programming language so that you can write code that is clearer, more correct, more robust, and more reusable? Look no further! Effective Java™, Second Edition, brings together seventy-eight indispensable programmer’s rules of thumb: working, best-practice solutions for the programming challenges you encounter every day.\n" +
            "This highly anticipated new edition of the classic, Jolt Award-winning work has been thoroughly updated to cover Java SE 5 and Java SE 6 features introduced since the first edition. Bloch explores new design patterns and language idioms, showing you how to make the most of features ranging from generics to enums, annotations to autoboxing.\n" +
            "\n" +
            "Each chapter in the book consists of several “items” presented in the form of a short, standalone essay that provides specific advice, insight into Java platform subtleties, and outstanding code examples. The comprehensive descriptions and explanations for each item illuminate what to do, what not to do, and why.\n" +
            "\n" +
            "Highlights include:\n" +
            "\n" +
            "New coverage of generics, enums, annotations, autoboxing, the for-each loop, varargs, concurrency utilities, and much more\n" +
            "Updated techniques and best practices on classic topics, including objects, classes, libraries, methods, and serialization\n" +
            "How to avoid the traps and pitfalls of commonly misunderstood subtleties of the language\n" +
            "Focus on the language and its most fundamental libraries: java.lang, java.util, and, to a lesser extent, java.util.concurrent and java.io\n" +
            "Simply put, Effective Java™, Second Edition, presents the most practical, authoritative guidelines available for writing efficient, well-designed programs.",
            date: new Date(new Date().getTime() + THREE_DAYS * MILLISECONDS_IN_ONE_DAY),
            status: "READY_TO_PICK_UP"
        }, {

            id: "8535902719",
            title: "Java 8 Preview Sampler",
            image: "https://books.google.com/books/content/images/frontcover/5FVOAwAAQBAJ?fife=w200-h300",
            author: "Herbert Schildt",
            category: "Java Programming",
            description: "In this exclusive eBook, preview excerpts from brand-new and forthcoming Oracle Press Java JDK 8 books. Written by leading Java experts, Oracle Press books offer the most definitive, complete, and up-to-date coverage of the latest Java release. Featuring an introduction by bestselling programming author Herb Schildt, this eBook includes chapters from the following Oracle Press books:\n" +
            "Java: The Complete Reference, Ninth Edition by Herb Schildt\n" +
            "Java: A Beginner’s Guide, Sixth Edition by Herb Schildt\n" +
            "Mastering Lambdas: Java Programming in a Multicore World by Maurice Naftalin\n" +
            "Quick Start Guide to JavaFX by J.F. DiMarzio\n" +
            "Mastering JavaFX 8 Controls: Create Custom JavaFX 8 Controls for Cross-Platform Applications by Hendrik Ebbers",
            date: new Date(new Date().getTime() + THREE_WEEKS * MILLISECONDS_IN_ONE_DAY),
            status: "RESERVED"
        }
        ],
        borrowed: [{
            id: "8535902724",
            title: "Getting Started with Polymer",
            image: "https://books.google.com/books/content/images/frontcover/fAFwDQAAQBAJ?fife=w200-h300",
            author: "Arshak Khachatrian",
            category: "Web Design",
            description: "Explore the whole new world of web development and create responsive web apps using PolymerAbout This Book\n" +
            "Get to grips with the principles of Material Design and Google Web components\n" +
            "Make full use of the Polymer Designer Tool, Polymer Starter Kit, and Dart to create responsive web apps\n" +
            "An in-depth guide with real-life examples so you can learn everything you need to know about Polymer\n" +
            "Who This Book Is For\n" +
            "If you are a beginner-level web developer who would like to learn the concepts of web development using the Polymer library, then this is the book for you. Knowledge of JavaScript and HTML is expected.\n" +
            "\n" +
            "What You Will Learn\n" +
            "Understand the basics of web components such as Shadow DOM, HTML imports, Templates, and custom elements\n" +
            "Familiarize yourself with the principles of Material Design\n" +
            "Install Polymer on your system and create your project structure\n" +
            "Use the different Polymer 1.0 elements in your code\n" +
            "Work with Polymer.dart and create your own app\n" +
            "Get to know the best practices in Polymer programming from the top guys in the Polymer team\n" +
            "In Detail\n" +
            "Polymer is a library that helps you develop fast, responsive applications for the desktop and mobile web. It uses the Web Components specifications for the components and Material Design concepts to create a beautiful user interface.\n" +
            "\n" +
            "This focused, fast-paced guide deals with Polymer web components. We will cover layouts, attributes, elements, and handling touch and gesture events. You will also see how to build custom web components and applications using Polymer. Don't want to code? You can make the most of the Polymer Designer Tool app and create your own app without coding at all. Finally, you will see how you can improve your Polymer application by reading the best practices from Google Polymer team.\n" +
            "\n" +
            "By the end of this book, you will be equipped with all the necessary skills to use Polymer to create custom web components.\n" +
            "\n" +
            "Style and approach\n" +
            "This is your guide to designing custom web components, and the concepts are explained in a conversational and easy-to-follow style. Each topic is explained through examples, with detailed explanations wherever required.",
            dueDate: new Date(new Date().getTime() + TWO_WEEKS * MILLISECONDS_IN_ONE_DAY),
            status: "BORROWED",
            isAllowedLoanExtension: true
        }],
        overdue: [{
            id: "8535902722",
            title: "Mastering Lambdas: Java Programming in a Multicore World",
            image: "https://books.google.com/books/content/images/frontcover/Zw5oBAAAQBAJ?fife=w200-h300",
            author: "Maurice Naftalin",
            category: "Java Programming",
            description: "In this exclusive eBook, preview excerpts from brand-new and forthcoming Oracle Press Java JDK 8 books. Written by leading Java experts, Oracle Press books offer the most definitive, complete, and up-to-date coverage of the latest Java release. Featuring an introduction by bestselling programming author Herb Schildt, this eBook includes chapters from the following Oracle Press books:\n" +
            "Java: The Complete Reference, Ninth Edition by Herb Schildt\n" +
            "Java: A Beginner’s Guide, Sixth Edition by Herb Schildt\n" +
            "Mastering Lambdas: Java Programming in a Multicore World by Maurice Naftalin\n" +
            "Quick Start Guide to JavaFX by J.F. DiMarzio\n" +
            "Mastering JavaFX 8 Controls: Create Custom JavaFX 8 Controls for Cross-Platform Applications by Hendrik Ebbers",
            dueDate: new Date(new Date().getTime() - TWO_DAYS * MILLISECONDS_IN_ONE_DAY),
            status: "OVERDUE",
            isAllowedLoanExtension: false
        }],
        shouldReturnSoon: [{
            id: "8535902724",
            title: "Getting Started with Polymer",
            image: "https://books.google.com/books/content/images/frontcover/fAFwDQAAQBAJ?fife=w200-h300",
            author: "Arshak Khachatrian",
            category: "Web Design",
            description: "Explore the whole new world of web development and create responsive web apps using PolymerAbout This Book\n" +
            "Get to grips with the principles of Material Design and Google Web components\n" +
            "Make full use of the Polymer Designer Tool, Polymer Starter Kit, and Dart to create responsive web apps\n" +
            "An in-depth guide with real-life examples so you can learn everything you need to know about Polymer\n" +
            "Who This Book Is For\n" +
            "If you are a beginner-level web developer who would like to learn the concepts of web development using the Polymer library, then this is the book for you. Knowledge of JavaScript and HTML is expected.\n" +
            "\n" +
            "What You Will Learn\n" +
            "Understand the basics of web components such as Shadow DOM, HTML imports, Templates, and custom elements\n" +
            "Familiarize yourself with the principles of Material Design\n" +
            "Install Polymer on your system and create your project structure\n" +
            "Use the different Polymer 1.0 elements in your code\n" +
            "Work with Polymer.dart and create your own app\n" +
            "Get to know the best practices in Polymer programming from the top guys in the Polymer team\n" +
            "In Detail\n" +
            "Polymer is a library that helps you develop fast, responsive applications for the desktop and mobile web. It uses the Web Components specifications for the components and Material Design concepts to create a beautiful user interface.\n" +
            "\n" +
            "This focused, fast-paced guide deals with Polymer web components. We will cover layouts, attributes, elements, and handling touch and gesture events. You will also see how to build custom web components and applications using Polymer. Don't want to code? You can make the most of the Polymer Designer Tool app and create your own app without coding at all. Finally, you will see how you can improve your Polymer application by reading the best practices from Google Polymer team.\n" +
            "\n" +
            "By the end of this book, you will be equipped with all the necessary skills to use Polymer to create custom web components.\n" +
            "\n" +
            "Style and approach\n" +
            "This is your guide to designing custom web components, and the concepts are explained in a conversational and easy-to-follow style. Each topic is explained through examples, with detailed explanations wherever required.",
            dueDate: new Date(new Date().getTime() + THREE_DAYS * MILLISECONDS_IN_ONE_DAY),
            status: "BORROWED",
            isAllowedLoanExtension: false
        }]
    }
;

const testCategories = ["Java Programming", "Web Design", "Business", "JavaScript", "Programming", "Biography", "Fiction", "C++"].sort();

