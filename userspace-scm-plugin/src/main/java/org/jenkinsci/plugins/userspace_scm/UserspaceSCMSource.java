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

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import java.io.IOException;
import java.util.Set;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadEvent;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import jenkins.scm.api.SCMSourceDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

// TODO add an SCMNavigator implementation

public class UserspaceSCMSource extends SCMSource {

    public final ContainerConfig how;

    @DataBoundConstructor public UserspaceSCMSource(ContainerConfig how) {
        this.how = how;
    }

    @Override protected void retrieve(SCMSourceCriteria criteria, SCMHeadObserver observer, SCMHeadEvent<?> event, TaskListener listener) throws IOException, InterruptedException {
        // TODO
    }

    @Override public SCM build(SCMHead head, SCMRevision revision) {
        UserspaceSCM scm = new UserspaceSCM(how, head.getName());
        scm.revision = revision.toString();
        return scm;
    }

    // TODO createProbe

    @Override public SCMRevision getTrustedRevision(SCMRevision revision, TaskListener listener) throws IOException, InterruptedException {
        return super.getTrustedRevision(revision, listener); // TODO
    }

    // TODO retrieveActions overloads

    @Override protected Set<String> retrieveRevisions(TaskListener listener) throws IOException, InterruptedException {
        return super.retrieveRevisions(listener); // TODO
    }
    
    @Override protected SCMRevision retrieve(String thingName, TaskListener listener) throws IOException, InterruptedException {
        return super.retrieve(thingName, listener); // TODO
    }

    @Symbol("userspace")
    @Extension public static class DescriptorImpl extends SCMSourceDescriptor {

        @Override public String getDisplayName() {
            return "Userspace";
        }

    }

}
