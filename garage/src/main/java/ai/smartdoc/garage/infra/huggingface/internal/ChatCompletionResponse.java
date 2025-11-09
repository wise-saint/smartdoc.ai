package ai.smartdoc.garage.infra.huggingface.internal;

import lombok.Data;

import java.util.List;

@Data
class ChatCompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Long index;
        private Message message;

        @Data
        public static class Message {
            private String content;
        }
    }
}