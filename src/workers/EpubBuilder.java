package workers;

import model.Chapter;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class EpubBuilder {

    public void build(String title, List<Chapter> chapters) throws Exception {
        Book book = new Book();
        book.getMetadata().addTitle(title);

        for (Chapter c : chapters) {
            String html =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" " +
                "\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head><title>" + c.getTitle() + "</title></head>\n" +
                "<body>\n" +
                "<h1>" + c.getTitle() + "</h1>\n" +
                "<p>" + c.getContent().replace("&", "&amp;") + "</p>\n" +
                "</body></html>";

            // Unique filename for each chapter inside the EPUB
            String fileName = "chapter-" + c.getId() + ".xhtml";

            // Convert XHTML string into a resource (file inside EPUB)
            Resource resource = new Resource(html.getBytes("UTF-8"), fileName);
            book.addSection("Chapter " + c.getId(), resource);
        }

        // Save EPUB file to user's Downloads folder.
        String outputPath = System.getProperty("user.home") + File.separator
                          + "Downloads" + File.separator + title + ".epub";

        // Write EPUB file to disk
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            new EpubWriter().write(book, out);
        }

        System.out.println("EPUB saved to: " + outputPath);
    }
}