package ejb.remote.stateless;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Hashtable;

import ejb.remote.stateful.CounterBean;
import ejb.remote.stateful.RemoteCounter;
import ejb.remote.stateless.CalculatorBean;
import ejb.remote.stateless.RemoteCalculator;

/**
 * A sample program which acts a remote client for a EJB deployed on AS7 server.
 * This program shows how to lookup a stateless bean via JNDI and then invoke on it
 *
 */
public class RemoteEJBClient {
	// Define a static logger variable so that it references the Logger instance named "RemoteEJBClient".
 
    public static void main(String[] args) throws Exception {    	
        // Invoke a stateless bean
    	invokeStatelessBean();
    	invokeStatefulBean();
    	invokeStatefulBean();
    }
 
    /**
     * Looks up a stateless bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws NamingException {
        // Let's lookup the remote stateless calculator
    	System.out.println("test");
        final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();
        System.out.println("Obtained a remote stateless calculator for invocation");
        // invoke on the remote calculator
        int a = 204;
        int b = 340;
        System.out.println("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
        int sum = statelessRemoteCalculator.add(a, b);
        System.out.println("Remote calculator returned sum = " + sum);
        if (sum != a + b) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was " + (a + b));
        }
        int num1 = 3434;
        int num2 = 2332;
        System.out.println("Subtracting " + num2 + " from " + num1 + " via the remote stateless calculator deployed on the server");
        int difference = statelessRemoteCalculator.subtract(num1, num2);
        System.out.println("Remote calculator returned difference = " + difference);
        if (difference != num1 - num2) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference + " ,expected difference was " + (num1 - num2));
        }
    }
 
    /**
     * Looks up a stateful bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatefulBean() throws NamingException {
        // Let's lookup the remote stateful Counter
    	final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulCounter();
        System.out.println("Obtained a remote stateful counter for invocation");
        System.out.println("Nouvelle instance de counter n°"+statefulRemoteCounter.getNbInstance());
        for (int i=0;i<5;i++){
        	System.out.println("i="+i);
        	System.out.println("index="+statefulRemoteCounter.getCount());
        	statefulRemoteCounter.increment();
        }
        System.out.println("Dernière valeur de index="+statefulRemoteCounter.getCount());
        for (int i=5; i>0; i--){
        	System.out.println("i="+i);
        	System.out.println("index="+statefulRemoteCounter.getCount());
        	statefulRemoteCounter.decrement();
        }
        System.out.println("Dernière valeur de index="+statefulRemoteCounter.getCount());
    }
    
    private static Hashtable getJNDI(){
    	final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
        jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "manager");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "tuyaux");
        return jndiProperties;
    }
    /**
     * Looks up and remotes stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {
        final Hashtable jndiProperties = getJNDI();
        final Context context = new InitialContext(jndiProperties);
        final String moduleName = "EJBRemote";
        final String distinctName = "";
        final String beanName = CalculatorBean.class.getSimpleName();
        final String viewClassName = RemoteCalculator.class.getName();
        
        return (RemoteCalculator) context.lookup("ejb:/" + moduleName + "/" + beanName + "!" + viewClassName);
    }
    
    /**
     * Looks up and remotes stateful counter bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCounter lookupRemoteStatefulCounter() throws NamingException {
    	final Hashtable jndiProperties = getJNDI();
        final Context context = new InitialContext(jndiProperties);
        final String moduleName = "EJBRemote";
        final String distinctName = "";
        final String beanName = CounterBean.class.getSimpleName();
        final String viewClassName = RemoteCounter.class.getName();
        
        return (RemoteCounter) context.lookup("ejb:/" + moduleName + "/" + beanName + "!" + viewClassName +"?stateful");
    }
}