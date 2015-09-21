package coyote.commons.network.http;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import coyote.commons.ByteUtil;
import coyote.commons.StringUtil;


public class BasicAuthTest {

  @Test
  public void testToString() {
    String expected = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
    String test = new BasicAuth( "Aladdin", "open sesame" ).toString();
    //System.out.println( test );
    assertEquals( expected, test );
  }




  @Test
  public void testDecode() {
    String val = "sgAPPNAME:GWpasswd";
    String tst = "c2dBUFBOQU1FOkdXcGFzc3dk";
    byte[] bytes = ByteUtil.fromBase64( tst );
    assertEquals( val, StringUtil.getString( bytes ) );
    //System.out.println( StringUtil.getString( bytes ) );
  }

}
