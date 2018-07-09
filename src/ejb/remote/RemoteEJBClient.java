package ejb.remote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import domain.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import ejb.remote.stateful.CounterBean;
import ejb.remote.stateful.RemoteCounter;
import ejb.remote.stateless.CalculatorBean;
import ejb.remote.stateless.LibraryPersistentBeanRemote;
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
    	
    	showGUI();
    	utiliserLibrairie();
    }
    
    private static void showGUI(){
        System.out.println("**********************");
        System.out.println("Bienvenu à la librairie");
        System.out.println("**********************");
        System.out.print("Options \n1. ajouter un livre\n2. Quitter \nEntrer le choix: ");
    }
    
    /**
     * Definit le JNDI pour la connexion au serveur EJB
     * @return Hastable jndiProperties
     */
    private static Hashtable getJNDI(){
    	final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProperties.put("jboss.naming.client.ejb.context", true);
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
        jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "manager");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "tuyaux");
        return jndiProperties;
    }
    
    /**
     * Utilise un bean stateless pour du traitement de calcule
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws NamingException {
        // Let's lookup the remote stateless calculator
        final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();
        System.out.println("Reception d'un remote stateless calculator pour invocation");
        // invoke on the remote calculator
        int a = 204;
        int b = 340;
        System.out.println("Addition de  " + a + " et " + b + " via le remote stateless calculator déployé sur le serveur");
        int sum = statelessRemoteCalculator.add(a, b);
        System.out.println("Remote calculator a retourné la somme = " + sum);
        if (sum != a + b) {
            throw new RuntimeException("Remote stateless calculator  aretourné une somme incorrecte de " + sum + ", la somme attendue était " + (a + b));
        }
        int num1 = 3434;
        int num2 = 2332;
        System.out.println("Soustraction de " + num2 + " à " + num1 + " via le remote stateless calculator déployé sur le serveur");
        int difference = statelessRemoteCalculator.subtract(num1, num2);
        System.out.println("Remote calculator a retourné la différence = " + difference);
        if (difference != num1 - num2) {
            throw new RuntimeException("Remote stateless calculator a retourné une différence incorrecte " + difference + ", la différence devait être " + (num1 - num2));
        }
    }
 
    /**
     * Utilise un bean stateful pour du traitement de calcule
     *
     * @throws NamingException
     */
    private static void invokeStatefulBean() throws NamingException {
        // Let's lookup the remote stateful Counter
    	final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulCounter();
        System.out.println("Reception d'un remote stateful counter pour invocation");
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
    
    /**
     * Appelle un bean stateless et l'instancie
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
     * Appelle un bean stateful et l'instancie
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
     
     private static void utiliserLibrairie(){
    	BufferedReader brConsoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
           int choice = 1; 
           final Hashtable jndiProperties = getJNDI();
           final Context context = new InitialContext(jndiProperties);
           LibraryPersistentBeanRemote libraryBean =(LibraryPersistentBeanRemote)context.lookup("LibraryPersistentBean/remote");

           while (choice != 2) {
              String bookName;
              showGUI();
              String strChoice = brConsoleReader.readLine();
              choice = Integer.parseInt(strChoice);
              if (choice == 1) {
                 System.out.print("Entrer le nom du livre: ");
                 bookName = brConsoleReader.readLine();
                 Book book = new Book();
                 book.setName(bookName);
                 libraryBean.addBook(book);          
              } else if (choice == 2) {
                 break;
              }
           }

           List<Book> booksList = libraryBean.getBooks();

           System.out.println("Nombre de livres en librairies: " + booksList.size());
           int i = 0;
           for (Book book:booksList) {
              System.out.println((i+1)+". " + book.getName());
              i++;
           }           
        } catch (Exception e) {
           System.out.println(e.getMessage());
           e.printStackTrace();
        }finally {
           try {
              if(brConsoleReader !=null){
                 brConsoleReader.close();
              }
           } catch (IOException ex) {
              System.out.println(ex.getMessage());
              ex.printStackTrace();
           }
        }
     }
}