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
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't load PDF", e);
        }
    }

    private void extractChapter() throws MojoExecutionException {
        PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();
        recurseExtractChapter(root.getFirstChild());
    }

    private void recurseExtractChapter(PDOutlineItem item) throws MojoExecutionException {
        while (item != null) {
            if (item.getTitle().equals(chapter)) {
                try {
                    PDPage page = item.findDestinationPage(doc);
                    PDPage page2 = item.getNextSibling().findDestinationPage(doc);
                    List<PDPage> allPages = doc.getDocumentCatalog().getAllPages();
                    int start = allPages.indexOf(page) + 1;
                    int end = allPages.indexOf(page2);
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

    private void extractPageRange(int start, int end) throws MojoExecutionException {
        try {
            PageExtractor extractor = new PageExtractor(doc, start, end);
            PDDocument extracted = extractor.extract();
            outFile.getParentFile().mkdirs();
            FileOutputStream outputStream = new FileOutputStream(outFile);
            extracted.save(outputStream);
            outputStream.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't extract pages from PDF", e);
        } catch (COSVisitorException e) {
            throw new MojoExecutionException("Couldn't extract pages from PDF", e);
        }
    }

    private int getEndPage() {
        return Integer.valueOf(pageRange.split("-")[1]);
    }

    private int getStartPage() {
        return Integer.valueOf(pageRange.split("-")[0]);
    }
}
