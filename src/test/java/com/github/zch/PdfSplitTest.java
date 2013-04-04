package com.github.zch;

import org.apache.maven.plugin.MojoExecutionException;
import org.easymock.EasyMock;
import org.junit.Test;

import java.net.URL;

public class PdfSplitTest {
    private static final URL BOV_URL = PdfSplitTest.class.getResource("/book-of-vaadin.pdf");

    @Test
    public void testFindChapter22() throws Exception {
        assertChapterPages("Chapter 22. Mobile Applications with TouchKit", 479, 504);
    }

    @Test
    public void testFindChapter23() throws Exception {
        assertChapterPages("Chapter 23. Vaadin TestBench", 505, 542);
    }

    @Test
    public void testFindAppendixA() throws Exception {
        assertChapterPages("Appendix A. Songs of Vaadin", 543, 546);
    }

    private void assertChapterPages(String chapter, int startPage, int endPage) throws MojoExecutionException {
        PdfSplitMojo splitter = EasyMock.createMockBuilder(PdfSplitMojo.class).addMockedMethod("extractPageRange", int.class, int.class).createMock();
        splitter.extractPageRange(startPage, endPage);
        EasyMock.expectLastCall().once();
        EasyMock.replay(splitter);

        splitter.setChapter(chapter);
        splitter.setInputUrl(BOV_URL);
        splitter.execute();

        EasyMock.verify(splitter);
    }
}
