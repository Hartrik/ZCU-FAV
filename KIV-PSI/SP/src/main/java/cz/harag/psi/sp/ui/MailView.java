package cz.harag.psi.sp.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.commons.text.StringEscapeUtils;

/**
 * @author Patrik Harag
 * @version 2020-05-24
 */
public class MailView {

    private final WebView contentView;
    private final Label subject;
    private final Label from;

    public MailView() {
        this.contentView = new WebView();
        this.from = new Label();
        this.subject = new Label();
    }

    public void show(MimeMessageParser parser) throws Exception {
        subject.setText(parser.getSubject());
        from.setText(parser.getFrom());

        String content;
        if (parser.hasHtmlContent()) {
            content = parser.getHtmlContent();
        } else {
            content = "<pre>" + StringEscapeUtils.escapeHtml4(parser.getPlainContent()) + "</pre>";
        }
        contentView.getEngine().loadContent(content);
    }

    public void clean() {
        contentView.getEngine().loadContent("");
        from.setText(null);
        subject.setText(null);
    }

    public Node getNode() {
        Label fromLabel = new Label("From: ");
        fromLabel.setStyle("-fx-font-weight: bold;");
        Label subjectLabel = new Label("Subject: ");
        subjectLabel.setStyle("-fx-font-weight: bold;");

        VBox headerBox = new VBox(5,
                new HBox(5, fromLabel, from),
                new HBox(5, subjectLabel, subject)
        );
        headerBox.setPadding(new Insets(10));

        return new VBox(
                headerBox,
                contentView
        );
    }

}
