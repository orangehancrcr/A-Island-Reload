/* 这是用于计算饼干的类.
 * 用法示例:
 *	GetCookies example;
 * 	String cookie = example.getCookies(String args);
 * @Author: NavelOrange
 */

import java.util.*;
import java.awt.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang.StringUtils; //第三方的

public class GetCookies
{
	public String getCookies(String args)
	{
		return RandCookie(Dec2Alphabet(Hex2Dec(SHABuilder(args))));
	}

	/* 这是用来加密字符串的函数.
	 * para: arg ---- a string
	 * output: scale 16 hashed arg.
	 */

	private String SHABuilder(String args)
        {
                String strResult = null;
                if(args != null && args.length() > 0)
                {   
                        try 
                        {   
                                MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                                messageDigest.update(args.getBytes());
                                byte[] byteBuffer = messageDigest.digest();
                                StringBuffer strHexString = new StringBuffer();
                                for (int i = 0; i < byteBuffer.length; i++) 
                                {   
                                        String hex = Integer.toHexString(0xff & byteBuffer[i]);
                                        if (hex.length() == 1)
                                        {   
                                                strHexString.append('0');
                                        }   
                                        strHexString.append(hex);
                                }   
                                strResult = strHexString.toString();
                        }   
                        catch (NoSuchAlgorithmException e)
                        {   
                                e.printStackTrace();
                        }   
                }   
                return strResult;
	}

	/* 用BigInteger类型存储转换后的十进制数字.
	 * para: args ---- a string
	 * output: arg turned to Dec BigInteger.
	 */

	private BigInteger Hex2Dec(String args)
	{
		return new BigInteger(args, 16);
	}

	/* 转换成62进制文字.
	 * para: num ---- a biginteger
	 * output: a scale-62-string.
	 */

	public String Dec2Alphabet(BigInteger num) 
	{
        	char[] depository = 
		{
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		 	'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
		        'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
		        'z', 'x', 'c', 'v', 'b', 'n', 'm',
		        'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
		        'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
		        'Z', 'X', 'C', 'V', 'B', 'N', 'M'
		}; //Init num circ space
		BigInteger bint62 = new BigInteger(String.valueOf(62)); // bint62 ---- devisor
		StringBuffer strp = new StringBuffer();
		while(true)
		{
			BigInteger remainder = new BigInteger(String.valueOf(0)); // define minor
			remainder = num.mod(bint62);
			strp.append(depository[remainder.intValue()]);//According to each minor, catch a character
			num = num.divide(bint62);
			if(num.intValue() == 0)
			{
				break;
			}
		}
		strp.reverse(); //Since our devision is from left to right, we'd reverse it.
		return strp.toString();
    	}

	/* 随机取7位.
	 * para: arg ---- a 62-scale-string
	 * output: a possible "cookie".
	 */

	public String RandCookie(String args)
	{
		String cookie = "";
		Random random = new Random();
		for(int i = 0; i < 7; i++)
		{
			int randnum = random.nextInt(args.length());
			char temp = args.charAt(randnum);
			cookie += temp;
		}
		return cookie;
	}

	/* 用于测试的主函数.
	public static void main(String args[])
	{
		GetCookies dbg = new GetCookies();
		Scanner gets;
		gets = new Scanner(System.in);
		String test = gets.nextLine();
		System.out.println(dbg.getCookies(test));
	}*/
};
