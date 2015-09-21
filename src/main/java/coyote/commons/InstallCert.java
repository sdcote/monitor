package coyote.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * Class used to add the server's certificate to the KeyStore
 * with your trusted certificates.
 * 
 * Some of you may be familiar with the (not very user friendly) exception 
 * message<pre> 
 * javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: 
 * PKIX path building failed: 
 * sun.security.provider.certpath.SunCertPathBuilderException: unable to find 
 * valid certification path to requested target.</pre>
 * when trying to open an SSL connection to a host using JSSE. What this 
 * usually means is that the server is using a test certificate (possibly 
 * generated using keytool) rather than a certificate from a well known 
 * commercial Certification Authority such as Verisign or GoDaddy. Web browsers 
 * display warning dialogs in this case, but since JSSE cannot assume an 
 * interactive user is present it just throws an exception by default.</p>
 * 
 * <p>Certificate validation is a very important part of SSL security. This 
 * shows a simple way to talk to that host with the test certificate, if you 
 * really want to.</p>
 * 
 * <p>Basically, you want to add the server's certificate to the KeyStore with 
 * your trusted certificates. There are any number of ways to achieve that, but 
 * a simple solution is to compile and run the attached program as {@code java 
 * InstallCert hostname}, for example:<pre>
 * % java InstallCert ecc.fedora.redhat.com
 * Loading KeyStore
 *  /usr/jdk/instances/jdk1.5.0/jre/lib/security/cacerts...
 *  Opening connection to ecc.fedora.redhat.com:443...
 *  Starting SSL handshake...
 *  
 *  sun.security.validator.ValidatorException: PKIX path building failed: 
 *  sun.security.provider.certpath.SunCertPathBuilderException: unable to find 
 *  valid certification path to requested target
 *  
 *  Server sent 2 certificate(s):
 *  
 * 1 Subject CN=ecc.fedora.redhat.com, O=example.com, C=US
 *      Issuer  CN=Certificate Shack, O=example.com, C=US
 *      sha1    2e 7f 76 9b 52 91 09 2e 5d 8f 6b 61 39 2d 5e 06 e4 d8 e9 c7
 *      md5     dd d1 a8 03 d7 6c 4b 11 a7 3d 74 28 89 d0 67 54
 *    
 * 2 Subject CN=Certificate Shack, O=example.com, C=US
 *     Issuer  CN=Certificate Shack, O=example.com, C=US
 *     sha1    fb 58 a7 03 c4 4e 3b 0e e3 2c 40 2f 87 64 13 4d df e1 a1 a6
 *     md5     72 a0 95 43 7e 41 88 18 ae 2f 6d 98 01 2c 89 68
 *     
 * Enter certificate to add to trusted keystore or 'q' to quit: [1]</pre>
 * 
 * What happened was that the program opened a connection to the specified host 
 * and started an SSL handshake. It printed the exception stack trace of the 
 * error that occurred and shows you the certificates used by the server. Now it 
 * prompts you for the certificate you want to add to your trusted KeyStore. 
 * You should only do this if you are sure that this is the certificate of the 
 * trusted host you want to connect to. You may want to check the MD5 and SHA1 
 * certificate fingerprints against a fingerprint generated on the server (e.g. 
 * using keytool) to make sure it is the correct certificate.</p>
 * 
 * <p>If you've changed your mind, enter 'q'. If you really want to add the 
 * certificate, enter '1'. (You could also add a CA certificate by entering a 
 * different certificate, but you usually don't want to do that'). Once you 
 * have made your choice, you can re-run the program again and confirm the 
 * certs have been added.</p>
 * 
 * <p>After you run this program again and verify the certs are working, copy 
 * the generated “jssecacerts” file to your “$JAVA_HOME\jre\lib\security” folder.
 * 
 */
public class InstallCert {

  private static final char[] HEXDIGITS = "0123456789ABCDEF".toCharArray();




  public static void main( String[] args ) throws Exception {
    String host;
    int port;
    char[] passphrase;
    if ( ( args.length == 1 ) || ( args.length == 2 ) ) {
      String[] c = args[0].split( ":" );
      host = c[0];
      port = ( c.length == 1 ) ? 443 : Integer.parseInt( c[1] );
      String p = ( args.length == 1 ) ? "changeit" : args[1];
      passphrase = p.toCharArray();
    } else {
      System.out.println( "Usage: java InstallCert [:port] [passphrase]" );
      return;
    }

    File file = new File( "jssecacerts" );
    if ( file.isFile() == false ) {
      char SEP = File.separatorChar;
      File dir = new File( System.getProperty( "java.home" ) + SEP + "lib" + SEP + "security" );
      file = new File( dir, "jssecacerts" );
      if ( file.isFile() == false ) {
        file = new File( dir, "cacerts" );
      }
    }
    System.out.println( "Loading KeyStore " + file + "..." + file.getAbsolutePath() );
    InputStream in = new FileInputStream( file );
    KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
    ks.load( in, passphrase );
    in.close();

    SSLContext context = SSLContext.getInstance( "TLS" );
    TrustManagerFactory tmf = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
    tmf.init( ks );
    X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
    SavingTrustManager tm = new SavingTrustManager( defaultTrustManager );
    context.init( null, new TrustManager[] { tm }, null );
    SSLSocketFactory factory = context.getSocketFactory();

    System.out.println( "Opening connection to " + host + ":" + port + "..." );
    SSLSocket socket = (SSLSocket)factory.createSocket( host, port );
    socket.setSoTimeout( 10000 );
    try {
      System.out.println( "Starting SSL handshake..." );
      socket.startHandshake();
      socket.close();
      System.out.println();
      System.out.println( "No errors, certificate is already trusted" );
    } catch ( SSLException e ) {
      System.out.println();
      System.out.println( e.getMessage() );
      e.printStackTrace( System.out );
    }

    X509Certificate[] chain = tm.chain;
    if ( chain == null ) {
      System.out.println( "Could not obtain server certificate chain" );
      return;
    }

    BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

    System.out.println();
    System.out.println( "Server sent " + chain.length + " certificate(s):" );
    System.out.println();
    MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );
    MessageDigest md5 = MessageDigest.getInstance( "MD5" );
    for ( int i = 0; i < chain.length; i++ ) {
      X509Certificate cert = chain[i];
      System.out.println( " " + ( i + 1 ) + " Subject " + cert.getSubjectDN() );
      System.out.println( "   Issuer  " + cert.getIssuerDN() );
      sha1.update( cert.getEncoded() );
      System.out.println( "   sha1    " + toHexString( sha1.digest() ) );
      md5.update( cert.getEncoded() );
      System.out.println( "   md5     " + toHexString( md5.digest() ) );
      System.out.println();
    }

    System.out.println( "Enter certificate to add to trusted keystore or 'q' to quit: [1]" );
    String line = reader.readLine().trim();
    int k;
    try {
      k = ( line.length() == 0 ) ? 0 : Integer.parseInt( line ) - 1;
    } catch ( NumberFormatException e ) {
      System.out.println( "KeyStore not changed" );
      return;
    }

    X509Certificate cert = chain[k];
    String alias = host + "-" + ( k + 1 );
    ks.setCertificateEntry( alias, cert );

    OutputStream out = new FileOutputStream( "jssecacerts" );
    ks.store( out, passphrase );
    out.close();

    System.out.println();
    System.out.println( cert );
    System.out.println();
    System.out.println( "Added certificate to keystore 'jssecacerts' using alias '" + alias + "'" );
  }




  private static String toHexString( byte[] bytes ) {
    StringBuilder sb = new StringBuilder( bytes.length * 3 );
    for ( int b : bytes ) {
      b &= 0xff;
      sb.append( HEXDIGITS[b >> 4] );
      sb.append( HEXDIGITS[b & 15] );
      sb.append( ' ' );
    }
    return sb.toString();
  }

  /**
   * Our own version of the trust manager which checks the chain
   */
  private static class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager tm;
    private X509Certificate[] chain;




    SavingTrustManager( X509TrustManager tm ) {
      this.tm = tm;
    }




    public X509Certificate[] getAcceptedIssuers() {
      return null;//throw new UnsupportedOperationException();
    }




    public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
      //throw new UnsupportedOperationException();
    }




    public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
      this.chain = chain;
      tm.checkServerTrusted( chain, authType );
    }
  }
}