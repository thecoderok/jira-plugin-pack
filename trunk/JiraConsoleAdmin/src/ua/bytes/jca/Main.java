package ua.bytes.jca;

import bugtracker.rpc.soap.abws_admin.ABJiraSoapService;
import bugtracker.rpc.soap.abws_admin.ABJiraSoapServiceServiceLocator;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.SimpleJSAP;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author Vmganzha
 */
public class Main {

    protected enum Action {
        REINDEX,

        INTEGRITYCHECK,

        NOTFOUND; // the last one

        private static Action toAction(String string) {
            try {
                return valueOf(string);
            } catch (Exception exception) {
                exception.printStackTrace();
                return NOTFOUND;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ServiceException, RemoteException, JSAPException {

         SimpleJSAP jsap = new SimpleJSAP(
            "Jira Console Administration Tool",
            "Do some useful administration tasks in console mode",
            new Parameter[] {
            }
        );


        jsap.registerParameter(new FlaggedOption("server", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 's', "server", "Server URL."));

        FlaggedOption userName = new FlaggedOption("userName")
                                     .setRequired(true)
                                     .setStringParser(JSAP.STRING_PARSER)
                                     .setAllowMultipleDeclarations(false)
                                     .setShortFlag('u')
                                     .setLongFlag("user");

        jsap.registerParameter(userName);

        FlaggedOption password = new FlaggedOption("password")
                                     .setRequired(true)
                                     .setStringParser(JSAP.STRING_PARSER)
                                     .setAllowMultipleDeclarations(false)
                                     .setShortFlag('p')
                                     .setLongFlag("password");

        jsap.registerParameter(password);

        FlaggedOption action = new QualifiedSwitch( "actions", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'a', "actions",
                    "Acton to execute (reindex, integritycheck)." ).setList( true ).setListSeparator( ',' );


        jsap.registerParameter(action);

        JSAPResult config = jsap.parse(args);
      
        if (jsap.messagePrinted()) {
            
            System.err.println("Usage: java "
                                        + Main.class.getName());
            System.err.println("                "
                                + jsap.getUsage());
            System.err.println();
            // show full help as well
            System.err.println(jsap.getHelp());
            System.exit(1);
        }

        ABJiraSoapServiceServiceLocator serviceLocator = new ABJiraSoapServiceServiceLocator();
        serviceLocator.setAbwsAdminEndpointAddress(config.getString("server"));

        Date dt = new Date();
        System.out.println("Running JCAT "+ dt);

        ABJiraSoapService service = serviceLocator.getAbwsAdmin();
        String token = service.login(config.getString("userName"),config.getString("password"));
        String []actions = config.getStringArray("actions");
        for (int i=0;i<actions.length; ++i)
        {
            switch (Action.toAction(actions[i].toUpperCase())){
                case REINDEX: System.out.println("Reindex answer="+service.startReindex(token));
                               break;
                case INTEGRITYCHECK:
                    String[] result = service.checkIntegrity(token);
                    System.out.println("IntegrityCheck answer: ");
                    for (int j=0; j<result.length; ++j)
                        System.out.println(result[j]);
                    break;
                case NOTFOUND:
                    System.err.println("Usage: java "
                                        + Main.class.getName());
                    System.err.println("                "
                                        + jsap.getUsage());
                    System.err.println();
                    // show full help as well
                    System.err.println(jsap.getHelp());
                    System.exit(1);
            }
        }
        System.out.println("===End===");
    }

}
