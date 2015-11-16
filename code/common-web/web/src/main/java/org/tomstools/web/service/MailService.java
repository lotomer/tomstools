/**
 * 
 */
package org.tomstools.web.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * @author Administrator
 *
 */
@Controller
public class MailService {
    private static final Log LOG = LogFactory.getLog(MailService.class);
	@Autowired
	private JavaMailSenderImpl javaMailSender;
	public void sendMail(String title,String content,String to){
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setDefaultEncoding("UTF-8");
//        mailSender.setHost(mailHost);
//        mailSender.setPassword(mailPassword);
//        mailSender.setUsername(mailUser);
//        mailSender.setPort(port);
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", mailAuth);
//        properties.put("mail.smtp.starttls.enable", mailStarttls);
//        properties.put("mail.smtp.socketFactory.class", mailSocketFactory);
//        properties.put("mail.debug", mailDebug);
//        mailSender.setJavaMailProperties(properties);
	    if (!StringUtils.isEmpty(javaMailSender.getHost())){
	        SimpleMailMessage mailMessage = new SimpleMailMessage(); 
	        mailMessage.setTo(to);
	        mailMessage.setSubject(title);
	        mailMessage.setText(content);
	        javaMailSender.send(mailMessage);
	    }else{
	        LOG.warn("Need config the mail host!");
	    }
        
	}
	public static void main(String[] args) {
		MailService m = new MailService();
		m.sendMail("测试标题", "我这个是测试用的", "lotomer@163.com");
		m.sendMail("测试标题", "我这个是测试用的", "32455321@qq.com");
		m.sendMail("测试标题", "我这个是测试用的", "longcm@asiainfo.com");
	}
}
