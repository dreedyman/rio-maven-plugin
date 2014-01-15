/*
 * Copyright to the original author or authors.
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
package org.rioproject.tools.maven;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Starts the Rio UI
 *
 * @goal ui
 *
 * @description Starts the Rio UI
 *
 */
public class UIMojo extends AbstractRioMojo {
    /**
     * Optional group name to set. Sets the org.rioproject.groups system property
     * @parameter
     * expression="${rio.groups}"
     */
    private String group;

    public void execute() throws MojoExecutionException {
        if(!project.isExecutionRoot()) {
            getLog().debug("Project not the execution root, do not execute");
            return;
        }
        String options = "";
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Mac"))
            options = "-Xdock:name=Rio";
        String rioHome = getRioHome();
        String rioLib = rioHome+"lib"+File.separator;
        String groups = "";
        if(group!=null)
            groups="-Dorg.rioproject.groups="+group;
        File rioLibDir = new File(rioLib);
        String rioUI = null;
        for(String jar : rioLibDir.list()) {
            if(jar.startsWith("rio-ui")) {
                rioUI = jar;
                break;
            }
        }
        String policy = String.format("-Djava.security.policy=%spolicy%spolicy.all", rioHome, File.separator);
        if(rioUI==null) {
            throw new MojoExecutionException("Unable to locate rio-ui JAR file");
        }
        String command = String.format("java %s -DRIO_HOME=%s %s %s -jar %s%s", policy, rioHome, options, groups, rioLib, rioUI);
        getLog().debug(command);
        ExecHelper.doExec(command, false);
    }
}
