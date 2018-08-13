import com.sil.npci.iso8583.CBSEncoderDecoder;
import com.sil.npci.iso8583.ISO8583Message;
import com.sil.npci.iso8583.NPCIEncoderDecoder;
import com.sil.npci.iso8583.util.ParseException;
import com.sil.npci.util.ByteHexUtil;

public class Test {

	public static void main(String[] args) throws ParseException {
		ISO8583Message iso8583Message = CBSEncoderDecoder.decode(ByteHexUtil.hexToByte("0200F23AC40128E1901000000000040000001660770800100046760110000000008000000702044135161688101132070207020702601190106800001326071020022217086=260852000000285818310007664SACWE879SACWE879       STATION RD AMET        AMET         RJIN044CASHNET                 4000003560000000000035636572E7B0C2EBBE7012EUROPRO1+00015006002300000281"));
		System.out.println(NPCIEncoderDecoder.log(iso8583Message));
	}
	
	public static final String importKey(final String zmklmk, final String keyzmk) {
		String commandA6 = new StringBuilder().append("0000").append("A6").append("001U").append(zmklmk).append(keyzmk).append("U").toString();
		return commandA6;
	}
	
}
