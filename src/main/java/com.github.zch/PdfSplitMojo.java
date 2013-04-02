package com.github.zch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
public class PdfSplitMojo extends AbstractMojo
{
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
    @Parameter(property = "pageRange", required = true)
    private String pageRange;

    public void execute() throws MojoExecutionException {
        try {
            Document document = new Document();
            outFile.getParentFile().mkdirs();
            FileOutputStream outputStream = new FileOutputStream(outFile);
            PdfSmartCopy copy = new PdfSmartCopy(document, outputStream);
            PdfReader reader = new PdfReader(inputUrl);
            document.open();
            for (int page = getStartPage(); page <= getEndPage(); page++) {
                copy.addPage(copy.getImportedPage(reader, page));
            }
            document.close();
            copy.close();
            outputStream.close();
        } catch (DocumentException e) {
            getLog().error("Could not split document");
            throw new MojoExecutionException("Could not split document", e);
        } catch (FileNotFoundException e) {
            getLog().error("Could not split document");
            throw new MojoExecutionException("Could not split document", e);
        } catch (IOException e) {
            getLog().error("Could not split document");
            throw new MojoExecutionException("Could not split document", e);
        }
    }

    private int getEndPage() {
        return Integer.valueOf(pageRange.split("-")[1]);
    }

    private int getStartPage() {
        return Integer.valueOf(pageRange.split("-")[0]);
    }
}
