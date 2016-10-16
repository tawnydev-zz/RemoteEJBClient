
package org.jboss.as.quickstarts.ejb.remote.client;
 
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Hashtable;
 
import org.jboss.as.quickstarts.ejb.remote.stateless.CalculatorBean;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;
 
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
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {
        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
        jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "manager");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "tuyaux");
        final Context context = new InitialContext(jndiProperties);
        final String moduleName = "EJBRemote";
        final String distinctName = "";
        final String beanName = CalculatorBean.class.getSimpleName();
        final String viewClassName = RemoteCalculator.class.getName();
        
        return (RemoteCalculator) context.lookup("ejb:/" + moduleName + "/" + beanName + "!" + viewClassName);
    }
}