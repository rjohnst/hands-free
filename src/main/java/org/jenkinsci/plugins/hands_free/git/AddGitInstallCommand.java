package org.jenkinsci.plugins.hands_free.git;

import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.plugins.git.GitTool;
import hudson.plugins.git.GitTool.DescriptorImpl;
import hudson.security.Permission;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Rob Johnston  <rob.johnston@suncorp.com.au>
 * @since 21/07/14
 */
@Extension
public class AddGitInstallCommand extends CLICommand {

    @Override
    public String getShortDescription() {
        return "Adds a new git installation to any existing ones. If the -r flag is set, it will replace any existing ones";
    }

    @Argument(metaVar="NAME",index=0,usage="The name that identifies the installation",required=true)
    public String name;

    @Argument(metaVar="HOME",index=1,usage="The full path to the git installation",required=true)
    public String home;

    @Option(name="-r", usage="Set to replace all existing installations with the specified one")
    public boolean replace = false;

    @Override
    protected int run() throws Exception {
        Jenkins jenkins = Jenkins.getInstance();
        jenkins.checkPermission(Permission.CONFIGURE);

        DescriptorImpl descriptor = (DescriptorImpl) jenkins.getDescriptor(GitTool.class);
        GitTool[] installations = descriptor.getInstallations();

        GitTool newInstallation = new GitTool(name, home, Collections.<ToolProperty<?>>emptyList());

        if (installations == null || installations.length == 0 || replace) {
            // add the given installation as the only installation
            descriptor.setInstallations(newInstallation);

        } else {
            // add the given installation to the current installations
            GitTool[] installs = Arrays.copyOf(installations, installations.length + 1);
            installs[installations.length] = newInstallation;
            descriptor.setInstallations(installs);
        }

        descriptor.save();

        return 0;
    }
}
