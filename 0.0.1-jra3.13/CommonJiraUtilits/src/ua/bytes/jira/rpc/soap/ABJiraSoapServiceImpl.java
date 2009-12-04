package ua.bytes.jira.rpc.soap;

import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.jira.appconsistency.integrity.IntegrityCheckManager;
import com.atlassian.jira.appconsistency.integrity.IntegrityCheckManagerImpl;
import com.atlassian.jira.appconsistency.integrity.IntegrityChecker;
import com.atlassian.jira.appconsistency.integrity.check.AbstractAmendment;
import com.atlassian.jira.appconsistency.integrity.check.Check;
import com.atlassian.jira.appconsistency.integrity.integritycheck.AbstractIntegrityCheck;
import com.atlassian.jira.ofbiz.DefaultOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.rpc.auth.TokenManager;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemoteException;

import com.atlassian.jira.web.bean.I18nBean;
import com.opensymphony.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ua.bytes.tasksupport.ReindexThread;

/**
 *
 * @author Vmganzha
 */
public class ABJiraSoapServiceImpl implements ABJiraSoapService {

    private static final Logger log = Logger.getLogger(ABJiraSoapServiceImpl.class);

    private TokenManager tokenManager;
    private volatile boolean reindexing = false;

    public ABJiraSoapServiceImpl(TokenManager tokenManager)
    {
        this.tokenManager = tokenManager;
    }

    public String login(String username, String password) throws RemoteException, RemoteAuthenticationException {
        return tokenManager.login(username, password);
    }

    public boolean logout(String token) {
        return tokenManager.logout(token);
    }

    public String startReindex(String token) throws RemoteException {

        User user = tokenManager.retrieveUserNoPermissionCheck(token);
        if (isReindexing())
            return "ReindexInProgress";
        setReindexing(true);
        new ReindexThread(this).start();

        log.info("Reindexing runned");
        return "Started";
    }

    /**
     * @return the reindexing
     */
    public boolean isReindexing() {
        return reindexing;
    }

    /**
     * @param reindexing the reindexing to set
     */
    public synchronized void setReindexing(boolean reindexing) {
        this.reindexing = reindexing;
    }

    public String[] checkIntegrity(String token) throws RemoteException {
        
        User user = tokenManager.retrieveUserNoPermissionCheck(token);

        ArrayList<String> answer = new ArrayList<String>();

        OfBizDelegator delegator = new DefaultOfBizDelegator(CoreFactory.getGenericDelegator());

        IntegrityCheckManager integrityCheckManager = new IntegrityCheckManagerImpl(delegator,new I18nBean());
        IntegrityChecker integrityChecker = new  IntegrityChecker(integrityCheckManager);

        List<AbstractIntegrityCheck> intChecks = integrityCheckManager.getIntegrityChecks();

        List<Check> checks = new ArrayList<Check>();

        for (AbstractIntegrityCheck i: intChecks)
        {
            checks.add(integrityCheckManager.getCheck(i.getId()));
        }
        Map results = null;
        try {
            results = integrityChecker.preview(checks);
            answer.add("\tFounded problems:");
            for (Object i : results.keySet())
            {
                ArrayList item = (ArrayList) results.get(i);
                if (item != null && item.size() > 0)
                {
                    for (Object sub: item)
                    {
                        AbstractAmendment amendment = (AbstractAmendment) sub;
                        answer.add("\t\t"+amendment.getMessage());
                    }
                }

            }
            answer.add("\tResolved problems:");
            results = integrityChecker.correct(checks);
            
            for (Object i : results.keySet())
            {
                ArrayList item = (ArrayList) results.get(i);
                if (item != null && item.size() > 0)
                {
                    for (Object sub: item)
                    {
                        AbstractAmendment amendment = (AbstractAmendment) sub;
                        answer.add("\t\t"+amendment.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String[] array = new String[answer.size()];
        return answer.toArray(array);
    }
}
