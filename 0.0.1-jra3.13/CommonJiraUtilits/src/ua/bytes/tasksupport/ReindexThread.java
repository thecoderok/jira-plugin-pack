package ua.bytes.tasksupport;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.index.IndexException;
import ua.bytes.jira.rpc.soap.ABJiraSoapServiceImpl;

/**
 *
 * @author Vmganzha
 */
public class ReindexThread extends Thread{

    private ABJiraSoapServiceImpl abJiraSoapServiceImpl;

    public ReindexThread(ABJiraSoapServiceImpl abJiraSoapServiceImpl)
    {
        this.abJiraSoapServiceImpl = abJiraSoapServiceImpl;
    }

    @Override
    public void run(){
        try {
            ComponentManager.getInstance().getIndexManager().reIndexAll();
        } catch (IndexException ex) {
            ex.printStackTrace();
        }
        finally
        {
            abJiraSoapServiceImpl.setReindexing(false);
        }
    }


}
