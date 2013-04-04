package com.github.zch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.util.PageExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Goal which splits a pdf file.
 */
@Mojo(name = "split", defaultPhase = LifecyclePhase.PACKAGE)
public class PdfSplitMojo extends AbstractMojo {
    /**
     * Location of the pdf.
     */
    @Parameter(property = "inputUrl", required = false)
    private URL inputUrl;

    /**
     * Location of the target file.
     */
    @Parameter(property = "outFile", required = true)
    private File outFile;

    /**
     * A range of pages to extract, e.g. "200-215"
     */
    @Parameter(property = "pageRange", required = false)
    private String pageRange;

    @Parameter(property = "chapter", required = false)
    private String chapter;

    private PDDocument doc;

    public void execute() throws MojoExecutionException {
        try {
            doc = PDDocument.load(inputUrl);
            if (chapter != null) {
                extractChapter();
            } else if (pageRange != null) {
                extractPageRange(getStartPage(), getEndPage());
            } else {
                getLog().error("No chapter or page range defined, not doing anything");
            }
            doc.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't load PDF", e);
        }
    }

    private List<PDPage> allPages;
    void extractChapter() throws MojoExecutionException {
        allPages = doc.getDocumentCatalog().getAllPages();
        PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();
        recurseExtractChapter(root.getFirstChild());
    }

    void recurseExtractChapter(PDOutlineItem item) throws MojoExecutionException {
        while (item != null) {
            if (item.getTitle().equals(chapter)) {
                try {
                    PDPage page = item.findDestinationPage(doc);
                    int start = allPages.indexOf(page) + 1;
                    int end = findFirstPageNumOfNextSection(doc, item);
                    extractPageRange(start, end);
                } catch (MojoExecutionException e) {
                    throw e;
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not find the pages to extract", e);
                }
            } else if (item.getFirstChild() != null) {
                recurseExtractChapter(item.getFirstChild());
            }
            item = item.getNextSibling();
        }
    }

    int findFirstPageNumOfNextSection(PDDocument doc, PDOutlineItem item) throws IOException {
        if (item.getNextSibling() != null) {
            return allPages.indexOf(item.getNextSibling().findDestinationPage(doc));
        } else {
            if (item.getParent() instanceof PDDocumentOutline) {
                return doc.getNumberOfPages();
            }
            return findFirstPageNumOfNextSection(doc, (PDOutlineItem) item.getParent());
        }
    }

    void extractPageRange(int start, int end) throws MojoExecutionException {
        try {
            PageExtractor extractor = new PageExtractor(doc, start, end);
            PDDocument extracted = extractor.extract();
            outFile.getParentFile().mkdirs();
            FileOutputStream outputStream = new FileOutputStream(outFile);
            extracted.save(outputStream);
            outputStream.close();
            extracted.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't extract pages from PDF", e);
        } catch (COSVisitorException e) {
            throw new MojoExecutionException("Couldn't extract pages from PDF", e);
        }
    }

    int getEndPage() {
        return Integer.valueOf(pageRange.split("-")[1]);
    }

    int getStartPage() {
        return Integer.valueOf(pageRange.split("-")[0]);
    }

    public void setInputUrl(URL inputUrl) {
        this.inputUrl = inputUrl;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }
}
