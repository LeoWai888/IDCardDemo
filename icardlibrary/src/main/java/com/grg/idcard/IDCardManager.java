package com.grg.idcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;


import com.decard.NDKMethod.BasicOper;
import com.decard.entitys.IDCard;

import java.util.regex.Pattern;


/**
 * 
 *身份证管理类
 * 
 *@author zjxin2 on 2016-03-24
 *@version  
 *
 */
public class IDCardManager {
	private static final String TAG = "IDCardManager";

	private static boolean isConnected = false;

	public static final String COM_IDCARD = "/dev/ttyS1";

	public static int BAUTERATE_IDCARD = 115200;


	public static final String COM_PRINTER= "/dev/ttyS3";

	public static int BAUTERATE_PRINTER = 9600;
	
	public IDCardManager() {
		
	}


	/**
	 * 连接身份模块
	 * @param context
	 * @return
	 */
	public boolean connectModel(Context context) {
		int rst = BasicOper.dc_open("COM", context, COM_IDCARD, BAUTERATE_IDCARD);
		if(rst > 0) {
			BasicOper.dc_beep(5);
			isConnected = true;
			return true;
		} else {
			isConnected = false;
			return false;
		}
	}
	
	/**
	 * 断开身份证模块连接
	 */
	public void disconnect() {
		BasicOper.dc_exit();
		isConnected = false;
	}
	
	/**
	 * Get Identify Card base message
	 * 
	 * @return
	 * @throws IDCardException
	 */
	@SuppressWarnings("deprecation")
	public IDCardMsg getIDCardMsg(Context context) {
		if(!isConnected) {
			connectModel(context);
		}
		IDCard idCard = BasicOper.dc_SamAReadCardInfo(1);
		if (idCard != null){
			IDCardMsg msg = new IDCardMsg();
			msg.setName(idCard.getName());
			if ("男".equals(idCard.getSex())){
				msg.setSex(1);
			}else {
				msg.setSex(2);
			}
			msg.setNation(idCard.getNation());
			String temp = idCard.getBirthday();
			msg.setBirthDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
					Integer.parseInt(temp.substring(4, 6)),
					Integer.parseInt(temp.substring(6, 8))));
			msg.setAddress(idCard.getAddress());
			msg.setIdCardNum(idCard.getId());
			msg.setSignOffice(idCard.getOffice());
			temp = idCard.getEndTime();
			if (temp.contains("长期")){
				msg.setNoEndDate(true);
			}else {
				msg.setUsefulStartDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
						Integer.parseInt(temp.substring(4, 6)),
						Integer.parseInt(temp.substring(6, 8))));
				temp = idCard.getEndTime();
				msg.setUsefulEndDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
						Integer.parseInt(temp.substring(4, 6)),
						Integer.parseInt(temp.substring(6, 8))));
			}

			msg.setPortrait(BitmapFactory.decodeByteArray(idCard.getPhotoData(), 0, idCard.getPhotoData().length));
			return msg;
		}

		return null;

		/*String rawStr = BasicOper.dc_get_i_d_raw_string();
		if(rawStr.startsWith("0000")) {
			((CollectActivity)context).openCamera();
			String[] raws = rawStr.split("\\|", -1);
//            IDCard idCard = new IDCard();
//            idCard.setName(raws[1]);
//            idCard.setSex(raws[2]);
//            idCard.setNation(raws[3]);
//            idCard.setBirthday(raws[4]);
//            idCard.setAddress(raws[5]);
//            idCard.setId(raws[6]);
//            idCard.setOffice(raws[7]);
//            idCard.setStartTime(raws[8]);
//            idCard.setEndTime(raws[9]);
//            idCard.setPhotoDataHexStr(raws[10]);
            
            IDCardMsg msg = new IDCardMsg();
            strs2Msg(raws, msg);
            
            raws = null;
            rawStr = null;*/
            
           /* return null;
		} else {
			return null;
		}*/

	}
	
	
	private void strs2Msg(String[] strs, IDCardMsg msg) {
		String temp = strs[1].trim();
		msg.setName(temp);
		
		temp = strs[2].trim();
		msg.setSex(temp.equals("男") ? 1 : 2);
		
		temp = strs[3].trim();
		msg.setNation(temp);
		
		temp = strs[4].trim();
		if(isDigit(temp) && temp.length() == 8) {
			msg.setBirthDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
					Integer.parseInt(temp.substring(4, 6)),
					Integer.parseInt(temp.substring(6, 8))));
		}
		
		temp = strs[5].trim();
		msg.setAddress(temp.trim());
		
		temp = strs[6].trim();
		msg.setIdCardNum(temp.trim());
		
		temp = strs[7].trim();
		msg.setSignOffice(temp.trim());
		
		temp = strs[8].trim();
		if(isDigit(temp) && temp.length() == 8) {
			msg.setUsefulStartDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
					Integer.parseInt(temp.substring(4, 6)),
					Integer.parseInt(temp.substring(6, 8))));
		}
		
		temp = strs[9].trim();
		if(isDigit(temp) && temp.length() == 8) {
			msg.setUsefulEndDate(new IDCardDate(Integer.parseInt(temp.substring(0, 4)),
					Integer.parseInt(temp.substring(4, 6)),
					Integer.parseInt(temp.substring(6, 8))));
		} else {
			msg.setNoEndDate(true);
		}
		
		temp = strs[10].trim();
		byte[] portrait = hexString2Bytes(temp);
		if(portrait != null) {
			msg.setPortrait(BitmapFactory.decodeByteArray(portrait, 0, portrait.length));
		}
		
		portrait = null;
		temp = null;
	}
	
	 /**
     * hexString转byteArr
     * <p>例如：</p>
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    @SuppressLint("DefaultLocale")
	private byte[] hexString2Bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
        	return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        
        hexBytes = null;
        
        return ret;
    }
    
    /**
     * hexChar转int
     *
     * @param hexChar hex单个字节
     * @return 0..15
     */
    private int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }
	
	/**
	 * 判断字符串是否为数字字符串
	 * 
	 * @param str
	 * @return
	 */
	public boolean isDigit(String str) {
		if (str.equals("")) {
			return false;
		}
		// 通过正则表达式来匹配
		Pattern pattern = Pattern.compile("[0-9]*");
		boolean result = pattern.matcher(str).matches();
		pattern = null;
		return result;
	}


	private void decodeByte(byte[] msg, char[] msg_str) throws Exception {
		byte[] newmsg = new byte[msg.length + 2];

		newmsg[0] = (byte) 0xff;
		newmsg[1] = (byte) 0xfe;

		for (int i = 0; i < msg.length; i++) {
			newmsg[i + 2] = msg[i];
		}

		String s = new String(newmsg, "UTF-16");
		for (int i = 0; i < s.toCharArray().length; i++) {
			msg_str[i] = s.toCharArray()[i];
		}
		
		newmsg = null;
	}



}
