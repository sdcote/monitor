package coyote.commons.network.http.auth.ntlm;

import javax.naming.AuthenticationException;

import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;

import coyote.commons.Assert;
import coyote.commons.network.http.HttpMessage;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.auth.AuthSchemeBase;
import coyote.commons.network.http.auth.MalformedChallengeException;
import coyote.commons.security.Credentials;


public class NTLMScheme extends AuthSchemeBase {

  enum State {
    UNINITIATED, CHALLENGE_RECEIVED, MSG_TYPE1_GENERATED, MSG_TYPE2_RECEVIED, MSG_TYPE3_GENERATED, FAILED,
  }

  private final NTLMHandler engine;

  private State state;
  private String challenge;




  public NTLMScheme( final NTLMHandler engine ) {
    super();
    Assert.notNull( engine, "NTLM handler" );
    this.engine = engine;
    this.state = State.UNINITIATED;
    this.challenge = null;
  }




  /**
   * @since 4.3
   */
  public NTLMScheme() {
    this( new NTLMHandlerImpl() );
  }




  public String getSchemeName() {
    return "ntlm";
  }




  public String getParameter( final String name ) {
    // String parameters not supported
    return null;
  }




  public String getRealm() {
    // NTLM does not support the concept of an authentication realm
    return null;
  }




  public boolean isConnectionBased() {
    return true;
  }




  @Override
  protected void parseChallenge( final CharArrayBuffer buffer, final int beginIndex, final int endIndex ) throws MalformedChallengeException {
    this.challenge = buffer.substringTrimmed( beginIndex, endIndex );
    if ( this.challenge.length() == 0 ) {
      if ( this.state == State.UNINITIATED ) {
        this.state = State.CHALLENGE_RECEIVED;
      } else {
        this.state = State.FAILED;
      }
    } else {
      if ( this.state.compareTo( State.MSG_TYPE1_GENERATED ) < 0 ) {
        this.state = State.FAILED;
        throw new MalformedChallengeException( "Out of sequence NTLM response message" );
      } else if ( this.state == State.MSG_TYPE1_GENERATED ) {
        this.state = State.MSG_TYPE2_RECEVIED;
      }
    }
  }




  public Header authenticate( final Credentials credentials, final HttpRequest request ) throws AuthenticationException {
    NTCredentials ntcredentials = null;
    try {
      ntcredentials = (NTCredentials)credentials;
    } catch ( final ClassCastException e ) {
      throw new InvalidCredentialsException( "Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName() );
    }
    String response = null;
    if ( this.state == State.FAILED ) {
      throw new AuthenticationException( "NTLM authentication failed" );
    } else if ( this.state == State.CHALLENGE_RECEIVED ) {
      response = this.engine.generateType1Msg( ntcredentials.getDomain(), ntcredentials.getWorkstation() );
      this.state = State.MSG_TYPE1_GENERATED;
    } else if ( this.state == State.MSG_TYPE2_RECEVIED ) {
      response = this.engine.generateType3Msg( ntcredentials.getUserName(), ntcredentials.getPassword(), ntcredentials.getDomain(), ntcredentials.getWorkstation(), this.challenge );
      this.state = State.MSG_TYPE3_GENERATED;
    } else {
      throw new AuthenticationException( "Unexpected state: " + this.state );
    }
    final CharArrayBuffer buffer = new CharArrayBuffer( 32 );
    if ( isProxy() ) {
      buffer.append( AUTH.PROXY_AUTH_RESP );
    } else {
      buffer.append( AUTH.WWW_AUTH_RESP );
    }
    buffer.append( ": NTLM " );
    buffer.append( response );
    return new BufferedHeader( buffer );
  }




  public boolean isComplete() {
    return this.state == State.MSG_TYPE3_GENERATED || this.state == State.FAILED;
  }




  @Override
  public void processChallenge( HttpMessage message ) throws MalformedChallengeException {
    // TODO Auto-generated method stub

  }
}