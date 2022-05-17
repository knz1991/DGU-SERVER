package com.dgu.server.service.impl;

import com.dgu.server.model.entities.User;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ForgotPwdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ForgotPwdServiceImpl implements ForgotPwdService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender mailSender;

    public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
        User user;
        if (userRepository.findByEmail(email).isPresent()) {
            user = userRepository.findByEmail(email).get();
            user.setResetpasswordtoken(token);
            userRepository.save(user);
        } else throw new UsernameNotFoundException("User Not Found with email: " + email);
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetpasswordtoken(token);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetpasswordtoken(null);
        userRepository.save(user);
    }

    public void sendEmail(String token, String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dgu.neoxam@gmail.com", "DGU NeoXam");
        helper.setTo(recipientEmail);
        String[] splitEmail = recipientEmail.split("[@._]");
        String subject = "Password reset request";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "    <!-- Facebook sharing information tags -->\n" +
                "\t<meta property=\"og:title\" content=\"%%subject%%\" />\n" +
                "\n" +
                "    <title>%%subject%%</title>\n" +
                "</head>\n" +
                "\n" +
                "<body bgcolor=\"#FFFFFF\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; height: 100% !important; width: 100% !important; background-color: #FFFFFF; margin: 0; padding: 0;\">\n" +
                "<style type=\"text/css\">#outlook a {\n" +
                "          padding: 0;\n" +
                "      }\n" +
                "      .body{\n" +
                "          width: 100% !important;\n" +
                "          -webkit-text-size-adjust: 100%;\n" +
                "          -ms-text-size-adjust: 100%;\n" +
                "          margin: 0;\n" +
                "          padding: 0;\n" +
                "      }\n" +
                "      .ExternalClass {\n" +
                "          width:100%;\n" +
                "      }\n" +
                "      .ExternalClass,\n" +
                "      .ExternalClass p,\n" +
                "      .ExternalClass span,\n" +
                "      .ExternalClass font,\n" +
                "      .ExternalClass td,\n" +
                "      .ExternalClass div {\n" +
                "          line-height: 100%;\n" +
                "      }\n" +
                "      img {\n" +
                "          outline: none;\n" +
                "          text-decoration: none;\n" +
                "          -ms-interpolation-mode: bicubic;\n" +
                "      }\n" +
                "      a img {\n" +
                "          border: none;\n" +
                "      }\n" +
                "      p {\n" +
                "          margin: 1em 0;\n" +
                "      }\n" +
                "      table td {\n" +
                "          border-collapse: collapse;\n" +
                "      }\n" +
                "      \n" +
                "      blockquote .original-only, .WordSection1 .original-only {\n" +
                "        display: none !important;\n" +
                "      }\n" +
                "\n" +
                "      @media only screen and (max-width: 480px){\n" +
                "        body, table, td, p, a, li, blockquote{-webkit-text-size-adjust:none !important;} \n" +
                "                body{width:100% !important; min-width:100% !important;} \n " +
                "\n" +
                "        #bodyCell{padding:10px !important;}\n" +
                "\n" +
                "        #templateContainer{\n" +
                "          max-width:600px !important;\n" +
                "          width:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        h1{\n" +
                "          font-size:24px !important;\n" +
                "          line-height:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        h2{\n" +
                "          font-size:20px !important;\n" +
                "          line-height:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        h3{\n" +
                "          font-size:18px !important;\n" +
                "          line-height:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        h4{\n" +
                "          font-size:16px !important;\n" +
                "          line-height:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        #templatePreheader{display:none !important;} \n" +
                "\n" +
                "        #headerImage{\n" +
                "          height:auto !important;\n" +
                "          max-width:600px !important;\n" +
                "          width:100% !important;\n" +
                "        }\n" +
                "\n" +
                "        .headerContent{\n" +
                "          font-size:20px !important;\n" +
                "          line-height:125% !important;\n" +
                "        }\n" +
                "\n" +
                "        .bodyContent{\n" +
                "          font-size:18px !important;\n" +
                "          line-height:125% !important;\n" +
                "        }\n" +
                "\n" +
                "        .footerContent{\n" +
                "          font-size:14px !important;\n" +
                "          line-height:115% !important;\n" +
                "        }\n" +
                "\n" +
                "        .footerContent a{display:block !important;} \n" +
                "      }\n" +
                "</style>\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"bodyTable\" style=\"-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFFFFF; border-collapse: collapse !important; height: 100% !important; margin: 0; mso-table-lspace: 0pt; mso-table-rspace: 0pt; padding: 0; width: 100% !important\" width=\"100%\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td align=\"center\" id=\"bodyCell\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; height: 100% !important; width: 100% !important; padding: 20px;\" valign=\"top\"><!-- BEGIN TEMPLATE // -->\n" +
                "\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"templateContainer\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-collapse: collapse !important; width: 600px; border: 1px solid #dddddd;\">\n" +
                "\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<td align=\"center\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" valign=\"top\"><!-- BEGIN PREHEADER // -->\n" +
                "\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"templatePreheader\" style=\"-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFFFFF; border-bottom-color: #CCCCCC; border-bottom-style: solid; border-bottom-width: 1px; border-collapse: collapse !important; mso-table-lspace: 0pt; mso-table-rspace: 0pt\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"preheaderContent\" pardot-region=\"preheader_content00\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #808080; font-family: Helvetica; font-size: 10px; line-height: 12.5px; text-align: left; padding: 10px 20px;\" valign=\"top\">EMAILING &nbsp;| NeoXam 2018</td>\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"preheaderContent\" pardot-region=\"preheader_content01\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #808080; font-family: Helvetica; font-size: 10px; line-height: 12.5px; text-align: left; padding: 10px 20px 10px 0;\" valign=\"top\" width=\"180\">Email not displaying correctly?<br>\n" +
                "\t\t\t\t\t\t\t\t\t<a href=\"%%view_online%%\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; color: #606060; font-weight: normal; text-decoration: underline;\" target=\"_blank\">View it in your browser</a>.</td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t<!-- // END PREHEADER --></td>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<td align=\"center\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" valign=\"top\"><!-- BEGIN HEADER // -->\n" +
                "\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"templateHeader\" style=\"-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFFFFF; border-bottom-color: #CCCCCC; border-bottom-style: solid; border-bottom-width: 1px; border-collapse: collapse !important; mso-table-lspace: 0pt; mso-table-rspace: 0pt\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t<tr pardot-repeatable=\"\" class=\"\">\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"headerContent\" pardot-region=\"header_image\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #505050; font-family: Helvetica; font-size: 20px; font-weight: bold; line-height: 20px; text-align: left; vertical-align: middle; padding: 0;\" valign=\"top\"><img alt=\"\" border=\"0\" height=\"160\" id=\"headerImage\" src=\"http://go.pardot.com/l/99632/2018-05-31/4xpqs5/99632/69008/Emailing_Template_Gen_2018_600px_v1.png\" style=\"max-width: 600px; outline: none; text-decoration: none; border-width: 0px; border-style: solid; width: 600px; height: 160px;\" width=\"600\"></td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t<!-- // END HEADER --></td>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<td align=\"center\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" valign=\"top\"><!-- BEGIN BODY // -->\n" +
                "\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"templateBody\" style=\"-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFFFFF; border-bottom-color: #CCCCCC; border-bottom-style: solid; border-bottom-width: 1px; border-collapse: collapse !important; border-top-color: #FFFFFF; border-top-style: solid; border-top-width: 1px; mso-table-lspace: 0pt; mso-table-rspace: 0pt\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t<tr pardot-repeatable=\"\" class=\"\">\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"bodyContent\" pardot-data=\"\" pardot-region=\"body_content\" style=\"color: rgb(146, 145, 145); font-family: &quot;Century Gothic&quot;, Helvetica; font-size: 14px; line-height: 18px; text-align: left; padding: 20px;\" valign=\"top\">\n" +
                "\t\t\t\t\t\t\t\t\t<h1 style=\"color: #0ed8b8 !important; display: block; font-family: 'Century Gothic', Helvetica;  font-size: 35px; font-style: normal; font-weight: bold; letter-spacing: normal; line-height: 40px; margin: 0; padding-bottom:5px; text-align: left\">DGU NeoXam</h1>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t<h3 style=\"color: #0ed8b8 !important; display: block; font-family: 'Century Gothic', Helvetica;  font-size: 16px; font-style: normal; letter-spacing: normal; line-height: 24px; margin: 0; text-align: left\">"+ date +"</h3>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t<h5 style=\"color: #22211f !important; display: block; font-family: 'Century Gothic', Helvetica;  font-size: 24px; font-style: normal; font-weight: bold; letter-spacing: normal; line-height: 32px; margin: 0; padding-bottom:20px; padding-top:20px;text-align: left\">Reset Password</h5>\n" +
                "\t\t\t\t\t\t\t\t\tDear <strong>"+ splitEmail[0] +" "+ splitEmail[1] +"</strong>,<br>\n" +
                "\t\t\t\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t\t\t\t\tYou have requested a password change, to continue copy the code below and use it to verify you identity\n" +
                "\t\t\t\t\t\t\t\t\t<br><br>\n" +
                "\t\t\t\t\t\t\t\t\t<strong> "+ token +"</strong><br>\n" +
                "\t\t\t\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t\t\t\t<strong><span style=\"color: #0ed8b8;\"> If you haven't request a password reset please ignore this email.</span></strong><br>\n" +
                "\t\t\t\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t\t\t\tBest regards,<br>\n" +
                "\t\t\t\t\t\t\t\t\t<br>\n" +
                "\t\t\t\t\t\t\t\t\t<strong><span style=\"color: #22211f;\">Dgu-NeoXam Support</span></strong><br>\n" +
                "\t\t\t\t\t\t\t\t\tJob title: EmailBot<br><br>\n" +
                "\t\t\t\t\t\t\t\t\t<h6 style=\"color: #0ed8b8 !important; display: block; font-family: 'Century Gothic', Helvetica;  font-size: 18px; font-style: normal; font-weight: bold; letter-spacing: normal; line-height: 20px; margin: 0;  padding-bottom:5px; padding-top:20px;text-align: left\">CONTACTS</h6>\n" +
                "\t\t\t\t\t\t\t\t\t<a href=\"mailto:events@neoxam.com\" style=\"text-decoration: underline; color:#929191;\">events@neoxam.com</a><br>\n" +
                "                      website: <a href=\"mailto:events@neoxam.com\" style=\"text-decoration: underline; color:#929191;\">www.neoxam.com</a>\n" +
                "\t\t\t\t\t\t\t\t\t    \n" +
                "\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t<!-- // END BODY --></td>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t<td align=\"center\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" valign=\"top\"><!-- BEGIN FOOTER // -->\n" +
                "\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"templateFooter\" style=\"-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; background-color: #FFFFFF; border-collapse: collapse !important; border-top-color: #0ed8b8; border-top-style: solid; border-top-width: 2px; mso-table-lspace: 0pt; mso-table-rspace: 0pt\" width=\"100%\">\n" +
                "\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t<tr pardot-removable=\"\" class=\"\">\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"center\" class=\"footerContent\" pardot-region=\"footer_content00\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #808080; font-family: 'Century Gothic',Helvetica; font-size: 10px; line-height: 15px; text-align: center; padding: 20px;\" valign=\"top\"><a href=\"https://www.linkedin.com/company/neoxam\" style=\"border:none;background:none;\" target=\"_blank\"><img alt=\"Ico-LinkedIn\" border=\"0\" height=\"60\" src=\"http://go.pardot.com/l/99632/2018-05-31/4xpqsh/99632/69018/Ico_B_LinkedIn.png\" style=\"text-decoration: none; display: inline-block; border: 0px solid; background-image: none; width: 60px; height: 60px;\" width=\"60\"></a> &nbsp;<a href=\"https://twitter.com/neoxamsoftware\" style=\"border:none;background:none;\" target=\"_blank\"><img alt=\"Ico-Twitter\" border=\"0\" height=\"60\" src=\"http://go.pardot.com/l/99632/2018-05-31/4xpqsf/99632/69016/Ico_B_Twitter.png\" style=\"text-decoration: none; display: inline-block; border: 0px solid; background-image: none; width: 60px; height: 60px;\" width=\"60\"></a> &nbsp;<a href=\"https://www.youtube.com/channel/UCkXfTkZerN3TSlunbKQCgzg\" style=\"border:none;background:none;\" target=\"_blank\"><img alt=\"Ico-YouTube\" border=\"0\" height=\"60\" src=\"http://go.pardot.com/l/99632/2018-05-31/4xpqsm/99632/69022/Ico_B_YouTube.png\" style=\"text-decoration: none; display: inline-block; border: 0px solid; background-image: none; width: 60px; height: 60px;\" width=\"60\"></a> &nbsp;<a href=\"https://www.facebook.com/neoxam/\" style=\"border:none;background:none;\" target=\"_blank\"><img alt=\"Ico-Facebook\" border=\"0\" dir=\"\" height=\"60\" src=\"http://go.pardot.com/l/99632/2018-05-31/4xpqs7/99632/69010/Ico_B_Facebook.png\" style=\"text-decoration: none; display: inline-block; border: 0px solid; background-image: none; width: 60px; height: 60px;\" width=\"60\"></a> &nbsp;</td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"footerContent\" pardot-region=\"footer_content01\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #808080; font-family: 'Century Gothic',Helvetica; font-size: 11px; line-height: 12px; text-align: left; padding: 0 20px 20px;\" valign=\"top\">© 2022 NEOXAM • Trademark information: NeoXam and the NeoXam logo are trademarks of NeoXam. All other trade names are trademarks or registered trademarks of their respective holders.</td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t<td align=\"left\" class=\"footerContent original-only\" pardot-region=\"footer_content02\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #808080; font-family: 'Century Gothic',Helvetica; font-size: 11px; line-height: 12px; text-align: left; padding: 10px 20px 20px;\" valign=\"top\"><a href=\"%%unsubscribe%%\" style=\"-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; color: #606060; font-weight: normal; text-decoration: underline;\">unsubscribe from all emails</a></td>\n" +
                "\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t<!-- // END FOOTER --></td>\n" +
                "\t\t\t\t\t</tr>\n" +
                "\t\t\t\t</tbody>\n" +
                "\t\t\t</table>\n" +
                "\t\t\t<!-- // END TEMPLATE --></td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<br>\n" +
                "<!--\n" +
                "          This email was originally designed by the wonderful folks at MailChimp and remixed by Pardot.\n" +
                "          It is licensed under CC BY-SA 3.0\n" +
                "        -->\n" +
                "</body>\n" +
                "</html>"
                ;

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
        // Send the email
    }
}
