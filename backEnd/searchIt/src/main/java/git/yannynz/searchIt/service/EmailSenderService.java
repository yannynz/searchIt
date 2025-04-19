package git.yannynz.searchIt.service;

public interface EmailSenderService {
   void sendEmail(String to, String subject, String body);
}

