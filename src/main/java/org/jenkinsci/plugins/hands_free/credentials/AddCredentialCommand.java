package org.jenkinsci.plugins.hands_free.credentials;

import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey.DirectEntryPrivateKeySource;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey.PrivateKeySource;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.AbortException;
import hudson.Extension;
import hudson.util.IOUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Adds an extension to the credentials plugin to allow creation of new credentials from the command line
 * @author Rob Johnston <rob.johnston@suncorp.com.au>
 * @since 12/07/14
 */
@Extension
public class AddCredentialCommand extends AbstractCredentialCommand {

    @Override
    public String getShortDescription() {
        return "Adds a new global credential. Username and password is default, set --ssh and put a private key file " +
                "on stdin for an SSH with inline key credential.";
    }

    @Argument(metaVar="DESCRIPTION",index=0,usage="A meaningful description of the credential to add",required=true)
    public String description;

    @Argument(metaVar="USERNAME",index=1,usage="The username that identifies the credential",required=true)
    public String username;

    @Argument(metaVar="PASSWORD",index=2,usage="The password that authenticates the credential")
    public String password = "";

    @Option(name="--ssh", usage="Set to create an SSH username and inline key credential")
    public boolean doSsh = false;

    @Override
    protected int run() throws Exception {
        final CredentialsStoreAction.DomainWrapper wrapper = getDomainWrapper();
        if (!wrapper.getStore().isDomainsModifiable()) {
            throw new AbortException("Domain is read-only");
        }

        wrapper.getStore().checkPermission(CredentialsStoreAction.CREATE);

        Credentials credentials;
        if (doSsh) {
            PrivateKeySource source = new DirectEntryPrivateKeySource(IOUtils.toString(stdin));
            credentials = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, null, username, source, password, description);

        } else {
            credentials = new UsernamePasswordCredentialsImpl(
                    CredentialsScope.GLOBAL, null, description, username, password
            );
        }

        wrapper.getStore().addCredentials(wrapper.getDomain(), credentials);

        return 0;
    }

}
