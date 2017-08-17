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

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class ContainerConfig extends AbstractDescribableImpl<ContainerConfig> {

    public final String image;
    public final String config;
    // TODO volumes, etc.

    @DataBoundConstructor public ContainerConfig(String image, String config) {
        this.image = image; // TODO sanitize
        this.config = config;
    }

    // TODO switch to JSON for I/O
    public String run(@Nonnull Launcher launcher, @CheckForNull FilePath workspace, @Nonnull TaskListener listener, String... envs) throws IOException, InterruptedException {
        ByteArrayOutputStream userId = new ByteArrayOutputStream();
        launcher.launch().cmds("id", "-u").quiet(true).stdout(userId).start().joinWithTimeout(15, TimeUnit.SECONDS, listener);
        ByteArrayOutputStream groupId = new ByteArrayOutputStream();
        launcher.launch().cmds("id", "-g").quiet(true).stdout(groupId).start().joinWithTimeout(15, TimeUnit.SECONDS, listener);
        String charsetName = Charset.defaultCharset().name();
        String ug = String.format("%s:%s", userId.toString(charsetName).trim(), groupId.toString(charsetName).trim());
        List<String> cmds = new ArrayList<>(Arrays.asList("docker", "run", "--rm", "--user", ug, "--env", "CONFIG=" + config));
        for (String env : envs) {
            if (env.endsWith("=null")) {
                continue;
            }
            cmds.add("--env");
            cmds.add(env);
        }
        if (workspace != null) {
            workspace.mkdirs();
            cmds.add("--volume");
            cmds.add(workspace + ":/ws");
        }
        cmds.add(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (launcher.launch().cmds(cmds).stdout(baos).stderr(listener.getLogger()).start().joinWithTimeout(1, TimeUnit.HOURS, listener) != 0) {
            throw new AbortException("Command failed");
        }
        return baos.toString("UTF-8");
    }

    @Symbol("container")
    @Extension public static class DescriptorImpl extends Descriptor<ContainerConfig> {}

}
