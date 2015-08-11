package org.tomstools.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestLog4j {
	
	private static Logger logger=Logger.getLogger(TestLog4j.class);
	
	
	private static String PATH="/home/wangyf/my.log";
	
	private static char[] spilt={0x00,0x3F};
	
	private static java.util.List<String> list=new ArrayList<String>();
	
	//@BeforeClass
	public static void setUp(){
		File file=new File(PATH);
		BufferedReader read=null;
		try{
			read=new BufferedReader(new FileReader(file));
			String message=null;
			while((message=read.readLine())!=null){
				list.add(message);
			}
		}catch(IOException k){
			
		}finally{
			try{
				read.close();
			}catch(IOException k){
				
			}
		}
	}
	
 
	@Test
	public void testLog(){
		TestVo vo=new TestVo("abc2","efg4",100);
		TestVo vo1=new TestVo("ab1cR","1efgE",200);
		TestVo vo2=new TestVo("ab2c3","2efgB",300);
		TestVo vo3=new TestVo("ab3cF","3efg3",400);
		
		logger.info(vo.toString());
		logger.info(vo1.toString());
		logger.info(vo2.toString());
		logger.info(vo3.toString());
	}
	
	//@Test
	public void testSpilt(){
		Assert.assertEquals(list.size(), 4);
		for(String f:list){
			String[] k=f.split(new String(spilt));//StringUtils.split(f, spilt);
			//Assert.assertEquals(k.length, 3);
			System.out.println(k[0]);
		}
		
	}
	
	
	
	private class TestVo{
		private String messageId;
		
		private String value;
		
		private Integer price;
		
		public TestVo(String messageId, String value, Integer price) {
			super();
			this.messageId = messageId;
			this.value = value;
			this.price = price;
		}

		public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Integer getPrice() {
			return price;
		}

		public void setPrice(Integer price) {
			this.price = price;
		}

		@Override
		public String toString() {
			StringBuilder builer=new StringBuilder();
			builer.append(messageId).append(spilt).append(value).append(spilt).append(price);
			return builer.toString();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		StringBuilder builer=new StringBuilder();
		char[] k={0x9f};
		builer.append("messageId").append(k).append("value").append(k).append(100);
		String yString=builer.toString();
		logger.info(yString);
		System.out.println(yString);
		String[] ff=yString.split(new String(k));//StringUtils.split(yString,k);
		System.out.println(ff[0]);
	}
	
}
