package school.sptech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Envia mensagens formatadas para o Slack usando JSON "rich blocks".
 */
public class SlackNotifier {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotifier.class);
    private static final String webhookUrl = System.getenv("SLACK_WEBHOOK_URL");

    /**
     *
     * @param emoji   Emoji para destaque (ex: "🔴", "⚠️", "✅")
     * @param titulo  Título curto da notificação
     * @param detalhes  Texto explicativo (pode conter múltiplas linhas)
     * @param corHex  Cor lateral (ex: "#FF0000" para erros)
     */
    public static void sendRichMessage(String emoji, String titulo, String detalhes, String corHex) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            logger.warn("⚠️ Slack Webhook URL não configurada. Mensagem não enviada.");
            return;
        }

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // Slack attachments + blocks → mais bonito visualmente
            String payload = String.format("""
                {
                  "attachments": [
                    {
                      "color": "%s",
                      "blocks": [
                        {
                          "type": "header",
                          "text": {
                            "type": "plain_text",
                            "text": "%s %s"
                          }
                        },
                        {
                          "type": "section",
                          "text": {
                            "type": "mrkdwn",
                            "text": "*%s*\\n%s"
                          }
                        },
                        {
                          "type": "context",
                          "elements": [
                            {
                              "type": "mrkdwn",
                              "text": "_Enviado em %s_"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
                """,
                    corHex,
                    emoji, titulo,
                    titulo,
                    detalhes.replace("\"", "\\\""),
                    java.time.LocalDateTime.now()
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.warn("⚠️ Slack retornou HTTP {} ao enviar mensagem.", responseCode);
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem ao Slack: {}", e.getMessage());
        }
    }
}
