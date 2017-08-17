/*
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.userspace_scm;

import hudson.model.Run;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

public class UserspaceChangeLogParser extends ChangeLogParser {

    @SuppressWarnings("rawtypes")
    @Override public ChangeLogSet<? extends ChangeLogSet.Entry> parse(Run build, RepositoryBrowser<?> browser, File changelogFile) throws IOException, SAXException {
        List<UserspaceEntry> entries = new ArrayList<>();
        try (InputStream is = new FileInputStream(changelogFile); Reader r = new InputStreamReader(is, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(r)) {
            String line;
            String revision = null;
            String author = null;
            String url = null;
            long timestamp = -1;
            StringBuilder message = new StringBuilder();
            List<UserspaceFile> files = new ArrayList<>();
            while (true) {
                line = br.readLine();
                if (line == null || line.isEmpty()) {
                    if (url != null) {
                        entries.add(new UserspaceEntry(revision, author, url.equals("-") ? null : url, timestamp, message.toString().trim(), new ArrayList<>(files)));
                    } // else we are just at the end
                    if (line == null) {
                        break;
                    } else {
                        revision = null;
                        author = null;
                        url = null;
                        timestamp = -1;
                        message.setLength(0);
                        files.clear();
                    }
                } else if (revision == null) {
                    revision = line;
                } else if (author == null) {
                    author = line;
                } else if (url == null) {
                    url = line;
                } else if (timestamp == -1) {
                    timestamp = Long.parseLong(line);
                } else if (line.startsWith("> ")) {
                    if (message.length() > 0) {
                        message.append('\n');
                    }
                    message.append(line.substring(2));
                } else if (line.startsWith("* ")) {
                    files.add(new UserspaceFile(line.substring(2), EditType.EDIT));
                } else if (line.startsWith("+ ")) {
                    files.add(new UserspaceFile(line.substring(2), EditType.ADD));
                } else if (line.startsWith("- ")) {
                    files.add(new UserspaceFile(line.substring(2), EditType.DELETE));
                } else {
                    throw new IOException("unrecognized: ‘" + line + "’");
                }
            }
        }
        return new UserspaceChangeLogSet(build, browser, entries);
    }

}
