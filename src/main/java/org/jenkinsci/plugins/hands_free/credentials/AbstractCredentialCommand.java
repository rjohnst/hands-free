package org.jenkinsci.plugins.hands_free.credentials;

import com.cloudbees.plugins.credentials.CredentialsSelectHelper;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import hudson.cli.CLICommand;

/**
 * Base class for all commands that interact with credentials
 * @author Rob Johnston  <rob.johnston@suncorp.com.au>
 * @since 15/07/14
 */
public abstract class AbstractCredentialCommand extends CLICommand {

    protected CredentialsStoreAction.DomainWrapper getDomainWrapper() {
        // reusing CredentialsSelectHelper to save implementing getWrapper myself
        CredentialsSelectHelper helper = new CredentialsSelectHelper();
        return helper.getWrapper();
    }
}
