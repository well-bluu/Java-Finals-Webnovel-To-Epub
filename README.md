# WebNovel to EPUB

A multithreaded Java application that downloads chapters from RoyalRoad and builds them into one EPUB file.

## Current Support

- RoyalRoad fiction pages (for example: https://www.royalroad.com/fiction/12345/example-title)

## Features

- Producer-Consumer multithreading (1 producer + 3 consumers)
- Automatic chapter URL discovery from the fiction page
- Concurrent chapter downloading with retry-safe error handling
- EPUB creation using epublib
- Output saved to your Downloads folder

## Requirements

- Java 17 or higher (project is currently running on JDK 24 in your workspace)
- Included dependency JARs:
  - lib/jsoup/jsoup-1.22.1.jar
  - lib/jars/epublib-core-3.1-complete.jar

## Project Structure

```
src/
├── Main.java
├── factory/
│   └── NovelSiteFactory.java
├── interfaces/
│   └── NovelSite.java
├── model/
│   ├── Chapter.java
│   └── Task.java
├── sites/
│   └── RoyalRoadSite.java
└── workers/
    ├── ChapterQueue.java
    ├── Consumer.java
    ├── EpubBuilder.java
    └── Producer.java
```

## How It Works

1. Main reads a novel URL.
2. NovelSiteFactory selects the site parser (currently RoyalRoad).
3. RoyalRoadSite collects all chapter URLs.
4. Producer pushes chapter tasks into a shared queue.
5. Consumers fetch chapter pages in parallel and parse title/content.
6. EpubBuilder sorts chapters and writes one EPUB file.

## Build and Run (PowerShell)

From the project root:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
Get-ChildItem -Path src -Recurse -Filter *.java |
    ForEach-Object FullName |
    Set-Content sources.txt

javac -cp "lib/jsoup/jsoup-1.22.1.jar;lib/jars/epublib-core-3.1-complete.jar" -d bin @sources.txt
java -cp "bin;lib/jsoup/jsoup-1.22.1.jar;lib/jars/epublib-core-3.1-complete.jar" Main
```

When prompted:

```text
Enter novel URL: https://www.royalroad.com/fiction/12345/example-title
```

Output file:

```text
C:\Users\<your-user>\Downloads\<novel-slug>.epub
```

## Example Console Output

<img width="514" height="318" alt="Screenshot 2026-04-09 151337" src="https://github.com/user-attachments/assets/115dc6e8-3a6a-461d-b521-c94751a6e1bf" />


## OOP and Concurrency Notes

- Abstraction: NovelSite defines the site parsing contract.
- Polymorphism: RoyalRoadSite provides a concrete implementation.
- Factory pattern: NovelSiteFactory maps URL to parser implementation.
- Thread-safe communication: ChapterQueue wraps a BlockingQueue.
- Thread-safe chapter storage: ConcurrentHashMap is used by consumers.
- Graceful shutdown: Producer sends one poison pill task per consumer.

## Add Support for Another Site

1. Create a new class under src/sites that implements NovelSite.
2. Implement parsing methods for title, content, and chapter URL collection.
3. Register the site in NovelSiteFactory.getSiteByUrl.

## Notes and Limitations

- Only RoyalRoad is supported right now.
- Each consumer waits about 3-4 seconds between requests to reduce rate-limit risk.
- Failed chapters are skipped, and the EPUB is still generated from successful downloads.
