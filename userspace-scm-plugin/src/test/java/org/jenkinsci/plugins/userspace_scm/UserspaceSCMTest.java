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
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import hudson.scm.PollingResult;
import hudson.util.StreamTaskListener;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import static org.hamcrest.Matchers.*;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

public class UserspaceSCMTest {

    @ClassRule public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule public JenkinsRule r = new JenkinsRule();

    @BeforeClass public static void buildTestImage() throws Exception {
        StreamTaskListener stdout = StreamTaskListener.fromStdout();
        Assume.assumeThat(new Launcher.LocalLauncher(stdout).launch().cmds("docker", "build", "-t", "userspace-scm-test", new File(UserspaceSCMTest.class.getResource("/test-image/Dockerfile").toURI()).getParent()).stdout(stdout).join(), is(0));
    }

    private WorkflowJob p;
    private FilePath ws;
    private String how;

    @Before public void projectWithRepo() throws Exception {
        p = r.createProject(WorkflowJob.class, "p");
        ws = r.jenkins.getWorkspaceFor(p);
        // cf. run.sh explanation:
        repo("trees/1/f", "one");
        repo("messages/1", "init");
        repo("trees/2/f", "two");
        repo("messages/2", "modified");
        repo("trees/3/f", "three");
        repo("trees/3/f2", "more");
        repo("messages/3", "modified again");
        repo("trees/4/f", "three");
        repo("trees/4/f2", "more");
        repo("trees/4/.stuff", "whatever");
        repo("messages/4", "irrelevant");
        repo("heads/trunk", "2");
        how = "container(image: 'userspace-scm-test', config: 'repo')";
    }
    private void repo(String path, String text) throws Exception {
        ws.child("repo/" + path).write(text, null);
    }

    @Test public void checkout() throws Exception {
        p.setDefinition(new CpsFlowDefinition("node {checkout userspace(how: " + how + ", head: 'trunk')}", true));
        r.buildAndAssertSuccess(p);
        assertEquals("two", ws.child("wc/f").readToString());
        p.setDefinition(new CpsFlowDefinition("node {checkout userspace(how: " + how + ", head: 'trunk', revision: '1')}", true));
        r.buildAndAssertSuccess(p);
        assertEquals("one", ws.child("wc/f").readToString());
    }

    @Test public void polling() throws Exception {
        p.setDefinition(new CpsFlowDefinition("node {checkout userspace(how: " + how + ", head: 'trunk', requiresWorkspaceForPolling: true)}", true));
        r.buildAndAssertSuccess(p);
        assertEquals("two", ws.child("wc/f").readToString());
        repo("heads/trunk", "3");
        TaskListener l = StreamTaskListener.fromStdout();
        assertEquals(PollingResult.Change.SIGNIFICANT, p.poll(l).change);
        assertEquals(PollingResult.Change.NONE, p.poll(l).change);
        repo("heads/trunk", "4");
        assertEquals(PollingResult.Change.INSIGNIFICANT, p.poll(l).change);
        assertEquals(PollingResult.Change.NONE, p.poll(l).change);
    }

    @Test public void changelog() throws Exception {
        p.setDefinition(new CpsFlowDefinition("node {checkout userspace(how: " + how + ", head: 'trunk')}", true));
        assertEquals(Collections.emptyList(), r.buildAndAssertSuccess(p).getChangeSets());
        repo("heads/trunk", "4");
        List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeSets = r.buildAndAssertSuccess(p).getChangeSets();
        assertEquals(1, changeSets.size());
        Iterator<? extends ChangeLogSet.Entry> entriesIt = changeSets.get(0).iterator();
        assertTrue(entriesIt.hasNext());
        ChangeLogSet.Entry entry = entriesIt.next();
        assertEquals("3", entry.getCommitId());
        assertEquals("modified again", entry.getMsg());
        assertEquals(User.get("dev"), entry.getAuthor());
        Set<ChangeLogSet.AffectedFile> affectedFiles = new TreeSet<>(Comparator.comparing(ChangeLogSet.AffectedFile::getPath));
        affectedFiles.addAll(entry.getAffectedFiles());
        Iterator<ChangeLogSet.AffectedFile> affectedFilesIt = affectedFiles.iterator();
        assertTrue(affectedFilesIt.hasNext());
        ChangeLogSet.AffectedFile file = affectedFilesIt.next();
        assertEquals("f", file.getPath());
        assertEquals(EditType.EDIT, file.getEditType());
        assertTrue(affectedFilesIt.hasNext());
        file = affectedFilesIt.next();
        assertEquals("f2", file.getPath());
        assertEquals(EditType.ADD, file.getEditType());
        assertFalse(affectedFilesIt.hasNext());
        assertTrue(entriesIt.hasNext());
        entry = entriesIt.next();
        assertEquals("4", entry.getCommitId());
        assertEquals("irrelevant", entry.getMsg());
        affectedFiles.clear();
        affectedFiles.addAll(entry.getAffectedFiles());
        affectedFilesIt = affectedFiles.iterator();
        assertTrue(affectedFilesIt.hasNext());
        file = affectedFilesIt.next();
        assertEquals(".stuff", file.getPath());
        assertEquals(EditType.ADD, file.getEditType());
        assertFalse(affectedFilesIt.hasNext());
        assertFalse(entriesIt.hasNext());
    }

    // TODO operations on agents
    // TODO implement webhooks

}
