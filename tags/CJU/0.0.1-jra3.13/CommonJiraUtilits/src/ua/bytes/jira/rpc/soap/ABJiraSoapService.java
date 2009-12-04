package ua.bytes.jira.rpc.soap;

import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemoteException;

/**
 *
 * @author Vmganzha
 */
public interface ABJiraSoapService {

    String login(String username, String password) throws RemoteException, RemoteAuthenticationException;

    boolean logout(String token);

    String startReindex(String token) throws RemoteException;

    String[] checkIntegrity(String token) throws RemoteException;
}
