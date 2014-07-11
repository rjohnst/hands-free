package org.jenkinsci.plugins.hands_free;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsSelectHelper;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.AbortException;
import hudson.Extension;
import hudson.cli.CLICommand;
import org.kohsuke.args4j.Argument;

import java.io.PrintStream;

/**
 * Adds an extension to the credentials plugin to allow creation of new credentials from the command line
 * @author Rob Johnston <rob.johnston@suncorp.com.au>
 * @since 12/07/14
 */
@Extension
public class AddCredentialCommand extends CLICommand {

    @Override
    public String getShortDescription() {
        return "Adds a new global username and password credential";
    }

    @Argument(metaVar="USERNAME",index=0,usage="The username that identifies the credential",required=true)
    public String username;

    @Argument(metaVar="PASSWORD",index=1,usage="The password that authenticates the credential",required=true)
    public String password;

    @Argument(metaVar="DESCRIPTION",index=2,usage="A meaningful description of the credential to add",required=true)
    public String description;

    @Override
    protected int run() throws Exception {
        final CredentialsStoreAction.DomainWrapper wrapper = getDomainWrapper();
        if (!wrapper.getStore().isDomainsModifiable()) {
            throw new AbortException("Domain is read-only");
        }

        wrapper.getStore().checkPermission(CredentialsStoreAction.CREATE);

        Credentials credentials = new UsernamePasswordCredentialsImpl(
                CredentialsScope.GLOBAL, null, description, username, password
        );

        wrapper.getStore().addCredentials(wrapper.getDomain(), credentials);

        return 0;
    }

    private CredentialsStoreAction.DomainWrapper getDomainWrapper() {
        // reusing CredentialsSelectHelper to save implementing getWrapper myself
        CredentialsSelectHelper helper = new CredentialsSelectHelper();
        return helper.getWrapper();
    }

    protected void printUsageSummary(PrintStream stderr) {
        stderr.println(
            "Adds a new global credential, using a username and password for authentication.\n" +
            "Calls to this command should take the form:\n" +
            "\tadd-credential username password \"some description\""
        );
    }
}
