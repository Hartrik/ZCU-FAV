package cz.hartrik.pia.service

import cz.hartrik.pia.model.Account
import cz.hartrik.pia.model.User
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig
import org.xhtmlrenderer.pdf.ITextRenderer

import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import java.time.ZonedDateTime

/**
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Service
class UserNotificationServiceImpl implements UserNotificationService {

    private static final def LOGGER = LoggerFactory.getLogger(UserNotificationServiceImpl)

    @Autowired
    private FreeMarkerConfig freeMarkerConfig

    @Autowired
    private AccountManager accountManager

    @Override
    void sendWelcome(User newUser, String rawPassword) {
        // for testing purposes
        LOGGER.info("User created: login=${newUser.login} password=${rawPassword}")

        def message = formatMessage("email-welcome.ftl",
                newUser.properties + [rawPassword: rawPassword])

        sendMail(newUser.email, "JavaBank - account created", message, null)
    }

    @Override
    void sendStatement(User user, int accountId, ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        Account account = null
        def transactions = accountManager.authorize(user) {
            account = findAccountById(accountId)
            findAllTransactionsByAccount(account, dateFrom, dateTo)
        }

        def properties = user.properties + account.properties + [
                transactions: transactions,
                from: dateFrom,
                to: dateTo,
                turnover: transactions.inject(BigDecimal.ZERO, {
                    a, b -> a + (b.sender?.id == account.id ? b.amountSent : b.amountReceived)
                }),
                change: transactions.inject(BigDecimal.ZERO, {
                    a, b -> a + (b.sender?.id == account.id ? -b.amountSent : b.amountReceived)
                })
        ]
        def message = formatMessage("email-account-statement.ftl", properties)

        sendMail(user.email, "JavaBank - account statement", message, "statement.pdf")
    }

    private void sendMail(String email, String subject, String htmlContent, String pdfName) {
        Properties props = new Properties()
        System.getenv()
                .findAll { it.key.startsWith('mail_smtp_') }
                .findAll { !it.key.endsWithAny('_user', "_pass") }
                .each { props.put(it.key.replace('_', '.'), it.value) }

        if (props.isEmpty()) {
            LOGGER.error('SMTP server not configured!')
            return
        }
        LOGGER.info("Sending email to $email, config: $props")

        def from = System.getenv("mail_smtp_user")
        def pass = System.getenv("mail_smtp_pass")

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass)
            }
        })

        Message message = new MimeMessage(session)
        message.setFrom(new InternetAddress(from))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
        message.setSubject(subject)

        if (pdfName) {
            // include pdf

            def out = new ByteArrayOutputStream()
            ITextRenderer renderer = new ITextRenderer()
            renderer.setDocumentFromString(htmlContent)
            renderer.layout()
            renderer.createPDF(out)

            Multipart multipartMessage = new MimeMultipart()
            message.setContent(multipartMessage, "multipart/mixed")

            MimeBodyPart htmlPart = new MimeBodyPart()
            htmlPart.setContent(htmlContent, "text/html")
            multipartMessage.addBodyPart(htmlPart)

            MimeBodyPart pdfPart = new MimeBodyPart()
            pdfPart.setDataHandler(new DataHandler(new ByteArrayDataSource(
                    out.toByteArray(), "application/pdf")))
            pdfPart.setFileName(pdfName)
            multipartMessage.addBodyPart(pdfPart)
        } else {
            message.setContent(htmlContent, "text/html")
        }

        Transport.send(message)

        LOGGER.info("Email sent successfully")
    }

    private String formatMessage(String templateName, Map parameters) {
        Configuration cfg = new Configuration()
        cfg.setClassForTemplateLoading(getClass(), "/cz/hartrik/pia/service/")
        cfg.setDefaultEncoding("UTF-8")
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)

        Template template = cfg.getTemplate(templateName)

        def writer = new StringWriter()
        template.process(parameters, writer)
        writer.toString()
    }

}
