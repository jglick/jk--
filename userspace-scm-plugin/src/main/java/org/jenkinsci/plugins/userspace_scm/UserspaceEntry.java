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

import hudson.model.User;
import hudson.scm.ChangeLogSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class UserspaceEntry extends ChangeLogSet.Entry {

    private final String revision;
    private final String author;
    private final long timestamp;
    private final String message;
    private final List<UserspaceFile> files;

    UserspaceEntry(String revision, String author, long timestamp, String message, List<UserspaceFile> files) {
        this.revision = revision;
        this.author = author;
        this.timestamp = timestamp;
        this.message = message;
        this.files = files;
    }

    void _setParent(UserspaceChangeLogSet parent) {
        setParent(parent);
    }
    
    @Override public String getCommitId() {
        return revision;
    }
    
    @Override public User getAuthor() {
        return User.get(author); // TODO perhaps look up by email address, etc.
    }
    
    @Override public long getTimestamp() {
        return timestamp;
    }
    
    @Override public String getMsg() {
        return message;
    }
    
    @Override public Collection<UserspaceFile> getAffectedFiles() {
        return files;
    }
    
    @Override public Collection<String> getAffectedPaths() {
        return files.stream().map(UserspaceFile::getPath).collect(Collectors.toList());
    }

    @Override public String toString() {
        return "UserspaceEntry{" + "revision=" + revision + ", author=" + author + ", timestamp=" + timestamp + ", message=" + message + ", files=" + files + '}';
    }

}
