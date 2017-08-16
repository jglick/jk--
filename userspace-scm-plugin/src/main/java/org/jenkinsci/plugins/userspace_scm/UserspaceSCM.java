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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Job;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.kohsuke.stapler.DataBoundSetter;

public class UserspaceSCM extends SCM {

    public final String image;
    public final String config;
    public final String head;
    @DataBoundSetter public String rev;

    public UserspaceSCM(String image, String config, String head) {
        this.image = image;
        this.config = config;
        this.head = head;
    }

    @Override public void checkout(Run<?, ?> build, Launcher launcher, FilePath workspace, TaskListener listener, File changelogFile, SCMRevisionState baseline) throws IOException, InterruptedException {
        // TODO
    }

    @Override public SCMRevisionState calcRevisionsFromBuild(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        return null; // TODO
    }

    @Override public PollingResult compareRemoteRevisionWith(Job<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
        return PollingResult.NO_CHANGES; // TODO
    }

    @Override public ChangeLogParser createChangeLogParser() {
        return new UserspaceChangeLogParser();
    }

    @Override public RepositoryBrowser<?> guessBrowser() {
        return super.guessBrowser(); // TODO
    }

    @Override public void buildEnvironment(Run<?, ?> build, Map<String, String> env) {
        super.buildEnvironment(build, env); // TODO
    }

    @Override public boolean processWorkspaceBeforeDeletion(Job<?, ?> project, FilePath workspace, Node node) throws IOException, InterruptedException {
        return super.processWorkspaceBeforeDeletion(project, workspace, node); // TODO
    }

    @Override public boolean requiresWorkspaceForPolling() {
        return false;
    }

    @Override public String getType() {
        return "userspace:" + image;
    }

    @Override public String getKey() {
        return "userspace:" + image + ":" + config;
    }

}
