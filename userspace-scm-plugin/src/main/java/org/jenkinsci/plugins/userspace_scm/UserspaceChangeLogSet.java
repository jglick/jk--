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
import hudson.scm.ChangeLogSet;
import hudson.scm.RepositoryBrowser;
import java.util.Iterator;
import java.util.List;

// TODO implement index and digest views
final class UserspaceChangeLogSet extends ChangeLogSet<UserspaceEntry> {

    private final List<UserspaceEntry> entries;

    UserspaceChangeLogSet(Run<?, ?> build, RepositoryBrowser<?> browser, List<UserspaceEntry> entries) {
        super(build, browser);
        this.entries = entries;
        entries.forEach((e) -> e._setParent(this));
    }
    
    @Override public boolean isEmptySet() {
        return entries.isEmpty();
    }
    
    @Override public Iterator<UserspaceEntry> iterator() {
        return entries.iterator();
    }

    @Override public String toString() {
        return entries.toString();
    }

}
