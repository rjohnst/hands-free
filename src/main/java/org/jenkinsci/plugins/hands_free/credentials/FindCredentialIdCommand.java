package org.jenkinsci.plugins.hands_free.credentials;

import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import hudson.Extension;
import org.jenkinsci.plugins.hands_free.credentials.AbstractCredentialCommand;
import org.kohsuke.args4j.Argument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adds an extension to locate the ID of a credential whose description matches the supplied regex
 * @author Rob Johnston <rob.johnston@suncorp.com.au>
 * @since 14/07/14
 */
@Extension
public class FindCredentialIdCommand extends AbstractCredentialCommand {

    @Override
    public String getShortDescription() {
        return "Locate the ID of a credential whose description matches the supplied regex";
    }

    @Argument(metaVar="REGEX",usage="The regex to apply",required=true)
    public String regex;

    @Override
    protected int run() throws Exception {
        final CredentialsStoreAction.DomainWrapper wrapper = getDomainWrapper();
        Set<Map.Entry<String, CredentialsStoreAction.CredentialsWrapper>> creds = wrapper.getCredentials().entrySet();
        List<Map.Entry<String, CredentialsStoreAction.CredentialsWrapper>> matches = new ArrayList<Map.Entry<String, CredentialsStoreAction.CredentialsWrapper>>();

        for (Map.Entry<String, CredentialsStoreAction.CredentialsWrapper> cred : creds) {
            if (cred.getValue().getDisplayName().matches(regex)) {
                matches.add(cred);
            }
        }

        if (matches.size() == 0) {
            stderr.println("No credentials matched the regex!");
            return -1;

        } else if (matches.size() == 1) {
            stdout.println(matches.get(0).getKey());
            return 0;

        } else {
            stderr.println("Multiple matches found!");
            for (Map.Entry<String, CredentialsStoreAction.CredentialsWrapper> cred : matches) {
                stderr.println("\t" + cred.getValue().getDisplayName());
            }
            return matches.size();
        }
    }

//    @Override
//    protected void printUsageSummary(PrintStream stderr) {
//        stderr.println(
//                "Adds a new global credential, using a username and password for authentication.\n" +
//                "Calls to this command should take the form:\n" +
//                "\tadd-credential username password \"some description\""
//        );
//    }
}
