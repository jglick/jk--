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
import hudson.util.StreamTaskListener;
import java.io.File;
import static org.hamcrest.Matchers.*;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
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

    @Test public void checkout() throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("node {checkout userspace(how: container(image: 'userspace-scm-test', config: 'mycfg'), head: 'trunk')}", true));
        r.buildAndAssertSuccess(p);
        FilePath trunk = r.jenkins.getWorkspaceFor(p).child("trunk");
        assertTrue(trunk.exists());
        assertEquals("mycfg", trunk.readToString());
    }

    // TODO operations on agents
    // TODO changelog
    // TODO polling

}
